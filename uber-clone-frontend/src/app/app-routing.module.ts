import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {PassengerAccountComponent} from "./passenger/passenger-account/passenger-account.component";
import {LoginRegisterComponent} from "./unregistered-user/login-register/login-register.component";
import {PassengerHomeComponent} from "./passenger/passenger-home/passenger-home.component";
import {UnregisteredUserMainComponent} from "./unregistered-user/unregistered-user-main/unregistered-user-main.component";
import {DriverAccountComponent} from "./driver/driver-account/driver-account.component";
import {DriverHomeComponent} from "./driver/driver-home/driver-home.component";
import {AdminHomeComponent} from "./admin/admin-home/admin-home.component";
import {AdminCreateDriverComponent} from "./admin/admin-create-driver/admin-create-driver.component";
import {AdminRideHistoryComponent} from "./admin/admin-ride-history/admin-ride-history.component";
import {LoginService} from "./shared/login-guard/login.service";

const routes: Routes = [
  {path: "admin-ride-history", component: AdminRideHistoryComponent},
  {path: "admin-home", component: AdminHomeComponent},
  {path: "admin-create-driver", component: AdminCreateDriverComponent},
  {path: "passenger-account", component: PassengerAccountComponent },
  {path: "driver-account", component: DriverAccountComponent },
  {
    path: 'login',
    component: LoginRegisterComponent,
    canActivate: [LoginService],
    loadChildren: () =>
      import('././shared/shared.module').then((m) => m.SharedModule),
  },
  {path: 'entrance',component:LoginRegisterComponent},
  {path: "passenger-home", component: PassengerHomeComponent},
  {path: "driver-home", component: DriverHomeComponent},
  {path: "unregistered-main", component: UnregisteredUserMainComponent },
  {path: '', redirectTo:'/unregistered-main', pathMatch: 'full'},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
