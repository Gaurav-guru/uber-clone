import {AfterViewInit, Component, Input, OnDestroy, OnInit} from '@angular/core';
import * as L from 'leaflet';
import {LatLng, latLng, LatLngTuple, marker} from 'leaflet';
import 'leaflet-routing-machine';
import {Location} from "../model/Location";
import {BehaviorSubject, Subject} from "rxjs";
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {MapService} from "./map.service";
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {VehicleWithOnlyLocation} from "../model/VehicleWithOnlyLocation";
import {Ride} from "../model/Ride";
import {DriverService} from "../../driver/driver.service";
import {Vehicle} from "../model/Vehicle";
import {PassengerService} from "../../passenger/passenger.service";
import {RideCreated} from "../model/RideCreated";
import {UserService} from "../user.service";

const reverseGeocodeUrl = "https://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/reverseGeocode?location=";

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css', '../../app.component.css']
})

export class MapComponent implements AfterViewInit, OnDestroy, OnInit {

  constructor(private http: HttpClient,
              public mapService: MapService,
              private driverService: DriverService,
              private passengerService: PassengerService,
              private userService: UserService) {
  }

  private stompClient!: Stomp.Client;
  vehicles: { [key: number]: L.Marker } = {};


  private simulation?: NodeJS.Timer;
  private otherSimulation?: NodeJS.Timer;

  private driverRideInProgress = false;
  private map!: L.Map;
  private routeLayer!: L.LayerGroup;
  private freeCars: LatLngTuple[] = [[45.237002, 19.829361], [45.240477, 19.847849], [45.244125, 19.842828]];
  private carMarker!: L.Marker;
  private freeCarMarkers: L.Marker[] = [];
  private route!: L.Routing.Control;
  private toDepartureRoute!: L.Routing.Control;
  private departurePlain = Location.getEmptyLocation();
  private destinationPlain = Location.getEmptyLocation();
  private enableClicking = true;
  private isDriver = false;
  @Input() displayCar = false;
  @Input() departure: BehaviorSubject<Location> = new BehaviorSubject<Location>(Location.getEmptyLocation());
  @Input() destination: BehaviorSubject<Location> = new BehaviorSubject<Location>(Location.getEmptyLocation());
  @Input() passenger: Subject<boolean> = new Subject<boolean>();

  private isTrackingDriver = false;

  private locationIcon = L.icon({
    iconUrl: 'assets/images/place-marker.png',
    iconRetinaUrl: 'assets/images/place-marker.png',

    iconSize: [40, 40],
    iconAnchor: [20, 40],
  });

  private vehicleFreeIcon = L.icon({
    iconUrl: 'assets/images/greencar.png',
    iconRetinaUrl: 'assets/images/greencar.png',

    iconSize: [35, 45],
    iconAnchor: [18, 45],
  });

  private vehicleReservedIcon = L.icon({
    iconUrl: 'assets/images/redcar.png',
    iconRetinaUrl: 'assets/images/redcar.png',

    iconSize: [35, 45],
    iconAnchor: [18, 45],
  });

  private driverIcon = L.icon({
    iconUrl: 'assets/images/car.png',
    iconRetinaUrl: 'assets/images/car.png',

    iconSize: [35, 35],
    iconAnchor: [35, 35],
  })

  ngOnInit(): void {
    this.initializeWebSocketConnection();
    this.mapService.getAllActiveRides().subscribe((ret: Ride[]) => {
      for (const ride of ret) {
        const markerLayer = marker([ride.vehicle.longitude, ride.vehicle.latitude], {
          icon: this.vehicleReservedIcon,
        });
        markerLayer.addTo(this.map);
        this.vehicles[ride.vehicle.id] = markerLayer;
      }
    });
  }

  initializeWebSocketConnection() {
    const ws = new SockJS('http://localhost:8080/socket');
    this.stompClient = Stomp.over(ws);
    this.stompClient.debug = f => f;
    this.stompClient.connect({}, () => {
      this.openGlobalSocket();
    });
  }

  openGlobalSocket() {
    this.stompClient.subscribe('/map-updates/update-vehicle-position', (message: { body: string }) => {
      if (!this.driverRideInProgress) {
        const vehicle: VehicleWithOnlyLocation = JSON.parse(message.body);
        const existingVehicle = this.vehicles[vehicle.id];
        if (existingVehicle) {
          existingVehicle.setLatLng([vehicle.longitude, vehicle.latitude]);
        }
      }
    });
    this.stompClient.subscribe('/map-updates/new-ride', (message: { body: string }) => {
      if (!this.driverRideInProgress) {
        const ride: Ride = JSON.parse(message.body);
        const markerLayer = marker([ride.vehicle.longitude, ride.vehicle.latitude], {
          icon: this.vehicleReservedIcon,
        });
        markerLayer.addTo(this.map);
        this.vehicles[ride.vehicle.id] = markerLayer;
      }
    });
    this.stompClient.subscribe('/map-updates/ended-ride', (message: { body: string }) => {
      const ride: Ride = JSON.parse(message.body);
      if (this.vehicles[ride.vehicle.id]) {
        this.vehicles[ride.vehicle.id].remove();
      }
      delete this.vehicles[ride.vehicle.id];
    });
  }

  private initMap(): void {
    const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      minZoom: 3,
    });
    this.map = L.map('map', {
      zoom: 14,
      center: latLng(18.5204, 73.8567),
      zoomControl: false,
      attributionControl: false
    });
    tiles.addTo(this.map);
    this.routeLayer = new L.LayerGroup();
    this.routeLayer.addTo(this.map);
  }

  private makeMarker(location: Location): L.Marker {
    return L.marker([location.latitude, location.longitude], {
      draggable: false,
    }).bindPopup(location.address).openPopup();
  }

  private removeRoutes(): void {
    if (this.route != null) {
      this.route.remove();
    }
    if (this.toDepartureRoute != null) {
      this.toDepartureRoute.remove();
    }
    this.departurePlain = Location.getEmptyLocation();
    this.destinationPlain = Location.getEmptyLocation();
    this.departure.next(Location.getEmptyLocation());
    this.mapService.setDeparture(Location.getEmptyLocation());
    this.mapService.setDestination(Location.getEmptyLocation());
    this.destination.next(Location.getEmptyLocation());
  }

  private drawRoute(departure: Location, destination: Location, driver = this.isDriver): void {
    if (this.route != null) {
      this.route.remove();
    }
    this.route = L.Routing.control({
      router: L.Routing.osrmv1({
        serviceUrl: `http://router.project-osrm.org/route/v1/`
      }),
      showAlternatives: false,
      lineOptions: {
        styles: [{color: '#CF0A0A', weight: 7}],
        extendToWaypoints: true,
        missingRouteTolerance: 100
      },
      fitSelectedRoutes: true,
      show: true,
      routeWhileDragging: true,
      plan: L.Routing.plan([
        this.makeMarker(departure).getLatLng(),
        this.makeMarker(destination).getLatLng()
      ], {
        createMarker: function (i: number, waypoint: L.Routing.Waypoint) {
          let text;
          if (i == 0) text = departure.address;
          else text = destination.address;
          return L.marker(waypoint.latLng, {
            draggable: false,
          }).bindPopup(text).openPopup();
        },
        draggableWaypoints: false
      }),
      waypoints: [
        this.makeMarker(departure).getLatLng(),
        this.makeMarker(destination).getLatLng()
      ]
    }).addTo(this.map);

    this.route.on('routesfound', (e: any) => {
      this.mapService.setDepartureDestinationCoords(e.routes[0].coordinates);
    });

    if (this.isDriver) {
      this.toDepartureRoute = L.Routing.control({
        router: L.Routing.osrmv1({
          serviceUrl: `http://router.project-osrm.org/route/v1/`
        }),
        showAlternatives: false,
        lineOptions: {
          styles: [{color: '#0000FF', weight: 7}],
          extendToWaypoints: true,
          missingRouteTolerance: 100
        },
        fitSelectedRoutes: true,
        show: true,
        routeWhileDragging: true,
        plan: L.Routing.plan([
          this.carMarker.getLatLng(),
          this.makeMarker(departure).getLatLng()
        ], {
          createMarker: function (i: number, waypoint: L.Routing.Waypoint) {
            let text;
            if (i == 0) text = departure.address;
            else text = destination.address;
            return L.marker(waypoint.latLng, {
              draggable: false,
            }).bindPopup(text).openPopup();
          },
          draggableWaypoints: false
        }),
        waypoints: [
          this.carMarker.getLatLng(),
          this.makeMarker(departure).getLatLng()
        ]
      }).addTo(this.map);
      this.toDepartureRoute.on('routesfound', (e: any) => {
        this.mapService.setDriverDepartureCoords(e.routes[0].coordinates);
      });
    }
  }

  private initFreeCars(): void {
    for (const car of this.freeCars) {
      this.freeCarMarkers.push(
        L.marker(car, {
          draggable: false,
          icon: this.vehicleFreeIcon
        }).addTo(this.map));
    }
  }

  private clearCars(): void {
    for (const car of this.freeCarMarkers) {
      car.remove();
    }
  }

  private initReverseGeocoding(): void {
    this.map.on('click', (ev) => {
      if (this.enableClicking) {
        const latlng = this.map.mouseEventToLatLng(ev.originalEvent);
        const locationUrl = reverseGeocodeUrl + latlng.lng + "," + latlng.lat + "&f=pjson";
        this.http.get(locationUrl).subscribe((response: any) => {
          const clicked = new Location(response.address.LongLabel, latlng.lng, latlng.lat);
          this.makeMarker(clicked).addTo(this.routeLayer);
          if (this.departurePlain.address == '') {
            this.departurePlain = clicked;
            this.mapService.setDeparture(this.departurePlain);
          } else {
            this.destinationPlain = clicked;
            this.mapService.setDestination(this.destinationPlain);
            this.routeLayer.clearLayers();
            this.drawRoute(this.departurePlain, this.destinationPlain);
            this.mapService.setRideEstimates().then();
          }
        });
      }
    });
  }

  drawMarker(location: Location): void {
    if (this.isDriver) return;
    this.makeMarker(location).addTo(this.routeLayer);
    this.map.setView([location.latitude, location.longitude], 16);
  }

  private initDriverCar(): void {
    if (this.userService.getRole() == 'ROLE_DRIVER') {
      this.driverService.getVehicle().subscribe((vehicle: Vehicle) => {
        this.driverService.vehicleId = vehicle.id;
        this.carMarker = L.marker(new LatLng(vehicle.currentLocation.latitude, vehicle.currentLocation.longitude), {
          draggable: false,
          icon: this.driverIcon
        }).addTo(this.map);
      });
    } else {
      this.carMarker = L.marker(new LatLng(0, 0), {
        draggable: false,
        icon: this.driverIcon
      }).addTo(this.map);
    }
  }

  ngAfterViewInit(): void {
    L.Marker.prototype.options.icon = this.locationIcon;
    this.initMap();
    this.initFreeCars();
    if (this.displayCar) {
      this.initDriverCar();
    }

    this.mapService.rideInProgress$.subscribe(rideInProgress => {
      this.driverRideInProgress = rideInProgress;
      if (rideInProgress) {
        this.clearCars();
        for (const key of Object.keys(this.vehicles)) {
          const vehicle = this.vehicles[Number(key)];
          vehicle.remove();
        }
        this.vehicles = {};
      } else {
        this.initFreeCars();
        if (this.carMarker != null) {
          this.driverService.changeVehicleLocation(this.carMarker.getLatLng()).subscribe();
        }
        clearInterval(this.simulation);
        return;
      }
    });
    this.mapService.rideReceived$.subscribe(rideReceived => {
      if (!rideReceived) {
        this.removeRoutes();
      }
    })
    this.mapService.departure$.subscribe(departure => {
      this.departurePlain = departure;
      if (departure.address != '') this.drawMarker(departure)
    });

    this.mapService.isDriver$.subscribe(status => this.isDriver = status);

    this.mapService.destination$.subscribe(destination => {
      this.destinationPlain = destination;
      if (destination.address != '') this.drawMarker(destination);
    });

    this.mapService.clearMap$.subscribe(() => this.clear());

    this.mapService.mapRoutingOnly$.subscribe(status => {
      this.enableClicking = status;
      this.clear();
      this.initReverseGeocoding();
    });

    this.mapService.drawRouteRequest$.subscribe(() => {
      this.drawRouteBetweenSelectedLocations();
      this.mapService.setRideEstimates().then();
    });

    this.mapService.departureDestinationCoords$.subscribe((coords) => {
      this.mapService.destinationCoords = coords;
      if (this.mapService.simulateMovementToDestination) {
        this.simulateMovementToDestination(coords);
      }
    });
    this.mapService.driverDepartureCoords$.subscribe((coords) => {
      if (this.mapService.simulateMovementToDeparture) {
        this.simulateMovementToDeparture(coords);
      }
    });
    this.mapService.simulateToDestination$.subscribe((status) => {
      if (status && !this.mapService.startedSimulation) {
        this.mapService.startedSimulation = true;
        this.simulateMovementToDestination(this.mapService.destinationCoords);
      }
    });

    this.mapService.trackDriver$.subscribe((status) => {
      if (status) {
        this.trackDriver();
      }
    });

    this.mapService.ride$.subscribe((ride) => {
      if (ride != RideCreated.getEmptyRideCreated()) {
        this.drawRoute(ride.locations[0].departure, ride.locations[0].destination);
      }
    });
  }

  private clear() {
    if (this.route != null) {
      this.route.remove();
    }
    this.isDriver = false;
    this.routeLayer.clearLayers();
    this.departurePlain = Location.getEmptyLocation();
    this.destinationPlain = Location.getEmptyLocation();
    this.departure.next(Location.getEmptyLocation());
    this.mapService.setDeparture(Location.getEmptyLocation());
    this.mapService.setDestination(Location.getEmptyLocation());
    this.destination.next(Location.getEmptyLocation());
  }

  public drawRouteBetweenSelectedLocations() {
    this.drawRoute(this.departurePlain, this.destinationPlain);
  }

  ngOnDestroy() {
    this.clear();
  }

  private simulateMovementToDeparture(coords: LatLng[]) {
    let i = 0;
    const interval_timer = setInterval(() => {
      if (i >= coords.length) {
        this.driverService.changeVehicleLocation(coords[i - 1]).subscribe();
        this.driverService.rideToDepartureDone = true;
        clearInterval(interval_timer);
        return;
      }
      if (this.carMarker) {
        this.carMarker.setLatLng(coords[i]);
      }
      if (i % 3 == 0) {
        this.driverService.changeVehicleLocation(coords[i]).subscribe();
      }
      i++;
    }, 200);

    this.simulation = interval_timer;
  }

  private simulateMovementToDestination(coords: LatLng[]) {
    let i = 0;
    const interval_timer = setInterval(() => {
      if (this.carMarker) {
        this.carMarker.setLatLng(coords[i]);
      }
      if (i % 3 == 0) {
        this.driverService.changeVehicleLocation(coords[i]).subscribe();
      }
      i++;
      if (i >= coords.length) {
        this.driverService.changeVehicleLocation(coords[i - 1]).subscribe();
        this.driverService.rideToDestinationDone = true;
        clearInterval(interval_timer);
        return;
      }
    }, 200);

    this.otherSimulation = interval_timer;
  }

  private lastThreeLocations: LatLng[] = [];
  private previousLocations: LatLng[] = [];

  private trackDriver() {
    this.isTrackingDriver = true;
    this.initDriverCar();
    const trackDriverTimer = setInterval(() => {
      if (!this.isTrackingDriver) {
        clearInterval(trackDriverTimer);
        return;
      }
      if (!this.mapService.passengerRideInProgress) {
        this.passengerService.getActiveRide().subscribe(
          (ride: RideCreated) => {
            this.passengerService.currentRideId = ride.id;
            this.passengerService.currentRide = ride;
            this.mapService.setRideInProgress(true);
          },
          (error: any) => {
            const a = 0;
          });
      } else {
        this.driverService.getVehicle().subscribe((vehicle: Vehicle) => {
          const newLocation = new LatLng(vehicle.currentLocation.latitude, vehicle.currentLocation.longitude);
            this.carMarker.setLatLng(newLocation);
            this.previousLocations.push(this.carMarker.getLatLng());
            this.lastThreeLocations.push(this.carMarker.getLatLng());
            if (this.lastThreeLocations.length > 3) {
              this.lastThreeLocations.shift();
            }
            if (this.lastThreeLocations.length == 3) {
              if (this.lastThreeLocations[0].equals(this.lastThreeLocations[1]) && this.lastThreeLocations[1].equals(this.lastThreeLocations[2])) {
                this.passengerService.getActiveRide().subscribe(
                  (ride: RideCreated) => {
                    const b = 0;
                  },
                  (error: any) => {
                    if (this.mapService.passengerRideInProgress) {
                      this.isTrackingDriver = false;
                      this.carMarker.remove();
                      this.mapService.setTrackDriver(false);
                      this.mapService.setRideInProgress(false);
                      this.mapService.setClearMap();
                      clearInterval(trackDriverTimer);
                      return;
                    }
                  });
              }
            }
          }
        );
      }
    }, 1000);
  }
}
