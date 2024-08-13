import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {UnregisteredUserMainComponent} from "./unregistered-user-main/unregistered-user-main.component";
import {TopBarComponent} from "./top-bar/top-bar.component";
import {RegistrationComponent} from "./registration/registration.component";
import {LoginRegisterComponent} from "./login-register/login-register.component";
import {LoginComponent} from "./login/login.component";
import {SharedModule} from "../shared/shared.module";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {ReactiveFormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {PassengerModule} from "../passenger/passenger.module";



@NgModule({
  declarations: [
    UnregisteredUserMainComponent,
    TopBarComponent,
    RegistrationComponent,
    LoginRegisterComponent,
    LoginComponent,
  ],
    imports: [
        CommonModule,
        SharedModule,
        MatIconModule,
        MatButtonModule,
        MatFormFieldModule,
        ReactiveFormsModule,
        MatInputModule,
        PassengerModule
    ],
  exports: [
    UnregisteredUserMainComponent,
    TopBarComponent,
    RegistrationComponent,
    LoginRegisterComponent,
    LoginComponent,
  ]
})
export class UnregisteredUserModule { }
