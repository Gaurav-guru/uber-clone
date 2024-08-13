import { Component } from '@angular/core';
import { Router } from "@angular/router";
import { MapService } from "../../shared/map/map.service";
import { DriverService } from "../driver.service";
import { NotificationService } from "../../shared/notification-service/notification.service";
import { UserService } from "../../shared/user.service";

@Component({
  selector: 'app-driver-side-navigation',
  templateUrl: './driver-side-navigation.component.html',
  styleUrls: ['../../passenger/passenger-side-navigation/passenger-side-navigation.component.css', './driver-side-navigation.component.css', '../../app.component.css']
})
export class DriverSideNavigationComponent {
  image = '';
  fullName = '';

  constructor(private router: Router,
              private mapService: MapService,
              public driverService: DriverService,
              private notificationService: NotificationService,
              private userService: UserService) {
    const picture = sessionStorage.getItem('user_picture');
    if (picture == null) this.image = '../../../assets/images/logo.png';
    else this.image = 'data:image/png;base64,' + picture;

    const fullName = sessionStorage.getItem('user_full_name');
    if (fullName == null) this.fullName = 'First Last';
    else this.fullName = fullName;
  }

  logout() {
    this.mapService.setRideDenied(false);
    this.mapService.setRideInProgress(false);
    if (this.driverService.shiftId != -1) {
      this.driverService.endShift().subscribe({
        next: (value) => {
          this.driverService.shiftId = -1;
        },
        error: (error) => {
          console.log("Error on shift end: " + error.error.message);
        }
      });
    }
    this.userService.logout();
    this.router.navigate(['entrance']);
  }

  changeState(): void {
    if (this.driverService.shiftId == -1) {
      this.driverService.startShift().subscribe({
        next: (value) => {
          this.driverService.shiftId = value.id;
        },
        error: (error) => {
          if (!error.error.message.includes("ongoing")) {
            if (error.error.message.includes("limit")) {
              this.notificationService.createNotification("You have already worked 8 hours today, so you cannot start a new shift.", 7500);
              this.driverService.cantStartShift = true;
            } else {
              console.log("Error on shift start: " + error.error.message);
            }
          }
        },
      });
    } else {
      this.driverService.endShift().subscribe({
        next: (value) => {
          this.driverService.shiftId = -1;
        },
        error: (error) => {
          console.log("Error on shift end: " + error.error.message);
        }
      });
    }
  }
}
