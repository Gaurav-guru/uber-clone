import { Injectable } from '@angular/core';
import {Observable, throwError} from "rxjs";
import {DriversShift} from "../app/driver/DriversShift";
import {Error} from "./user-mock.service";

export interface ComplexError {
  error: {
    message : string
  }
}
@Injectable()
export class DriverMockService {

  public startShift(): Observable<DriversShift> {
    const err : ComplexError = { error: {message:"limit"}};
    return throwError(err);
  }
}
