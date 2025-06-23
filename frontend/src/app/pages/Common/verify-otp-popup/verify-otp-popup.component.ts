import { NgClass } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../../core/Services/auth.service';
import { ForgotPasswordService } from '../../../core/Services/forgot-password.service';
import { ChangePasswordPopupComponent } from '../change-password-popup/change-password-popup.component';
import { RoleService } from '../../../core/Services/role.service';
import { TokenService } from '../../../core/Services/token.service';
@Component({
  selector: 'app-verify-otp-popup',
  standalone: true,
  imports: [ReactiveFormsModule, MatButtonModule, MatDialogModule, NgClass],
  templateUrl: './verify-otp-popup.component.html',
  styleUrl: './verify-otp-popup.component.css',
})
export class VerifyOtpPopupComponent implements OnInit {
  private data = inject(MAT_DIALOG_DATA);
  private dialog = inject(MatDialog);
  private authService = inject(AuthService);
  private forgotPassService = inject(ForgotPasswordService);
  private roleService = inject(RoleService);
  private tokenService = inject(TokenService);
  private toast = inject(ToastrService);
  private router = inject(Router);
  countdown = signal<number>(120);
  isResendBtnVisible = signal<boolean>(false);
  timer: any;
  isLoading = signal<boolean>(false);

  constructor(private ref: MatDialogRef<VerifyOtpPopupComponent>) {}
  ngOnInit(): void {
    this.startTimer();
  }
  verifyOtpForm = new FormGroup({
    otp: new FormControl('', [
      Validators.required,
      Validators.pattern('^[0-9]{6}'),
    ]),
  });
  get otp(): FormControl {
    return this.verifyOtpForm.get('otp') as FormControl;
  }
  submitVerifyOtpForm() {
    if (this.data.type == 'submit') {
      this.data.formData.append('otpCode', this.otp.value);
      this.authService.registerEmployee(this.data.formData).subscribe({
        next: (res) => {
          this.toast.success('Registered Successfully', 'Success', {
            timeOut: 3000,
            closeButton: true,
          });
          this.ref.close('success');
        },
        error: (error) => {
          this.toast.error('Error Occurred While Registering User', 'Error', {
            timeOut: 3000,
            closeButton: true,
          });
          this.ref.close('failure');
        },
      });
    } else if (this.data.type == 'verify') {
      this.forgotPassService
        .verifyOTP(this.otp.value, this.data.email)
        .subscribe((res) => {
          const tempData = res;
          if (tempData.status == true) {
            this.ref.close();
            this.toast.success('OTP Matched', 'Success', {
              timeOut: 5000,
              closeButton: true,
            });
            this.dialog.open(ChangePasswordPopupComponent, {
              width: '50%',
              enterAnimationDuration: '350ms',
              exitAnimationDuration: '350ms',
              data: {
                email: this.data.email,
              },
            });
          } else if (tempData.message == 'Invalid Email') {
            this.toast.error(tempData.email + ' Not Exist', 'Invalid Email', {
              timeOut: 5000,
              closeButton: true,
            });
          } else if (tempData.message == 'Not Match') {
            this.toast.error('OTP Not Exist', 'Not Match', {
              timeOut: 5000,
              closeButton: true,
            });
          } else {
            this.toast.error('OTP Expired or Invalid!', 'Error', {
              timeOut: 5000,
              closeButton: true,
            });
          }
        });
    } else if (this.data.type === 'update') {
      this.authService
        .updateEmail(this.data.oldEmail, this.data.newEmail, this.otp.value)
        .subscribe({
          next: (res) => {
            this.toast.success('Please Login with New Email', 'Mail Changed', {
              timeOut: 5000,
              closeButton: true,
            });
            this.tokenService.clearTokens();
            this.roleService.roleSubject.next(null);
            this.roleService.isLoggedin.next(false);
            this.router.navigate(['/login']);
          },
          error: (err) => {
            this.toast.error('Something went wrong!', 'Error', {
              timeOut: 5000,
              closeButton: true,
            });
          },
        });
    }
    this.ref.close(true);
  }
  startTimer() {
    this.timer = setInterval(() => {
      if (this.countdown() > 0) {
        this.countdown.update((val: number) => val - 1);
      } else {
        this.isResendBtnVisible.set(true);
        clearInterval(this.timer);
      }
    }, 1000);
  }
  formatTime(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    const formattedTime = `0${minutes} : ${remainingSeconds} `;
    return formattedTime;
  }
  resendOtp() {
    this.isLoading.set(true);
    const email: string =
      this.data.type == 'submit'
        ? this.data.formData.get('email')
        : this.data.email;
    console.log(email);

    this.forgotPassService.resendOTP(email).subscribe((res) => {
      const tempData = res;
      if (tempData.status == true) {
        this.isLoading.set(false);
        this.countdown.set(120);
        this.isResendBtnVisible.set(false);
        this.startTimer();
        this.toast.success('OTP Sent To ' + tempData.email, 'Success', {
          timeOut: 5000,
          closeButton: true,
        });
      } else {
        this.isLoading.set(false);
        this.toast.error(tempData.email + ' Not Exist', 'Invalid Email', {
          timeOut: 5000,
          closeButton: true,
        });
      }
    });
  }
}
