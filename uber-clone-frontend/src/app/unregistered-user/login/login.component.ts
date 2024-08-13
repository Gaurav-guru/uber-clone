import { Component } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {UserService} from "../../shared/user.service";
import {LoginCredentials} from "../../shared/model/LoginCredentials";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css','../../app.component.css']
})
export class LoginComponent {

  constructor(private router:Router,
              private userService : UserService) { }
  loginForm = new FormGroup({
    email: new FormControl( '',[Validators.required, Validators.email]),
    password: new FormControl('',[Validators.required]),
  });

  login(): void {
    const loginVal : LoginCredentials = {
      email: <string>this.loginForm.value.email,
      password: <string>this.loginForm.value.password,
    };
    if (this.loginForm.valid) {
      this.userService.login(loginVal).subscribe({
        next: (result) => {

          sessionStorage.setItem('user_email',<string>this.loginForm.value.email);
          sessionStorage.setItem('user', JSON.stringify(result));
          this.router.navigate(['login']);
        },
        error: (error) => {
         {
            console.log(error);
          }
        },
      });
    }
  }

}
