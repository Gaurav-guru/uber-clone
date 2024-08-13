import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AdminSideNavigationComponent} from "./admin-side-navigation/admin-side-navigation.component";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatCardModule} from "@angular/material/card";
import {MatListModule} from "@angular/material/list";
import {RouterLink, RouterLinkActive, RouterOutlet} from "@angular/router";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {AdminService} from "./admin.service";
import {SharedModule} from "../shared/shared.module";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatSelectModule} from "@angular/material/select";
import {AdminHomeComponent} from "./admin-home/admin-home.component";
import {AdminCreateDriverComponent} from "./admin-create-driver/admin-create-driver.component";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {CdkVirtualScrollViewport} from "@angular/cdk/scrolling";
import {AdminRideHistoryComponent} from "./admin-ride-history/admin-ride-history.component";

@NgModule({
  declarations: [
    AdminSideNavigationComponent,
    AdminHomeComponent,
    AdminCreateDriverComponent,
    AdminRideHistoryComponent
  ],
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule,
    MatSidenavModule,
    MatCardModule,
    MatListModule,
    RouterLink,
    RouterLinkActive,
    MatSlideToggleModule,
    FormsModule,
    RouterOutlet,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    SharedModule,
    MatAutocompleteModule,
    MatSelectModule,
    MatCheckboxModule,
    CdkVirtualScrollViewport
  ],
  exports: [
    AdminSideNavigationComponent
  ],
  providers: [
    AdminService
  ]
})
export class AdminModule { }
