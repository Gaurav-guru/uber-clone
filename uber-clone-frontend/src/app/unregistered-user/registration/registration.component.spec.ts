import { RegistrationComponent } from "./registration.component";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { PassengerService } from "../../passenger/passenger.service";
import { UserService } from "../../shared/user.service";
import { MatIconModule } from "@angular/material/icon";
import { BrowserModule, By } from "@angular/platform-browser";
import { UserMockService } from "../../../tests/user-mock.service";
import { MatInputModule } from "@angular/material/input";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { PassengerMockService } from "../../../tests/passenger-mock.service";
import { DebugElement } from "@angular/core";
import { Router } from "@angular/router";
import { LoginCredentials } from "../../shared/model/LoginCredentials";
import { Token } from "../../shared/model/Token";
import { BehaviorSubject } from "rxjs";

describe('RegistrationComponent', () => {
  let component: RegistrationComponent;
  let fixture: ComponentFixture<RegistrationComponent>;
  let registerForm: HTMLElement;
  let userService: UserService;
  let passengerService: PassengerService;
  let registerButton: any;
  let nextButton: any;
  
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegistrationComponent],
      providers: [
        { provide: UserService, useClass: UserMockService },
        { provide: PassengerService, useClass: PassengerMockService }
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
    
    fixture = TestBed.createComponent(RegistrationComponent);
    userService = TestBed.inject(UserService);
    passengerService = TestBed.inject(PassengerService);
    component = fixture.componentInstance;
    registerButton = fixture.debugElement.query(By.css("#register")).nativeElement;
    nextButton = fixture.debugElement.query(By.css("#nextButton")).nativeElement;
    registerForm = fixture.debugElement.nativeElement.querySelectorAll("form")[0];
    spyOn(component, "check").and.callThrough();
    fixture.detectChanges();
  });

  it('Should create registration component', () => {
    expect(component).toBeTruthy();
  });

  it('Should instantiate registration form', () => {
    expect(registerForm).toBeDefined();
  });

  it("Should not call register passenger in passenger service if fields are empty", async () => {
    component.registerPassengerForm.controls["name"].setValue('');
    component.registerPassengerForm.controls["surname"].setValue('');
    component.registerPassengerForm.controls["telephoneNumber"].setValue('');
    component.registerPassengerForm.controls["address"].setValue('');
    nextButton.click();
    fixture.detectChanges();
    component.registerPassengerForm.controls["email"].setValue('');
    component.registerPassengerForm.controls["password"].setValue('');
    component.registerPassengerForm.controls["passwordRepeat"].setValue('');
    registerButton.click();
    fixture.whenStable().then(() => {
      expect(component.check).toHaveBeenCalled();
      spyOn(passengerService, 'registerPassenger').and.callThrough();
      expect(passengerService.registerPassenger).not.toHaveBeenCalled();
    });
  });

  it("Should not call register passenger in passenger service if email field format is not correct", async () => {
    component.registerPassengerForm.controls["name"].setValue('Test');
    component.registerPassengerForm.controls["surname"].setValue('Test');
    component.registerPassengerForm.controls["telephoneNumber"].setValue('Test');
    component.registerPassengerForm.controls["address"].setValue('Test');
    nextButton.click();
    fixture.detectChanges();
    component.registerPassengerForm.controls["email"].setValue('Test');
    component.registerPassengerForm.controls["password"].setValue('Test');
    component.registerPassengerForm.controls["passwordRepeat"].setValue('Test');
    registerButton.click();
    fixture.whenStable().then(() => {
      expect(component.check).toHaveBeenCalled();
      spyOn(passengerService, 'registerPassenger').and.callThrough();
      expect(passengerService.registerPassenger).not.toHaveBeenCalled();
    });
  });

  it("Should not call register passenger in passenger service if password and password repeat fields are not the same", async () => {
    component.registerPassengerForm.controls["name"].setValue('Test');
    component.registerPassengerForm.controls["surname"].setValue('Test');
    component.registerPassengerForm.controls["telephoneNumber"].setValue('Test');
    component.registerPassengerForm.controls["address"].setValue('Test');
    nextButton.click();
    fixture.detectChanges();
    component.registerPassengerForm.controls["email"].setValue('Test');
    component.registerPassengerForm.controls["password"].setValue('Test');
    component.registerPassengerForm.controls["passwordRepeat"].setValue('Test1');
    registerButton.click();
    fixture.whenStable().then(() => {
      expect(component.check).toHaveBeenCalled();
      spyOn(passengerService, 'registerPassenger').and.callThrough();
      expect(passengerService.registerPassenger).not.toHaveBeenCalled();
    });
  });

  it("Should call register passenger in passenger service if the data is valid", async () => {
    component.registerPassengerForm.controls["name"].setValue('Todor');
    component.registerPassengerForm.controls["surname"].setValue('Todorovic');
    component.registerPassengerForm.controls["telephoneNumber"].setValue('+38162983143');
    component.registerPassengerForm.controls["address"].setValue('Bulevar oslobođenja 23');
    nextButton.click();
    fixture.detectChanges();
    component.registerPassengerForm.controls["email"].setValue('myemail@mail.com');
    component.registerPassengerForm.controls["password"].setValue('Test1test');
    component.registerPassengerForm.controls["passwordRepeat"].setValue('Test1test');
    spyOn(passengerService, 'registerPassenger').and.callThrough();
    registerButton.click();
    expect(component.check).toHaveBeenCalled();
    expect(passengerService.registerPassenger).toHaveBeenCalled();
  });
});
