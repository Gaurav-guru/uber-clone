import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { DriverService } from '../../driver/driver.service';
import { NotificationService } from '../notification-service/notification.service';
import { ChangePassword } from '../model/ChangePassword';
import { UserService } from '../user.service';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css', '../../app.component.css']
})
export class ChangePasswordComponent {
  changePasswordForm: FormGroup = new FormGroup({
    oldPassword: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    newPassword: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    repeatNewPassword: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  @Output() formClosed = new EventEmitter<boolean>();

  constructor(
    private driverService: DriverService,
    private notificationService: NotificationService,
    private userService: UserService
  ) {}

  closeDriverChangingPassword(): void {
    this.driverService.setChangingPassword(false);
    this.formClosed.emit(true);
  }

  changePassword(): void {
    if (this.changePasswordForm.valid) {
      if (this.changePasswordForm.value.oldPassword === this.changePasswordForm.value.newPassword) {
        this.notificationService.createNotification("The new password must be different from the old one.", 3000);
        return;
      }
      if (this.changePasswordForm.value.newPassword !== this.changePasswordForm.value.repeatNewPassword) {
        this.notificationService.createNotification("The new passwords do not match.", 3000);
        return;
      }

      this.sendRequest();
    } else {
      this.notificationService.createNotification("You have not filled in all the fields.", 3000);
      return;
    }
  }

  sendRequest(): void {
    const request: ChangePassword = {
      oldPassword: this.changePasswordForm.value.oldPassword,
      newPassword: this.changePasswordForm.value.newPassword
    };
    this.userService.changePassword(parseInt(<string>sessionStorage.getItem("user_id")), request).subscribe({
      next: () => {
        this.notificationService.createNotification('You have successfully changed your password.', 5000);
        this.closeDriverChangingPassword();
      },
      error: (error) => {
        if (error.error.message.includes("matching")) {
          this.notificationService.createNotification('The old password is incorrect.', 5000);
        } else if (error.error.message.includes("valid")) {
          if (error.error.message.includes("old")) {
            this.notificationService.createNotification('The old password is incorrect.', 5000);
          } else {
            this.notificationService.createNotification('The new password is not secure enough. ' +
              'It must be between 8 and 15 characters long, with at least one uppercase letter and one number. ' +
              'It must not contain invalid characters.', 5000);
          }
        } else {
          this.notificationService.createNotification('Password change failed. Errors: ' + error.error.message, 5000);
        }
      }
    });
  }
}
