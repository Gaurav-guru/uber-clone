import { Injectable } from '@angular/core';
import {environment} from "../environments/environment";
import {HttpClient} from "@angular/common/http";
import {DriverUpdateRequest} from "../shared/model/DriverUpdateRequest";
import {Observable} from "rxjs";
import {VehicleCreateRequest} from "../shared/model/VehicleCreateRequest";

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
export class AdminService {
  url = environment.apiURL + "driver";

  constructor(private http: HttpClient) { }

  sendCreateDriverRequest(newDriver : DriverUpdateRequest) : Observable<Response> {
    return this.http.post<Response>(this.url + "/driver", newDriver);
  }
  sendCreateVehicleRequest(newVehicle : VehicleCreateRequest) : Observable<Response>{
    return this.http.post<Response>(this.url + "/" + newVehicle.driverId + "/vehicle", newVehicle)
  }
}
