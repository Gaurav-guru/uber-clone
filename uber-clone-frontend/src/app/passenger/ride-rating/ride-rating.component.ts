import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {PassengerService} from "../passenger.service";
import {Review} from "../../shared/model/Review";
import {NotificationService} from "../../shared/notification-service/notification.service";
import {UserIdEmail} from "../../shared/model/UserIdEmail";

@Component({
  selector: 'app-ride-rating',
  templateUrl: './ride-rating.component.html',
  styleUrls: ['./ride-rating.component.css','../../app.component.css']
})
export class RideRatingComponent implements OnInit{

  @Input() isDriver = true;
  who = "driver";  // Translated from "vozača"
  rating = 0;
  @Input() windowVisible = true;
  @Output() windowClosed = new EventEmitter<boolean>();
  @Input() rideId = -1;

  reviewForm : FormGroup = new FormGroup({
    comment: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(500)] }),
  });

  constructor(private passengerService : PassengerService, private notificationService : NotificationService) {

  }
  ngOnInit() {
    if (this.isDriver) this.who = "driver";  // Translated from "vozača"
    else this.who = "vehicle";  // Translated from "vozilo"
  }
  
  close() {
    this.rating = 0;
    this.windowClosed.emit(true);
    this.windowVisible = false;
    if (!this.isDriver) {
      setTimeout(() => {
        window.location.reload();
      }, 250);
    }
  }

  onRatingChanged(rating: number) {
    this.rating = rating;
  }

  review() {
    if (!this.reviewForm.valid || this.rating == 0) return;
    const reviewer = new UserIdEmail(parseInt(<string>sessionStorage.getItem("user_id")), <string>sessionStorage.getItem("user_email"));
    const review: Review = {
      rating: this.rating,
      comment: this.reviewForm.controls["comment"].value,
      reviewer: reviewer
    };
    if (this.isDriver) this.passengerService.reviewDriver(review, this.rideId).subscribe();
    else this.passengerService.reviewVehicle(review, this.rideId).subscribe();
    this.notificationService.createNotification("Review successfully submitted!", 3000);  // Translated from "Ocena je uspešno poslata!"
    this.close();
  }
}
