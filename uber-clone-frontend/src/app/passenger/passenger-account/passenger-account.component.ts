import { Component, OnInit } from '@angular/core';
import { PassengerService } from "../passenger.service";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { NotificationService } from "../../shared/notification-service/notification.service";
import { UserUpdateInfo } from "../../shared/model/UserUpdateInfo";
import { UserService } from "../../shared/user.service";

@Component({
  selector: 'app-passenger-account',
  templateUrl: './passenger-account.component.html',
  styleUrls: ['./passenger-account.component.css', '../../app.component.css']
})
export class PassengerAccountComponent implements OnInit {

  updatePassengerForm: FormGroup = new FormGroup({
    name: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(100)] }),
    surname: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(100)] }),
    telephoneNumber: new FormControl('', { nonNullable: true, validators: [Validators.required,
    Validators.pattern('^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$')] }),
    address: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(100)] }),
    email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
  });

  updateMode = false;
  updateButtonText = "Update Information";
  image = "../../../assets/images/account.png";
  defaultImage = "../../../assets/images/account.png";
  passwordChange = false;

  constructor(private passengerService: PassengerService,
              private notificationService: NotificationService,
              private userService: UserService) { }

  ngOnInit() {
    this.updatePassengerForm.disable();
    this.showPassenger();
  }

  updatePassenger() {
    const passenger: UserUpdateInfo = {
      name: this.updatePassengerForm.value.name,
      surname: this.updatePassengerForm.value.surname,
      telephoneNumber: this.updatePassengerForm.value.telephoneNumber,
      profilePicture: this.image === this.defaultImage ? null : this.userService.cutBase64ImageFormat(this.image),
      address: this.updatePassengerForm.value.address,
      email: this.updatePassengerForm.value.email,
    }
    this.passengerService.updatePassenger(parseInt(<string>sessionStorage.getItem("user_id")), passenger).subscribe({
      next: () => {
        this.showSuccessMessage();
        this.disableForm();
      },
      error: (error) => {
        if (error.error.includes("email")) {
          this.notificationService.createNotification('Email address is taken!', 3000);
        } else if (error.error.includes("telephone")) {
          this.notificationService.createNotification('Phone number format is invalid!', 3000);
        }
      }
    });
    this.disableForm();
  }

  openChangePassword(): void {
    this.passwordChange = true;
  }

  closeChangePasswordWindow() {
    this.passwordChange = false;
  }

  showPassenger() {
    this.passengerService.getPassenger(parseInt(<string>sessionStorage.getItem("user_id")))
      .subscribe(passenger => {
        this.updatePassengerForm.patchValue({
          name: passenger.name,
          surname: passenger.surname,
          telephoneNumber: passenger.telephoneNumber,
          address: passenger.address,
          email: passenger.email,
        })
        if (passenger.profilePicture != null) {
          this.image = 'data:image/png;base64,' + passenger.profilePicture;
        } else {
          this.image = String(this.defaultImage);
        }
      });
  }

  toggleUpdateMode() {
    if (this.updateMode) {
      if (this.updatePassengerForm.valid) {
        this.updatePassenger();
        this.disableForm();
      }
    } else this.enableForm();
  }

  enableForm() {
    this.updateButtonText = "Confirm Changes";
    this.updateMode = true;
    this.updatePassengerForm.enable();
  }

  disableForm() {
    this.updateButtonText = "Update Information";
    this.updateMode = false;
    this.updatePassengerForm.disable();
  }

  showSuccessMessage() {
    this.notificationService.createNotification("Information successfully updated!", 2000);
  }

  onFileSelected(event: Event) {
    const maxFileSize = 5 * 1024 * 1024;
    if (event.target != null) {
      const inputElement: HTMLInputElement = event.target as HTMLInputElement;
      if (inputElement.files != null) {
        const file: File = inputElement.files[0];
        if (file) {
          if (file.type.startsWith('image/') && file.size < maxFileSize) {
            const fileReader = new FileReader();
            fileReader.onload = () => {
              this.image = fileReader.result as string;
            };
            fileReader.readAsDataURL(file);
          } else {
            this.notificationService.createNotification('You must select a valid image less than 5MB.', 5000);
          }
        }
      }
    }
  }

}
