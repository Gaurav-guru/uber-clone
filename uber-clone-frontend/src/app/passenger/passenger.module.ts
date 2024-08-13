import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {PassengerAccountComponent} from "./passenger-account/passenger-account.component";
import {PassengerHomeComponent} from "./passenger-home/passenger-home.component";
import {PassengerSideNavigationComponent} from "./passenger-side-navigation/passenger-side-navigation.component";
import {RideEstimatesComponent} from "./ride-estimates/ride-estimates.component";
import {ReactiveFormsModule} from "@angular/forms";
import {MatIconModule} from "@angular/material/icon";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatButtonModule} from "@angular/material/button";
import {MatInputModule} from "@angular/material/input";
import {MatCardModule} from "@angular/material/card";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatListModule} from "@angular/material/list";
import {RouterLink, RouterLinkActive, RouterOutlet} from "@angular/router";
import {SharedModule} from "../shared/shared.module";
import { RideReservationFormComponent } from './ride-reservation-form/ride-reservation-form.component';
import {MatSelectModule} from "@angular/material/select";
import {MatStepperModule} from "@angular/material/stepper";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatChipsModule} from "@angular/material/chips";
import {RideReservedPopupComponent} from "./ride-reserved-popup/ride-reserved-popup.component";
import { RideNotReservedPopupComponent } from './ride-not-reserved-popup/ride-not-reserved-popup.component';
import { RideRatingComponent } from './ride-rating/ride-rating.component';
import { RatingStarBarComponent } from './rating-star-bar/rating-star-bar.component';
import {NgxMaterialTimepickerModule} from "ngx-material-timepicker";
import { RideDetailsComponent } from './ride-details/ride-details.component';

@NgModule({
  declarations: [
    PassengerAccountComponent,
    PassengerHomeComponent,
    PassengerSideNavigationComponent,
    RideEstimatesComponent,
    RideReservationFormComponent,
    RideReservedPopupComponent,
    RideNotReservedPopupComponent,
    RideRatingComponent,
    RatingStarBarComponent,
    RideDetailsComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatIconModule,
    MatFormFieldModule,
    MatAutocompleteModule,
    MatButtonModule,
    MatInputModule,
    MatCardModule,
    MatSidenavModule,
    MatListModule,
    RouterLink,
    RouterLinkActive,
    RouterOutlet,
    SharedModule,
    MatSelectModule,
    MatStepperModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatChipsModule,
    NgxMaterialTimepickerModule,
  ],
    exports: [
        PassengerAccountComponent,
        PassengerHomeComponent,
        PassengerSideNavigationComponent,
        RideEstimatesComponent,
        RideReservationFormComponent
    ],
  providers: [
  ]
})
export class PassengerModule { }
