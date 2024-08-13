import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {MapService} from "../../shared/map/map.service";
import {AdminService} from "../admin.service";
import {NotificationService} from "../../shared/notification-service/notification.service";

@Component({
  selector: 'app-admin-side-navigation',
  templateUrl: './admin-side-navigation.component.html',
  styleUrls: ['../../passenger/passenger-side-navigation/passenger-side-navigation.component.css', './admin-side-navigation.component.css', '../../app.component.css']
})
export class AdminSideNavigationComponent {

  image  = '';
  fullName = '';
  constructor(private router: Router,
              private mapService: MapService) {

    const picture = sessionStorage.getItem('user_picture');
    if(picture == null) this.image = '../../../assets/images/logo.png';
    else this.image = 'data:image/png;base64,' + picture;
    const fullName = sessionStorage.getItem('user_full_name');
    if(fullName == null) this.fullName = 'Name surname'
    else this.fullName = fullName;
  }

  logout() {
    this.mapService.setRideDenied(false);
    this.mapService.setRideInProgress(false);
    this.router.navigate(['entrance']);
  }
}
