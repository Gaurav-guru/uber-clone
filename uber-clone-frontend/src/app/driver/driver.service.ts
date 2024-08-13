import { Injectable } from '@angular/core';
import {environment} from "../environments/environment";
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, Observable} from "rxjs";
import {UserRetrieved} from "../shared/model/UserRetrieved";
import {DriverUpdateRequest} from "../shared/model/DriverUpdateRequest";
import {DriversShift} from "./DriversShift";
import * as moment from "moment";
import {RideCreated} from "../shared/model/RideCreated";
import {Vehicle} from "../shared/model/Vehicle";
import {Ride} from "../shared/model/Ride";
import {LatLng} from "leaflet";
import {UserService} from "../shared/user.service";
import {PassengerService} from "../passenger/passenger.service";

type Response = {
  message: string
} | {
  message: string,
  status: 'error'
}

@Injectable({
  providedIn: 'root'
})

@Injectable()
export class DriverService {
  url = environment.apiURL + "driver";
  rideUrl = environment.apiURL + "ride";
  vehicleUrl = environment.apiURL + "vehicle";
  shiftId = -1;
  vehicleId = -1;
  cantStartShift = false;
  currentRide: RideCreated | null = null;
  receivedRide = false;
  rideToDepartureDone = false;
  rideToDestinationDone = false;

  constructor(private http: HttpClient,
              private userService: UserService,
              private passengerService: PassengerService) { }

  private changingPassword = new BehaviorSubject<boolean>(false);
  changingPassword$ = this.changingPassword.asObservable();

  public setChangingPassword(changing: boolean) {
    this.changingPassword.next(changing);
  }

  getDriver(): Observable<UserRetrieved> {
    return this.http.get<UserRetrieved>(this.url + "/" + sessionStorage.getItem('user_id'));
  }

  sendUpdateDriverRequest(newDriver : DriverUpdateRequest) : Observable<Response> {
    return this.http.post<Response>(this.url + "/edit-request", newDriver);
  }

  startShift(): Observable<DriversShift> {
    const shift = {
      'start': moment().format('YYYY-MM-DDTHH:mm:ss.SSS'),
    };
    return this.http.post<DriversShift>(this.url + "/" + sessionStorage.getItem("user_id") +"/working-hour", shift);
  }

  endShift(): Observable<DriversShift> {
    const shift = {
      'end': moment().format('YYYY-MM-DDTHH:mm:ss.SSS'),
    };
    return this.http.put<DriversShift>(this.url + "/working-hour/" + this.shiftId, shift);
  }

  denyRide(reason: string): Observable<{'reason': string}> {
    return this.http.put<{'reason': string}>(this.rideUrl + "/" + this.currentRide?.id + "/cancel", {'reason': reason});
  }

  acceptRide(): Observable<RideCreated> {
    return this.http.put<RideCreated>(this.rideUrl + "/" + this.currentRide?.id + "/accept", {});
  }

  startRide(): Observable<RideCreated> {
    return this.http.put<RideCreated>(this.rideUrl + "/" + this.currentRide?.id + "/start", {});
  }

  endRide(): Observable<RideCreated> {
    return this.http.put<RideCreated>(this.rideUrl + "/" + this.currentRide?.id + "/end", {});
  }

  changeVehicleLocation(latlng: LatLng) : Observable<Response> {
    return this.http.put<Response>(this.vehicleUrl + "/" + this.vehicleId + "/location", {latitude: latlng.lat, longitude: latlng.lng});
  }

  getVehicle(): Observable<Vehicle> {
    if (this.userService.getRole() == 'ROLE_DRIVER') {
      return this.http.get<Vehicle>(this.url + "/" + sessionStorage.getItem('user_id') + "/vehicle");
    }
    return this.http.get<Vehicle>(this.url + "/" + this.passengerService.currentRide?.driver.id +"/vehicle");
  }
}
