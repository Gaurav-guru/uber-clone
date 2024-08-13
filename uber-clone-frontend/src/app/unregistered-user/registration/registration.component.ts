import {Component} from "@angular/core";
import {PassengerService} from "../../passenger/passenger.service";
import {FormControl, FormGroup, ValidatorFn, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {User} from "../../shared/model/User";
import {UserService} from "../../shared/user.service";

@Component({
  selector: 'app-registration',
  templateUrl: 'registration.component.html',
  styleUrls: ['registration.component.css', '../../app.component.css']
})
export class RegistrationComponent {

  constructor(private passengerService: PassengerService,
              private router: Router,
              private userService: UserService) {
  }

  registerPassengerForm: FormGroup = new FormGroup({
    name: new FormControl('', {nonNullable: true, validators: [Validators.required]}),
    surname: new FormControl('', {nonNullable: true, validators: [Validators.required]}),
    telephoneNumber: new FormControl('', {nonNullable: true, validators: [Validators.required]}),
    address: new FormControl('', {nonNullable: true, validators: [Validators.required]}),
    email: new FormControl('', {nonNullable: true, validators: [Validators.required, Validators.email]}),
    password: new FormControl('', {nonNullable: true, validators: [Validators.required]}),
    passwordRepeat: new FormControl('', {nonNullable: true, validators: [Validators.required]}),
  });

  firstPartVisible = true;

  registerPassenger() {
    const passenger: User = {
      name: this.registerPassengerForm.value.name,
      surname: this.registerPassengerForm.value.surname,
      telephoneNumber: this.registerPassengerForm.value.telephoneNumber,
      profilePicture: null,
      address: this.registerPassengerForm.value.address,
      email: this.registerPassengerForm.value.email,
      password: this.registerPassengerForm.value.password
    }
    const loginVal = {
      email: this.registerPassengerForm.value.email,
      password: this.registerPassengerForm.value.password,
    };
    this.passengerService.registerPassenger(passenger).subscribe(
      (result) => {
        console.log(result);
        this.userService.login(loginVal).subscribe({
          next: (result) => {
            console.log(result)
            sessionStorage.setItem('user_email',<string>loginVal.email);
            sessionStorage.setItem('user', JSON.stringify(result));
            this.userService.setUser();
            this.router.navigate(['login']);
          },
          error: (error) => {
            {
              console.log(error);
            }
          },
        });
      },
      (error) => {
        console.log(error);
      }
    );
  }

  check() {
    if (this.registerPassengerForm.valid &&
      this.registerPassengerForm.value.password === this.registerPassengerForm.value.passwordRepeat) {
      this.registerPassenger();
    }
  }

  activateSecondPart() {
    this.firstPartVisible = false;
  }

  activateFirstPart() {
    this.firstPartVisible = true;
  }
}
