import { Injectable } from '@angular/core';
import * as CryptoJS from 'crypto-js';

@Injectable({
  providedIn: 'root',
})
export class TokenService {
  private encryptionKey = 'HelloFrom@Argusoft672!';

  setTokens(accessToken: string, refreshToken: string, role: string): void {
    const encryptedAccessToken = CryptoJS.AES.encrypt(
      accessToken,
      this.encryptionKey
    ).toString();
    const encryptedRefreshToken = CryptoJS.AES.encrypt(
      refreshToken,
      this.encryptionKey
    ).toString();
    const encryptedRole = CryptoJS.AES.encrypt(
      role,
      this.encryptionKey
    ).toString();

    localStorage.setItem('accessToken', encryptedAccessToken);
    localStorage.setItem('refreshToken', encryptedRefreshToken);
    localStorage.setItem('role', encryptedRole);
  }

  getAccessToken(): string | null {
    const token = localStorage.getItem('accessToken');
    return token
      ? CryptoJS.AES.decrypt(token, this.encryptionKey).toString(
          CryptoJS.enc.Utf8
        )
      : null;
  }

  setAccessToken(token: string): void {
    const encryptedAccessToken = CryptoJS.AES.encrypt(
      token,
      this.encryptionKey
    ).toString();
    localStorage.setItem('accessToken', encryptedAccessToken);
  }

  getRefreshToken(): string | null {
    const token = localStorage.getItem('refreshToken');
    return token
      ? CryptoJS.AES.decrypt(token, this.encryptionKey).toString(
          CryptoJS.enc.Utf8
        )
      : null;
  }

  getRole(): string | null {
    const token = localStorage.getItem('role');
    return token
      ? CryptoJS.AES.decrypt(token, this.encryptionKey).toString(
          CryptoJS.enc.Utf8
        )
      : null;
  }

  clearTokens(): void {
    localStorage.clear();
  }
}
