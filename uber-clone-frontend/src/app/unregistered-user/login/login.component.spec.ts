import {ComponentFixture, TestBed, tick, waitForAsync} from '@angular/core/testing';

import { LoginComponent } from './login.component';
import {UserService} from "../../shared/user.service";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BrowserModule, By} from "@angular/platform-browser";
import {MatInputModule} from "@angular/material/input";
import {MatIconModule} from "@angular/material/icon";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {LoginCredentials} from "../../shared/model/LoginCredentials";
import {Token} from "../../shared/model/Token";
import {BehaviorSubject} from "rxjs";
import {UserMockService} from "../../../tests/user-mock.service";
import {Router} from "@angular/router";


describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let loginForm : HTMLElement;
  let userService : UserService;
  let submitButton : any;
  beforeEach(async () => {

    await TestBed.configureTestingModule({
      declarations: [ LoginComponent ],
      providers: [
        {provide: UserService, useClass: UserMockService}
      ],
      imports: [
        BrowserModule,
        FormsModule,
        ReactiveFormsModule,
        MatInputModule,
        MatIconModule,
        BrowserAnimationsModule,
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    userService = TestBed.inject(UserService);
    component = fixture.componentInstance;
    submitButton = fixture.debugElement.query(By.css("#submit")).nativeElement;
    spyOn(component,"login").and.callThrough();
    loginForm = fixture.debugElement.nativeElement.querySelectorAll("form")[0];
    fixture.detectChanges();
  });

  it('Should create login component', () => {
    expect(component).toBeTruthy();
  });

  it('Should instantiate login form', () => {
    expect(loginForm).toBeDefined();
  });

  it("Should not call login in user service if fields are empty", async () => {
    component.loginForm.controls["email"].setValue('');
    component.loginForm.controls["password"].setValue('');
    submitButton.click();
    fixture.whenStable().then(() => {
      expect(component.login).toHaveBeenCalled();
      spyOn(userService,'login').and.callThrough();
      expect(userService.login).not.toHaveBeenCalled();
    });

  });

  it("Should not call login in user service if email field format is not correct", async () => {
    component.loginForm.controls["email"].setValue('mail.com');
    component.loginForm.controls["password"].setValue('Test1Test');
    submitButton.click();
    fixture.whenStable().then(() => {
      expect(component.login).toHaveBeenCalled();
      spyOn(userService,'login').and.callThrough();
      expect(userService.login).not.toHaveBeenCalled();
    });

  });

  it("Should redirect to login path if both fields are valid", (done) => {
    const router = TestBed.inject(Router);
    const routerSpy = spyOn(router,'navigate').and.stub();
    component.loginForm.controls["email"].setValue('mail@mail.com');
    component.loginForm.controls["password"].setValue('Test1Test');
    const credentials : LoginCredentials = {
      email : <string>component.loginForm.controls["email"].value,
      password : <string>component.loginForm.controls["password"].value,
    }
    const token :Token = {
      accessToken : "abc",
      refreshToken : "abc"
    }
    const returnVal = new BehaviorSubject(token).asObservable();
    spyOn(userService,'login').withArgs(credentials).and.returnValue(returnVal);
    submitButton.click();
    fixture.whenStable().then(() => {
      expect(component.login).toHaveBeenCalled();
      expect(userService.login).toHaveBeenCalled();
      expect(routerSpy.calls.first().args[0]).toContain('login');
      done();
    });
  });
});
