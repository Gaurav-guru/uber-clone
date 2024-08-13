import {Component, OnInit} from '@angular/core';
import {BehaviorSubject, Subject} from "rxjs";
import {Location} from "../../shared/model/Location";
import {NotificationService} from "../../shared/notification-service/notification.service";
import {MapService} from "../../shared/map/map.service";
import * as SockJS from "sockjs-client";
import * as Stomp from "stompjs";
import {RideCreated} from "../../shared/model/RideCreated";
import {DriverService} from "../driver.service";

@Component({
  selector: 'app-driver-home',
  templateUrl: './driver-home.component.html',
  styleUrls: ['./driver-home.component.css', '../../app.component.css']
})
export class DriverHomeComponent implements OnInit {
  public departure: BehaviorSubject<Location> = new BehaviorSubject<Location>(Location.getEmptyLocation());
  public destination: BehaviorSubject<Location> = new BehaviorSubject<Location>(Location.getEmptyLocation());
  public passenger: Subject<boolean> = new Subject<boolean>();
  public rideReceived = false;
  public displayCar = true;
  stompClient!: Stomp.Client;

  private initializeWebSocketConnection() {
    const ws = new SockJS('http://localhost:8080/socket');
    this.stompClient = Stomp.over(ws);
    this.stompClient.debug = f => f;
    this.stompClient.connect({}, () => {
      this.stompClient.subscribe('/ride-ordered/get-ride', (message: {body: string}) => {
        const ride: RideCreated = JSON.parse(message.body);
        if (ride.driver.id == parseInt(sessionStorage.getItem("user_id")!)) {
          this.driverService.currentRide = ride;
          this.driverService.receivedRide = true;
          this.receiveRide();
        }
      });
    });
  }

  constructor(private notificationService: NotificationService,
              public mapService: MapService,
              private driverService: DriverService) {
    this.initializeWebSocketConnection();
  }

  public sendLocationToMap(location: Location, which: string): void {
    if (which == 'destination') {
      this.mapService.setDestination(location);
    } else {
      this.mapService.setDeparture(location);
    }
  }

  public drawRoute(): void {
    this.mapService.setDrawRouteRequest();
  }

  public receiveRide(): void {
    this.mapService.setRideReceived(true);
    this.notificationService.createNotification("You have received a ride!", 3000);
    this.displayCar = false;
    this.mapService.simulateMovementToDeparture = true;
  }

  ngOnInit(): void {
    console.log('Loaded Driver Home');
  }
}
