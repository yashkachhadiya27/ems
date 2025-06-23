import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Router, RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { publicUrl } from '../../../core/Constants/apiUrl';
import { login } from '../../../core/Interfaces/AuthModel';
import { AuthService } from '../../../core/Services/auth.service';
import { RoleService } from '../../../core/Services/role.service';
import { VerifyEmailPopupComponent } from '../verify-email-popup/verify-email-popup.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    RouterLink,
    MatCardModule,
    MatDialogModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  onClick() {
    window.location.href = `${publicUrl}/oauth2/authorization/google`;
  }
  private authService = inject(AuthService);
  private router = inject(Router);
  private roleService = inject(RoleService);
  private dialog = inject(MatDialog);
  private toastr = inject(ToastrService);
  loginForm = new FormGroup({
    email: new FormControl('', [
      Validators.required,
      Validators.pattern(
        '^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$'
      ),
    ]),
    password: new FormControl('', [
      Validators.required,
      Validators.pattern(
        /^(?=\D*\d)(?=[^a-z]*[a-z])(?=.*[$@$!%*?&+^])(?=[^A-Z]*[A-Z]).{8,}$/
      ),
    ]),
  });
  get email(): FormControl {
    return this.loginForm.get('email') as FormControl;
  }
  get password(): FormControl {
    return this.loginForm.get('password') as FormControl;
  }
  forgotPassword() {
    this.dialog.open(VerifyEmailPopupComponent, {
      width: '50%',
      enterAnimationDuration: '350ms',
      exitAnimationDuration: '350ms',
      data: {
        placeholder: 'Current Email',
      },
    });
  }
  submitLoginForm() {
    if (this.loginForm.valid) {
      const loginData: login = this.loginForm.value as login;

      this.authService.login(loginData).subscribe({
        next: (response) => {
          localStorage.setItem('email', this.email.value);
          this.roleService.isLoggedin.next(true);
          this.toastr.success('Loggedin Successfully', 'Success', {
            timeOut: 3000,
            closeButton: true,
          });
          this.roleService.roleSubject.subscribe((role) => {
            if (role === 'ADMIN') {
              this.router.navigate(['/admin/dashboard']);
            } else {
              this.router.navigate(['/employee/dashboard']);
            }
          });
        },
        error: (err) => {
          this.toastr.error('Email or Password is invalid', 'Error', {
            timeOut: 3000,
            closeButton: true,
          });
        },
      });
    }
  }
}
