import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Notice } from '../Interfaces/NoticeModel';
import { adminEmployeeUrl, adminUrl } from '../Constants/apiUrl';

@Injectable({
  providedIn: 'root',
})
export class NoticeService {
  private http = inject(HttpClient);
  constructor() {}
  addNotice(notice: Notice) {
    return this.http.post(`${adminUrl}/addNotice`, notice);
  }
  getActiveNotices() {
    return this.http.get<Notice[]>(`${adminEmployeeUrl}/getActiveNotices`);
  }
}
