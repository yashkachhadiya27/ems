import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { adminUrl } from '../Constants/apiUrl';

@Injectable({
  providedIn: 'root',
})
export class AdminDashboardService {
  private http = inject(HttpClient);
  constructor() {}

  totalDepartment() {
    return this.http.get(`${adminUrl}/totalDepartment`);
  }
  totalEmployee() {
    return this.http.get(`${adminUrl}/totalEmployee`);
  }
  totalPendingLeave() {
    return this.http.get(`${adminUrl}/totalPendingLeave`);
  }
  getDepartmentEmployeeCount() {
    return this.http.get(`${adminUrl}/department-count`);
  }
}
