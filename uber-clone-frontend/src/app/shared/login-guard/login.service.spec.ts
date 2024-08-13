import { TestBed } from '@angular/core/testing';
import { LoginService } from './login.service';
import { UserService } from "../user.service";
import { UserMockService } from "../../../tests/user-mock.service";
import { DriverService } from "../../driver/driver.service";
import { NotificationService } from "../notification-service/notification.service";
import { Router } from "@angular/router";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { DriverMockService } from "../../../tests/driver-mock-service";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { UserRetrieved } from "../model/UserRetrieved";
import { BehaviorSubject } from "rxjs";
import { DriversShift } from "../../driver/DriversShift";

describe('LoginService', () => {
  let service: LoginService;
  let userService: UserService;
  let router: Router;
  let driverService: DriverService;
  let notificationService: NotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, MatSnackBarModule],
      providers: [
        { provide: UserService, useClass: UserMockService },
        { provide: DriverService, useClass: DriverMockService },
      ],
    });
    service = TestBed.inject(LoginService);
    userService = TestBed.inject(UserService);
    router = TestBed.inject(Router);
    driverService = TestBed.inject(DriverService);
    notificationService = TestBed.inject(NotificationService);
  });

  it('Login service should be created', () => {
    expect(service).toBeTruthy();
  });

  it('Should return false if user is not logged in', () => {
    const returnVal = service.canActivate();
    expect(returnVal).toBeFalsy();
  });

  it('Should return false if user email is null', () => {
    spyOn(sessionStorage, 'getItem').withArgs('user_email').and.returnValue(null);
    const returnVal = service.canActivate();
    expect(returnVal).toBeFalsy();
  });

  it('Should catch error if user email does not exist', () => {
    spyOn(userService, 'getUserIdByMail').and.returnValue(new BehaviorSubject<UserRetrieved>({
      id: -1, name: '', surname: '', profilePicture: '', telephoneNumber: '', email: '', address: ''
    }).asObservable());
    spyOn(router, 'navigate');
    const returnVal = service.canActivate();
    expect(returnVal).toBeFalsy();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('Should route to passenger home if credentials are correct', () => {
    spyOn(userService, 'isLoggedIn').and.returnValue(true);
    spyOn(sessionStorage, 'getItem').withArgs('user_email').and.returnValue('mail@mail.com');
    const user: UserRetrieved = {
      id: 1,
      name: '',
      surname: '',
      profilePicture: '',
      telephoneNumber: '',
      email: 'mail@mail.com',
      address: ''
    };
    spyOn(userService, 'getUserIdByMail').withArgs('mail@mail.com').and.returnValue(new BehaviorSubject(user).asObservable());
    const routerSpy = spyOn(router, 'navigate').and.stub();
    service.canActivate();
    expect(router.navigate).toHaveBeenCalled();
    expect(routerSpy.calls.first().args[0]).toContain('passenger-home');
  });

  it('Should route to admin home if credentials are correct', () => {
    spyOn(userService, 'isLoggedIn').and.returnValue(true);
    spyOn(userService, 'getRole').and.returnValue("ROLE_ADMIN");
    spyOn(sessionStorage, 'getItem').withArgs('user_email').and.returnValue('mail@mail.com');
    const user: UserRetrieved = {
      id: 1,
      name: '',
      surname: '',
      profilePicture: '',
      telephoneNumber: '',
      email: 'mail@mail.com',
      address: ''
    };
    spyOn(userService, 'getUserIdByMail').withArgs('mail@mail.com').and.returnValue(new BehaviorSubject(user).asObservable());
    const routerSpy = spyOn(router, 'navigate').and.stub();
    service.canActivate();
    expect(router.navigate).toHaveBeenCalled();
    expect(routerSpy.calls.first().args[0]).toContain('admin-home');
  });

  it('Should not route to driver home if his working hours are maxed out', () => {
    spyOn(userService, 'isLoggedIn').and.returnValue(true);
    spyOn(userService, 'getRole').and.returnValue("ROLE_DRIVER");
    spyOn(sessionStorage, 'getItem').withArgs('user_email').and.returnValue('mail@mail.com');
    const user: UserRetrieved = {
      id: 1,
      name: '',
      surname: '',
      profilePicture: '',
      telephoneNumber: '',
      email: 'mail@mail.com',
      address: ''
    };
    spyOn(userService, 'getUserIdByMail').withArgs('mail@mail.com').and.returnValue(new BehaviorSubject(user).asObservable());
    spyOn(notificationService, 'createNotification');
    spyOn(router, 'navigate').and.stub();
    service.canActivate();
    expect(notificationService.createNotification).toHaveBeenCalledWith(
      "You have already worked 8 hours today, so you cannot start a new shift.", 7500);
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('Should route to driver home if his credentials and working hours are in order', () => {
    spyOn(userService, 'isLoggedIn').and.returnValue(true);
    spyOn(userService, 'getRole').and.returnValue("ROLE_DRIVER");
    spyOn(sessionStorage, 'getItem').withArgs('user_email').and.returnValue('mail@mail.com');
    const user: UserRetrieved = {
      id: 1,
      name: '',
      surname: '',
      profilePicture: '',
      telephoneNumber: '',
      email: 'mail@mail.com',
      address: ''
    };
    const validShift: DriversShift = {
      id: 1,
      start: new Date(),
      end: new Date()
    };
    spyOn(userService, 'getUserIdByMail').withArgs('mail@mail.com').and.returnValue(new BehaviorSubject(user).asObservable());
    spyOn(driverService, 'startShift').and.returnValue(new BehaviorSubject(validShift).asObservable());
    const routerSpy = spyOn(router, 'navigate').and.stub();
    service.canActivate();
    expect(router.navigate).toHaveBeenCalled();
    expect(routerSpy.calls.first().args[0]).toContain('driver-home');
  });
});
