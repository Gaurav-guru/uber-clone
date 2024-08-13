import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { ChangePassword } from "./model/ChangePassword";
import { environment } from "../environments/environment";
import { BehaviorSubject, Observable } from "rxjs";
import { Token } from "./model/Token";
import { JwtHelperService } from '@auth0/angular-jwt';
import { UserIdEmail } from "./model/UserIdEmail";
import { UserRetrieved } from "./model/UserRetrieved";
import { NotificationService } from "./notification-service/notification.service";
import { LoginCredentials } from "./model/LoginCredentials";


type ChangePasswordResponse = {
  message: string
} | {
  message: string,
  status: 'error'
}

@Injectable({
  providedIn: 'root'
})
export class UserService {

  idk = false;
  private headers = new HttpHeaders({
    'Content-Type': 'application/json',
    skip: 'true',
  });

  user$ = new BehaviorSubject(null);
  userState$ = this.user$.asObservable();

  constructor(private http: HttpClient, private notificationService: NotificationService) {
    this.user$.next(this.getRole());
  }

  public changePassword(id: number, request: ChangePassword): Observable<ChangePasswordResponse> {
    return this.http.put<ChangePasswordResponse>(environment.apiURL + `user/${id}/changePassword`, request);
  }

  public cutBase64ImageFormat(base64Image: string): string {
    return base64Image.split(',')[1];
  }

  public login(auth: LoginCredentials): Observable<Token> {
    return this.http.post<Token>(environment.apiURL + 'user/login', auth, {
      headers: this.headers,
    });
  }

  public logout(): void {
    sessionStorage.removeItem('user');
  }

  public getRole(): any {
    if (this.isLoggedIn()) {
      const loginInfo = sessionStorage.getItem('user');
      if (loginInfo == null) return null;
      const accessToken: Token = JSON.parse(loginInfo);
      const helper = new JwtHelperService();
      try {
        const role = helper.decodeToken(accessToken.accessToken).role;
        return role;
      } catch (err: any) {
        this.notificationService.createNotification("Email or password is incorrect!", 3000);
        sessionStorage.removeItem("user");
        return null;
      }
    }
  }

  public isLoggedIn(): boolean {
    return sessionStorage.getItem('user') != null;
  }

  public setUser(): void {
    this.user$.next(this.getRole());
  }

  public getUserIdByMail(email: string): Observable<UserRetrieved> {
    const fullUrl = environment.apiURL + "user/" + email + "/id";
    return this.http.get<UserRetrieved>(fullUrl);
  }
}
