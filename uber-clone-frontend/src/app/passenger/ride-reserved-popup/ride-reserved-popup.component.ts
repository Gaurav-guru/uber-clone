import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { FavoriteRideCreation } from "../../shared/model/FavoriteRideCreation";
import { PassengerService } from "../passenger.service";
import { RideReservation } from "../../shared/model/RideReservation";
import { NotificationService } from "../../shared/notification-service/notification.service";
import { catchError, of } from "rxjs";
import { UserIdEmail } from "../../shared/model/UserIdEmail";
import { FavoriteRideCreated } from "../../shared/model/FavoriteRideCreated";

@Component({
  selector: 'app-ride-reserved-popup',
  templateUrl: './ride-reserved-popup.component.html',
  styleUrls: ['./ride-reserved-popup.component.css', '../../app.component.css']
})
export class RideReservedPopupComponent {

  @Input() popupOpened = true;
  @Output() popupClosed = new EventEmitter<boolean>();
  @Input() favoriteRideOrder!: RideReservation;
  firstStep = true;
  addFavoriteYes = false;

  favoriteNameForm: FormGroup = new FormGroup({
    favoriteName: new FormControl('', { nonNullable: false, validators: [Validators.required, Validators.maxLength(100)] }),
  });

  constructor(private passengerService: PassengerService, private notificationService: NotificationService) {
  }

  close() {
    this.popupOpened = false;
    this.addFavoriteYes = false;
    this.popupClosed.emit(true);
  }

  addFavorite() {
    if (!this.favoriteNameForm.valid) return;
    const favoriteRide = new FavoriteRideCreation(
      this.favoriteNameForm.controls["favoriteName"].value,
      this.favoriteRideOrder.locations,
      this.favoriteRideOrder.passengers,
      this.favoriteRideOrder.babyTransport,
      this.favoriteRideOrder.petTransport,
      this.favoriteRideOrder.vehicleType
    );
    this.passengerService.addFavorite(favoriteRide).pipe(
      catchError(error => {
        if (error.status == 0) {
          console.error("Server communication failed");
        } else {
          this.notificationService.createNotification("The maximum number of favorite rides is 10!", 3000);
        }
        return of(FavoriteRideCreated.getEmptyFavoriteRideCreated());
      })
    ).subscribe(() => {
      this.notificationService.createNotification("Ride successfully added to the list of favorites!", 3000);
      this.close();
    });
  }

  openNameDialog() {
    this.firstStep = false;
    this.addFavoriteYes = true;
  }

  closeNaming() {
    this.addFavoriteYes = false;
    this.firstStep = true;
  }
}
