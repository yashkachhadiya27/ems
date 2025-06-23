import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import * as CryptoJS from 'crypto-js';

@Injectable({
  providedIn: 'root',
})
export class RoleService {
  public roleSubject: BehaviorSubject<string | null> = new BehaviorSubject<
    string | null
  >(null);
  isLoggedin: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  private encryptionKey = 'HelloFrom@Argusoft672!';

  constructor() {
    const encryptedRole = localStorage.getItem('role');
    if (encryptedRole) {
      const role = CryptoJS.AES.decrypt(
        encryptedRole,
        this.encryptionKey
      ).toString(CryptoJS.enc.Utf8);
      this.roleSubject.next(role);
    }
  }

  setRole(role: string): void {
    const encryptedRole = CryptoJS.AES.encrypt(
      role,
      this.encryptionKey
    ).toString();
    localStorage.setItem('role', encryptedRole);
    this.roleSubject.next(role);
  }

  getRole(): void {
    const encryptedRole = localStorage.getItem('role');
    if (encryptedRole) {
      const role = CryptoJS.AES.decrypt(
        encryptedRole,
        this.encryptionKey
      ).toString(CryptoJS.enc.Utf8);
      this.roleSubject.next(role);
    }
  }

  // clearRole(): void {
  //   localStorage.removeItem('role');
  //   this.roleSubject.next(null);
  // }
}
