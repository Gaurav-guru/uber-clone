import { Component } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-login-register',
  templateUrl: './login-register.component.html',
  styleUrls: ['./login-register.component.css','../../app.component.css']
})
export class LoginRegisterComponent {

  constructor(private router : Router) {}
  public navigateToLandingPage() {
    this.router.navigate(['unregistered-main']).then();
  }
}
