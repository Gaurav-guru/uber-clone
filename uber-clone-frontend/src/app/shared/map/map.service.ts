import { Injectable } from '@angular/core';
import {BehaviorSubject, catchError, lastValueFrom, Observable, Subject} from "rxjs";
import {HttpClient, HttpEvent, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {RouteEstimates} from "../model/RouteEstimates";
import {RouteEstimatesRequestBody} from "../model/RouteEstimatesRequestBody";
import {Location} from "../model/Location";
import {Ride} from "../model/Ride";
import {LatLng} from "leaflet";
import {RideCreated} from "../model/RideCreated";

@Injectable({
  providedIn: 'root'
})
export class MapService {

  private estimatesUrl = environment.apiURL + "unregisteredUser";
  /*private header = new HttpHeaders({
    'Content-Type': 'application/json',
    skip: 'true',
  });*/
  destinationCoords: LatLng[] = [];
  startedSimulation = false;

  constructor(private http: HttpClient) {
  }

  public userIsDriver = false;

  private ride = new BehaviorSubject<RideCreated>(RideCreated.getEmptyRideCreated());
  ride$ = this.ride.asObservable();
  private trackDriver = new BehaviorSubject<boolean>(false);
  trackDriver$ = this.trackDriver.asObservable();
  private departureDestinationCoords = new BehaviorSubject<LatLng[]>([]);
  departureDestinationCoords$ = this.departureDestinationCoords.asObservable();
  private driverDepartureCoords = new BehaviorSubject<LatLng[]>([]);
  driverDepartureCoords$ = this.driverDepartureCoords.asObservable();
  private simulateToDestination = new BehaviorSubject<boolean>(false);
  simulateToDestination$ = this.simulateToDestination.asObservable();
  public simulateMovementToDeparture = false;
  public simulateMovementToDestination = false;
  private rideInProgress = new BehaviorSubject<boolean>(false);
  rideInProgress$ = this.rideInProgress.asObservable();
  private rideDenied = new BehaviorSubject<boolean>(false);
  rideDenied$ = this.rideDenied.asObservable();
  private departure : BehaviorSubject<Location> = new BehaviorSubject<Location>(Location.getEmptyLocation());
  departure$ = this.departure.asObservable();
  private destination : BehaviorSubject<Location> = new BehaviorSubject<Location>(Location.getEmptyLocation());
  destination$ = this.destination.asObservable();
  private drawRouteRequest : Subject<boolean> = new Subject<boolean>();
  drawRouteRequest$ = this.drawRouteRequest.asObservable();
  private mapRoutingOnly : Subject<boolean> = new Subject<boolean>();
  mapRoutingOnly$ = this.mapRoutingOnly.asObservable();
  private clearMap : Subject<boolean> = new Subject<boolean>();
  clearMap$ = this.clearMap.asObservable();
  private estimates : BehaviorSubject<RouteEstimates> = new BehaviorSubject<RouteEstimates>(RouteEstimates.getEmptyRouteEstimates());
  estimates$ = this.estimates.asObservable();
  private returnEstimates : Subject<boolean>= new Subject<boolean>();
  returnEstimates$ = this.returnEstimates.asObservable();

  private isDriver : Subject<boolean>= new Subject<boolean>();
  isDriver$ = this.isDriver.asObservable();
  private rideReceived : Subject<boolean> = new Subject<boolean>();
  rideReceived$ = this.rideReceived.asObservable();

  public passengerRideInProgress = false;

  public setRide(ride: RideCreated) {
    this.ride.next(ride);
  }

  public setTrackDriver(status: boolean) {
    this.trackDriver.next(status);
  }

  public setDepartureDestinationCoords(coords: LatLng[]) {
    this.departureDestinationCoords.next(coords);
  }

  public setSimulateToDestination(status: boolean) {
    this.simulateToDestination.next(status);
  }

  public setDriverDepartureCoords(coords: LatLng[]) {
    this.driverDepartureCoords.next(coords);
  }

  public setRideInProgress(inProgress: boolean) {
    this.passengerRideInProgress = inProgress;
    this.rideInProgress.next(inProgress);
  }

  public setRideDenied(denied: boolean) {
    this.rideDenied.next(denied);
  }

  public setRideReceived(received: boolean) {
    this.rideReceived.next(received);
  }

  private getRouteEstimates() : Promise<RouteEstimates>{
    const rideDetails: RouteEstimatesRequestBody = {
      locations: [this.departure.getValue(), this.destination.getValue()],
      vehicleType: "STANDARDNO",
      babyTransport: false,
      petTransport: false
    }
    return lastValueFrom(this.http.post<RouteEstimates>(this.estimatesUrl, rideDetails));
  }

  public setDeparture(departure : Location) {
    this.departure.next(departure);
  }

  public setDestination(destination : Location) {
    this.destination.next(destination);
  }

  public setClearMap() {
    this.clearMap.next(true);
  }

  public setMapRoutingOnly(status : boolean) {
    this.mapRoutingOnly.next(status);
  }

  public setDrawRouteRequest() {
    this.drawRouteRequest.next(true);
  }

  public async setRideEstimates() {
    await this.getRouteEstimates().then(retrieved => this.estimates.next(retrieved));
    this.returnEstimates.next(true);
  }

  public setIsDriver(status : boolean) {
    this.isDriver.next(status);
  }

  getAllActiveRides(): Observable<Ride[]> {
    return this.http.get<Ride[]>(environment.apiURL + 'ride');
  }
}
