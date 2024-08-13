import {Component, OnInit} from '@angular/core';
import {NotificationService} from "../../shared/notification-service/notification.service";
import {AdminService} from "../admin.service";
import {UserService} from "../../shared/user.service";

const defaultImage = "../../../assets/images/account.png";

@Component({
  selector: 'app-ride-history-driver',
  templateUrl: './admin-ride-history.component.html',
  styleUrls: ['../../passenger/passenger-account/passenger-account.component.css','./admin-ride-history.component.css', '../../app.component.css']
})
export class AdminRideHistoryComponent implements OnInit {

  baby = false;

  constructor(public adminService : AdminService,
              private notificationService : NotificationService,
              private userService : UserService) { }

  ngOnInit() {
    this.baby = true
  }
}
