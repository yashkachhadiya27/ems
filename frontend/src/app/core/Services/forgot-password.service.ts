import { inject, Injectable } from '@angular/core';
import { publicUrl } from '../Constants/apiUrl';
import { HttpClient } from '@angular/common/http';
import { forgotPasswordResp } from '../Interfaces/AuthModel';
@Injectable({
  providedIn: 'root',
})
export class ForgotPasswordService {
  private http = inject(HttpClient);
  constructor() {}
  verifyEmail(email: string, status: string = 'old') {
    return this.http.get<forgotPasswordResp>(
      `${publicUrl}/verifyEmail/${email}/${status}`
    );
  }
  verifyOTP(otp: number, email: string) {
    return this.http.get<forgotPasswordResp>(
      `${publicUrl}/verifyOTP/${otp}/${email}`
    );
  }

  changePassword(email: string, data: FormData) {
    return this.http.put<forgotPasswordResp>(
      `${publicUrl}/changePassword/${email}`,
      data
    );
  }

  resendOTP(email: string) {
    return this.http.get<forgotPasswordResp>(
      `${publicUrl}/resendForgotOTP/${email}`
    );
  }
}
