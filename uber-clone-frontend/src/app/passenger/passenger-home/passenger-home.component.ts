import { Component, OnInit } from '@angular/core';
import { MapService } from "../../shared/map/map.service";
import * as SockJS from "sockjs-client";
import * as Stomp from "stompjs";
import { RideCreated } from "../../shared/model/RideCreated";
import { NotificationService } from "../../shared/notification-service/notification.service";
import * as moment from "moment";
import { RideReservation } from "../../shared/model/RideReservation";
import { Ride } from "../../shared/model/Ride";

@Component({
  selector: 'app-passenger-home',
  templateUrl: './passenger-home.component.html',
  styleUrls: ['./passenger-home.component.css', '../../app.component.css']
})
export class PassengerHomeComponent implements OnInit {

  constructor(public mapService: MapService,
              private notificationService: NotificationService) {
    this.initializeWebSocketConnection();
  }

  orderClicked = false;
  stompClient!: Stomp.Client;
  finishedRideId = -1;
  openDriverRatingForm = false;
  openVehicleRatingForm = false;
  rides: RideCreated[] = [];
  openDetails = false;
  rideRoute = '';
  rideTotalCost = '';
  ridePricePerPerson = '';
  rideScheduleTime = '';

  ngOnInit() {
    this.mapService.setIsDriver(false);
  }

  public openRideOrderForm() {
    this.orderClicked = true;
  }

  public closeRideOrderForm() {
    this.orderClicked = false;
  }

  public openVehicleRating() {
    this.openDriverRatingForm = false;
    this.openVehicleRatingForm = true;
  }

  public closeRatings() {
    this.openDriverRatingForm = false;
    this.openVehicleRatingForm = false;
  }

  public closeRideDetailsForm() {
    this.openDetails = false;
  }

  initializeWebSocketConnection() {
    const ws = new SockJS('http://localhost:8080/socket');
    this.stompClient = Stomp.over(ws);
    this.stompClient.debug = f => f;
    this.stompClient.connect({}, () => {
      this.stompClient.subscribe('/ride-ordered/get-ride', (message: { body: string }) => {
        const ride: RideCreated = JSON.parse(message.body);
        setTimeout(() => {
          for (const passenger of ride.passengers) {
            if (passenger.id === parseInt(sessionStorage.getItem('user_id') as string)) {
              this.rides.push(ride);
              this.finishedRideId = ride.id;

              const createdRides = JSON.parse(sessionStorage.getItem('created_rides') as string);
              let isSelfMade = false;
              for (const reservedRide of createdRides) {
                if (ride.id === reservedRide.id) {
                  isSelfMade = true;
                  break;
                }
              }
              if (isSelfMade) break;

              const scheduleTime = moment(ride.scheduledTime);
              const notification: string = "You have been added as a passenger for a ride at " + scheduleTime.format("HH:mm") + ".";
              this.notificationService.createNotification(notification, 3000);
              const departureAddress = ride.locations[0].departure.address.split(",")[0];
              const destinationAddress = ride.locations[0].destination.address.split(",")[0];
              this.rideRoute = departureAddress + " - " + destinationAddress;
              this.rideScheduleTime = scheduleTime.format("HH:mm");
              this.rideTotalCost = ride.totalCost.toString();
              this.ridePricePerPerson = (ride.totalCost / ride.passengers.length).toString();
              this.openDetails = true;
              break;
            }
          }
        }, 100);
      });

      this.stompClient.subscribe('/ride-ended/notification', (message: { body: string }) => {
        const passengers: number[] = JSON.parse(message.body);
        if (passengers.includes(parseInt(sessionStorage.getItem('user_id') as string))) {
          this.openDriverRatingForm = true;
        }
      });

      this.stompClient.subscribe('/ride-started/notification', (message: { body: string }) => {
        const rideCreated: RideCreated = JSON.parse(message.body);
        for (const passenger of rideCreated.passengers) {
          if (passenger.id !== parseInt(sessionStorage.getItem('user_id') as string)) continue;
          this.mapService.setTrackDriver(true);
          this.mapService.setRide(rideCreated);
          const notification = "The ride has started.";
          this.notificationService.createNotification(notification, 3000);
          break;
        }
      });

      this.stompClient.subscribe('/ride-rejected/notification', (message: { body: string }) => {
        const rideCreated: RideCreated = JSON.parse(message.body);
        for (const passenger of rideCreated.passengers) {
          if (passenger.id !== parseInt(sessionStorage.getItem('user_id') as string)) continue;
          const notification = "Your ride scheduled at " + moment(rideCreated.scheduledTime).format("HH:mm") + " has been rejected.";
          this.notificationService.createNotification(notification, 3000);
        }
      });

      this.stompClient.subscribe('/ride-ordered/reservation-notification', (message: { body: string }) => {
        const reservedRide: RideReservation = JSON.parse(message.body);
        for (const passenger of reservedRide.passengers) {
          if (passenger.id !== parseInt(sessionStorage.getItem('user_id') as string)) continue;
          const notification = "You have a reserved ride at " + moment(reservedRide.scheduledTime).format("HH:mm") + ".";
          this.notificationService.createNotification(notification, 3000);
        }
      });

      this.stompClient.subscribe('/ride-ordered/not-found', (message: { body: string }) => {
        const reservedRide: RideReservation = JSON.parse(message.body);
        for (const passenger of reservedRide.passengers) {
          if (passenger.id !== parseInt(sessionStorage.getItem('user_id') as string)) continue;
          if (reservedRide.scheduledTime === null) break;
          const notification = "Your ride reserved at " + moment(reservedRide.scheduledTime).format("HH:mm") + " is being rejected. The system did not find a driver.";
          this.notificationService.createNotification(notification, 3000);
        }
      });
    });
  }
}
