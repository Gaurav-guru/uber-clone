import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {MatCheckboxChange} from "@angular/material/checkbox";
import {STEPPER_GLOBAL_OPTIONS} from "@angular/cdk/stepper";
import {NgxMaterialTimepickerTheme} from "ngx-material-timepicker";
import {NotificationService} from "../../shared/notification-service/notification.service";
import {PassengerService} from "../passenger.service";
import {UserIdEmail} from "../../shared/model/UserIdEmail";
import {catchError, of} from "rxjs";
import {MapService} from "../../shared/map/map.service";
import {Location} from "../../shared/model/Location";
import {RideReservation} from "../../shared/model/RideReservation";
import * as moment from "moment";
import {Route} from "../../shared/model/Route";
import {RideCreated} from "../../shared/model/RideCreated";
import {Ride} from "../../shared/model/Ride";

@Component({
  selector: 'app-ride-reservation-form',
  templateUrl: './ride-reservation-form.component.html',
  styleUrls: ['./ride-reservation-form.component.css','../../app.component.css'],
  providers: [
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: {showError: true},
    }]
})
export class RideReservationFormComponent {
  fieldsRequiredErrorText = 'Enter required fields!';
  emailFormatErrorText = "Email is poorly formatted!";
  userId: string  = sessionStorage.getItem('user_id') as string;
  userEmail: string = sessionStorage.getItem('user_email') as string;
  passengers: UserIdEmail[] = [new UserIdEmail(parseInt(this.userId), this.userEmail)];
  isReservation = false;
  rideReservationFailureMessage = '';
  reservedRide! : RideReservation;
  destination: Location = Location.getEmptyLocation();
  departure: Location = Location.getEmptyLocation();
  rideBasicInfoForm : FormGroup = new FormGroup({
    vehicleType: new FormControl( '',{nonNullable:true, validators: [Validators.required, Validators.maxLength(50)]}),
    petTransport: new FormControl(false,{nonNullable:true, validators: [Validators.required]}),
    babyTransport: new FormControl(false,{nonNullable:true, validators: [Validators.required]}),
    scheduleTime: new FormControl(null),
  });

  passengersEmailsForm : FormGroup = new FormGroup({
    email : new FormControl('',{nonNullable:false, validators : [Validators.email]}),
  });

  timepickerTheme: NgxMaterialTimepickerTheme = {
    container: {
      bodyBackgroundColor: '#303841',
      buttonColor: '#fff',

    },
    dial: {
      dialBackgroundColor: '#00ADB5',
    },
    clockFace: {
      clockFaceBackgroundColor: '#555',
      clockHandColor: '#00ADB5',
      clockFaceTimeInactiveColor: '#fff'
    }
  };
  totalPrice = 0.00;
  pricePerPerson = 0.00;
  @Input() orderOpened = false;
  @Output() orderClosed = new EventEmitter<boolean>();
  openSuccessPopup = false;
  openFailPopup = false;
  constructor(private notificationService : NotificationService,
              private passengerService : PassengerService,
              private mapService : MapService) {
    mapService.estimates$.subscribe(estimate => {
      this.totalPrice = estimate.estimatedCost;
      this.pricePerPerson = this.totalPrice;
    });
    mapService.departure$.subscribe(departure => this.departure = departure);
    mapService.destination$.subscribe(destination => this.destination = destination);
  }

  public close() {
    this.orderClosed.emit(true);
  }

  public removePassenger(email : string) {
    let index = -1;
    for(const passenger of this.passengers) {
      if (passenger.email == email) {
        index = this.passengers.indexOf(passenger);
        break;
      }
    }
    if (index >= 1) {
      this.passengers.splice(index, 1);
    }
  }
  addPassenger(): void {
    if(!this.passengersEmailsForm.valid || this.passengersEmailsForm.controls["email"].value == "") return;
    const email = this.passengersEmailsForm.controls["email"].value;
    if(this.checkIfPassengerIsAdded(email)) return;
    this.passengerService.getPassengerIdByMail(email).pipe(
      catchError(error => {
        if(error.status == 0) {
          console.error("Server communication failed");
        }
        else {
          this.notificationService.createNotification("User with the entered email does not exist!",3000);
        }
        return of(UserIdEmail.getEmptyUserIdEmail());
      })
    ).subscribe(value => {
      if(value.id != UserIdEmail.getEmptyUserIdEmail().id) {
        this.passengers.push(value);
        this.pricePerPerson = this.totalPrice / this.passengers.length;
      }
    });
  }

  reserve() {
    if(!this.rideBasicInfoForm.valid) return;
    let scheduleTime = null;
    const now = moment();
    if(this.isReservation) {
      scheduleTime = this.rideBasicInfoForm.controls["scheduleTime"].value;
      const date = moment(scheduleTime,"HH:mm");
      if(date.isBefore(now)) date.add(1,'day');
      if(moment.duration(date.diff(now)).asMinutes() > 5*60) {
        this.notificationService.createNotification("You can only reserve a ride up to 5 hours in advance!",3000);
      }
      else {
        scheduleTime = date.format("yyyy-MM-DDTHH:mm:ss");
      }
    }
    const route : Route = {
      departure : this.departure,
      destination : this.destination
    }
    const reservation : RideReservation = {
      locations : [route],
      passengers : this.passengers,
      vehicleType : this.rideBasicInfoForm.controls["vehicleType"].value,
      babyTransport : this.rideBasicInfoForm.controls["babyTransport"].value,
      petTransport : this.rideBasicInfoForm.controls["petTransport"].value,
      scheduledTime: scheduleTime
    }
    this.passengerService.reserveRide(reservation).pipe(
      catchError(error => {
        if(error.status == 0) {
          console.error("Server communication failed");
        }
        else if (error.status == 400) {
          this.rideReservationFailureMessage = "You cannot order a new ride if you have a previous order!"
        }
        else {
          console.log(error);
          this.rideReservationFailureMessage = "The system cannot find a driver. The order is rejected."
        }
        this.openFailPopup = true;
        return of(RideCreated.getEmptyRideCreated());
      })
    ).subscribe(value => {
      if((<RideCreated>value).id != RideCreated.getEmptyRideCreated().id) {
        const reservationCreated = JSON.parse(value.toString());
        const created_rides = JSON.parse(<string>sessionStorage.getItem('created_rides'));
        created_rides.push(reservationCreated);
        sessionStorage.setItem('created_rides',JSON.stringify(created_rides));
        this.openSuccessPopup = true;
        reservation.passengers = reservationCreated.passengers;
        this.reservedRide = reservation;
        this.clearReservationForm();
        this.mapService.setTrackDriver(true);
      }
    });
    this.close();
  }

  closePopup(){
    this.openSuccessPopup = false;
    this.openFailPopup = false;
  }

  toggleDatepicker(event: MatCheckboxChange) {
    if(event.checked) {
      this.isReservation = false;
      return;
    }
    this.isReservation = true;
  }

  checkIfPassengerIsAdded(email : string) : boolean {
    for(const passenger of this.passengers) {
      if(passenger.email == email) return true;
    }
    return false;
  }

  clearReservationForm() {
    this.rideBasicInfoForm.controls["vehicleType"].setValue('');
    this.rideBasicInfoForm.controls["scheduleTime"].setValue('');
    this.passengersEmailsForm.controls["email"].setValue('');
    this.passengers.splice(1,this.passengers.length - 1);
  }
}
