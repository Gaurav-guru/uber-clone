import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {RidePage} from "../model/RidePage";

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
export class RideHistoryService {
  urlDriver = environment.apiURL + "driver";
  urlPassenger = environment.apiURL + "passenger";

  constructor(private http: HttpClient) { }

  getPassengerRidesRequest(id : number) : Observable<RidePage>{
    return this.http.get<RidePage>(this.urlPassenger + "/" + id + "/ride")
  }

  getPassengerRidesRequestt(id : number) : Observable<RidePage>{
    return this.http.get<RidePage>(this.urlPassenger + "/" + id + "/ride?page=0;size=1000;")
  }
}
