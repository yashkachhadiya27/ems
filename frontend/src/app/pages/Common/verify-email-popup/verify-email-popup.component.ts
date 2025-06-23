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

import { ToastrService } from 'ngx-toastr';
import { forgotPasswordResp } from '../../../core/Interfaces/AuthModel';
import { ForgotPasswordService } from '../../../core/Services/forgot-password.service';
import { VerifyOtpPopupComponent } from '../verify-otp-popup/verify-otp-popup.component';
@Component({
  selector: 'app-verify-email-popup',
  standalone: true,
  imports: [ReactiveFormsModule, MatButtonModule, MatDialogModule, NgClass],
  templateUrl: './verify-email-popup.component.html',
  styleUrl: './verify-email-popup.component.css',
})
export class VerifyEmailPopupComponent implements OnInit {
  private dialog = inject(MatDialog);
  private toastr = inject(ToastrService);
  private data = inject(MAT_DIALOG_DATA);
  private forgotPassService = inject(ForgotPasswordService);
  isLoading = signal<boolean>(false);
  placeholder = signal<string>('Current Email');
  constructor(private ref: MatDialogRef<VerifyEmailPopupComponent>) {}
  ngOnInit(): void {
    if (this.data.placeholder) {
      this.placeholder.set(this.data.placeholder);
    } else {
      this.placeholder.set('Current Email');
    }
  }
  verifyEmailForm = new FormGroup({
    email: new FormControl('', [
      Validators.required,
      Validators.pattern(
        '^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$'
      ),
    ]),
  });
  get email(): FormControl {
    return this.verifyEmailForm.get('email') as FormControl;
  }
  submitVerifyEmailForm() {
    this.isLoading.set(true);
    let status;
    if (this.data.type) {
      status = this.data.type === 'update' ? 'new' : 'old';
    } else {
      status = 'old';
    }
    this.forgotPassService.verifyEmail(this.email.value, status).subscribe({
      next: (res: forgotPasswordResp) => {
        const tempData = res;
        if (tempData.status == true) {
          this.isLoading.set(false);
          this.ref.close();
          this.toastr.success('OTP Sent To ' + tempData.email, 'Success', {
            timeOut: 5000,
            closeButton: true,
          });
          if (this.data.type === 'update') {
            this.dialog.open(VerifyOtpPopupComponent, {
              width: '50%',
              enterAnimationDuration: '350ms',
              exitAnimationDuration: '350ms',
              data: {
                newEmail: this.email.value,
                oldEmail: this.data.oldEmail,
                type: this.data.type,
              },
            });
          } else {
            this.dialog.open(VerifyOtpPopupComponent, {
              width: '50%',
              enterAnimationDuration: '350ms',
              exitAnimationDuration: '350ms',
              data: {
                email: this.email.value,
                type: 'verify',
              },
            });
          }
        } else {
          this.isLoading.set(false);
          this.toastr.error(tempData.email + ' Not Exist', 'Invalid Email', {
            timeOut: 5000,
            closeButton: true,
          });
        }
      },
    });
  }
}
