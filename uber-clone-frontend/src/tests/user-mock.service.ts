import { Injectable } from '@angular/core';
import {LoginCredentials} from "../app/shared/model/LoginCredentials";
import {BehaviorSubject, Observable} from "rxjs";
import {Token} from "../app/shared/model/Token";
import {UserService} from "../app/shared/user.service";
import {HttpClient} from "@angular/common/http";
import {NotificationService} from "../app/shared/notification-service/notification.service";
import {UserRetrieved} from "../app/shared/model/UserRetrieved";

export interface Error {
  status?: number;
}
@Injectable()
export class UserMockService {

  public login(auth: LoginCredentials): Observable<Token> {

    const token :Token = {
      accessToken : "abc",
      refreshToken : "abc"
    }
    return new BehaviorSubject(token).asObservable();
  }

  public isLoggedIn() : boolean {
    return false;
  }

  public getUserIdByMail(email : string) : Observable<UserRetrieved> {
    const error : Error = {status : 404};
    throw error
  }

  public getRole() : string {
    return "ROLE_PASSENGER";
  }
}
