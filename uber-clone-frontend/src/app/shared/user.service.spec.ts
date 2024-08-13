import { TestBed } from '@angular/core/testing';

import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {LoginCredentials} from "./model/LoginCredentials";
import {UserService} from "./user.service";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {Token} from "./model/Token";
import {UserRetrieved} from "./model/UserRetrieved";

describe('UserService', () => {
  let service: UserService;
  let httpController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, MatSnackBarModule,BrowserAnimationsModule],
    });
    service = TestBed.inject(UserService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpController.verify();
  });

  it('User service should be created', () => {
    expect(service).toBeTruthy();
  });


  it('Should return token', () => {
    const loginCredentials : LoginCredentials = {
      email: "passenger1@mail.com",
      password: "Test2test"
    }
    const token :Token = {
      accessToken : "abc",
      refreshToken : "abc"
    }
    service.login(loginCredentials).subscribe(result => {
      expect(result.accessToken).toEqual("abc");
      expect(result.refreshToken).toEqual("abc");
    });

    const req = httpController.expectOne({
      method: 'POST',
      url: 'http://localhost:8080/api/user/login'
    });
    req.flush(token);
  });

  it('Should return user based on email', () => {
    const loginCredentials : LoginCredentials = {
      email: "passenger1@mail.com",
      password: "Test2test"
    }
    const user : UserRetrieved = {
      id : 1,
      name : '',
      surname : '',
      profilePicture : '',
      telephoneNumber : '',
      email : 'mail@mail.com',
      address : ''
    }

    service.getUserIdByMail("mail@mail.com").subscribe(result => {
      expect(result).toEqual(user);
    });

    const req = httpController.expectOne({
      method: 'GET',
      url: 'http://localhost:8080/api/user/mail@mail.com/id'
    });
    req.flush(user);
  });

});
