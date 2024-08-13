import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import {DriverModule} from "./driver/driver.module";
import {PassengerModule} from "./passenger/passenger.module";
import {UnregisteredUserModule} from "./unregistered-user/unregistered-user.module";
import {SharedModule} from "./shared/shared.module";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {AppRoutingModule} from "./app-routing.module";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {MatNativeDateModule} from '@angular/material/core';
import {AdminModule} from "./admin/admin.module";
import {InterceptorService} from "./shared/interceptor/interceptor.service";

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatSnackBarModule,
    DriverModule,
    AdminModule,
    PassengerModule,
    UnregisteredUserModule,
    SharedModule,
    MatNativeDateModule,
  ],
  exports: [
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: InterceptorService,
      multi: true,
    },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
