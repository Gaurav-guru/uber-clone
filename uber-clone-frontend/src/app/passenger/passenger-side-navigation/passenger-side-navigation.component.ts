import { Component } from '@angular/core';
import { Router } from "@angular/router";
import { UserService } from "../../shared/user.service";

@Component({
  selector: 'app-passenger-side-navigation',
  templateUrl: './passenger-side-navigation.component.html',
  styleUrls: ['./passenger-side-navigation.component.css', '../../app.component.css']
})
export class PassengerSideNavigationComponent {

  image = '';
  fullName = '';
  constructor(private router: Router, private userService: UserService) {
    const picture = sessionStorage.getItem('user_picture');
    if (picture == null) this.image = '../../../assets/images/logo.png';
    else this.image = 'data:image/png;base64,' + picture;
    const fullName = sessionStorage.getItem('user_full_name');
    if (fullName == null) this.fullName = 'First Last';
    else this.fullName = fullName;
  }

  logout() {
    this.userService.logout();
    this.router.navigate(['entrance']).then();
  }
}
