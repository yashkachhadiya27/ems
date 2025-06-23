import { HttpClient, HttpResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, map, Observable, take, throwError } from 'rxjs';
import { adminEmployeeUrl, employeeUrl, publicUrl } from '../Constants/apiUrl';
import { login, loginResponse, RegisterForm } from '../Interfaces/AuthModel';
import { RoleService } from './role.service';
import { TokenService } from './token.service';
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private tokenService = inject(TokenService);
  private roleService = inject(RoleService);

  constructor() {}

  sendOtp(email: string) {
    return this.http.post(`${publicUrl}/send-otp/${email}`, {});
  }
  registerEmployee(formData: RegisterForm) {
    return this.http.post(`${publicUrl}/register`, formData);
  }
  isUserExist(email: string) {
    return this.http.get<{ message: string; code: number }>(
      `${publicUrl}/isUserExist/${email}`
    );
  }
  login(data: login) {
    return this.http.post<loginResponse>(`${publicUrl}/login`, data).pipe(
      map((response: loginResponse) => {
        this.tokenService.setTokens(
          response.accessToken,
          response.refreshToken,
          response.role
        );
        this.roleService.setRole(response.role);
        localStorage.setItem('empId', response.empId + '');
        return response;
      })
    );
  }
  getToken(code: string) {
    return this.http
      .get<loginResponse>(`${publicUrl}/oauth2/callback?code=${code}`, {
        observe: 'response',
      })
      .pipe(
        take(1),
        map((response: HttpResponse<any>) => {
          if (
            response.status === 200 &&
            response.body !== null &&
            response.body.code !== 111
          ) {
            this.tokenService.setTokens(
              response.body.accessToken,
              response.body.refreshToken,
              response.body.role
            );
            this.roleService.setRole(response.body.role);
            localStorage.setItem('empId', response.body.empId + '');
            if (response.body.role === 'ADMIN') {
              localStorage.setItem('email', response.body.email);
              return 'ADMIN';
            } else if (response.body.role === 'USER') {
              localStorage.setItem('email', response.body.email);
              return 'USER';
            }
            return 1;
          } else if (response.body.code === 111) {
            return 2;
          } else {
            return 3;
          }
        })
      );
  }
  refreshToken(): Observable<string> {
    const refreshToken = this.tokenService.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    return this.http
      .post<{ accessToken: string }>(`${publicUrl}/refresh-token`, {
        refreshToken,
      })
      .pipe(
        map((response: { accessToken: string }) => {
          if (!response || !response.accessToken) {
            throw new Error('Failed to refresh access token');
          }
          this.tokenService.setAccessToken(response.accessToken);
          return response.accessToken;
        }),
        catchError((error) => {
          this.tokenService.clearTokens();
          return throwError(
            () =>
              new Error(
                'Refresh token is invalid or expired. Please log in again.'
              )
          );
        })
      );
  }
  logout(): void {
    const refreshToken = this.tokenService.getRefreshToken();
    this.http.post(`${publicUrl}/logout`, { refreshToken }).subscribe({
      next: (res) => {},
    });
    this.tokenService.clearTokens();
  }

  isAuthenticated(): boolean {
    return !!this.tokenService.getAccessToken();
  }

  getUserNameDepartment(email: string | null) {
    return this.http.get<{ image: string; name: string; department: string }>(
      `${adminEmployeeUrl}/getUserNameDepartment/${email}`
    );
  }

  updateEmail(oldEmail: string, newEmail: string, otp: number) {
    return this.http.patch(`${employeeUrl}/updateEmployeeEmail/${oldEmail}`, {
      newEmail,
      otp,
    });
  }
  updateProfileImage(email: string, data: FormData) {
    return this.http.patch(`${employeeUrl}/updateProfileImage/${email}`, data);
  }
}
