import { NgClass } from '@angular/common';
import { Component, Inject, inject } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { forgotPasswordResp } from '../../../core/Interfaces/AuthModel';
import { ForgotPasswordService } from '../../../core/Services/forgot-password.service';
@Component({
  selector: 'app-change-password-popup',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass, MatButtonModule, MatDialogModule],
  templateUrl: './change-password-popup.component.html',
  styleUrl: './change-password-popup.component.css',
})
export class ChangePasswordPopupComponent {
  toast: ToastrService = inject(ToastrService);
  private forgotPassService = inject(ForgotPasswordService);
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private ref: MatDialogRef<ChangePasswordPopupComponent>
  ) {}
  changePasswordForm = new FormGroup(
    {
      password: new FormControl('', [
        Validators.required,
        Validators.pattern(
          /^(?=\D*\d)(?=[^a-z]*[a-z])(?=.*[$@$!%*?&])(?=[^A-Z]*[A-Z]).{8,}$/
        ),
      ]),
      cPassword: new FormControl('', [Validators.required]),
    },
    { validators: this.passwordMatchValidator }
  );
  get password(): FormControl {
    return this.changePasswordForm.get('password') as FormControl;
  }
  get cPassword(): FormControl {
    return this.changePasswordForm.get('cPassword') as FormControl;
  }
  passwordMatchValidator(control: AbstractControl) {
    const passwordControl = control.get('password');
    const confirmPasswordControl = control.get('cPassword');

    if (
      (passwordControl?.touched || passwordControl?.dirty) &&
      (confirmPasswordControl?.touched || confirmPasswordControl?.dirty)
    ) {
      const password = passwordControl?.value;
      const confirmPassword = confirmPasswordControl?.value;

      if (!password || !confirmPassword) {
        return null;
      }

      return password === confirmPassword ? null : { mismatch: true };
    }

    return null;
  }
  submitChangePasswordForm() {
    const formData = new FormData();
    formData.append('newPassword', this.password.value);
    this.forgotPassService.changePassword(this.data.email, formData).subscribe({
      next: (res: forgotPasswordResp) => {
        const tempData = res;
        if (tempData.status == true) {
          this.ref.close();
          this.toast.success('Password Changed Successfully', 'Success', {
            timeOut: 5000,
            closeButton: true,
          });
        }
      },
    });
  }
}
