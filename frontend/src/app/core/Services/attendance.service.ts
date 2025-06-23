import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { adminEmployeeUrl, adminUrl, employeeUrl } from '../Constants/apiUrl';
import { AbsentEmployees, LeaveEmployees } from '../Interfaces/EmployeeModel';

@Injectable({
  providedIn: 'root',
})
export class AttendanceService {
  private http = inject(HttpClient);
  getStatus(userId: number | null): Observable<boolean> {
    return this.http.get<boolean>(`${employeeUrl}/attendance/status/${userId}`);
  }

  markIn(userId: number): Observable<any> {
    return this.http.post(`${employeeUrl}/attendance/in/${userId}`, {});
  }

  markOut(userId: number): Observable<any> {
    return this.http.post(`${employeeUrl}/attendance/out/${userId}`, {});
  }

  getAttendanceByMonth(
    userId: number,
    month: number,
    year: number
  ): Observable<any> {
    let params = new HttpParams().set('month', month).set('year', year);
    return this.http.get(
      `${employeeUrl}/attendance/attedanceByMonth/${userId}`,
      {
        params,
      }
    );
  }

  getAverageAttendance(
    userId: number,
    month: number,
    year: number
  ): Observable<any> {
    let params = new HttpParams().set('month', month).set('year', year);
    return this.http.get(
      `${employeeUrl}/attendance/averagesForMonth/${userId}`,
      {
        params,
      }
    );
  }

  getLogsForDay(userId: number, date: string): Observable<any> {
    let params = new HttpParams().set('date', date);
    return this.http.get(`${employeeUrl}/attendance/logs/${userId}`, {
      params,
    });
  }

  getAbsentEmployees(date: string) {
    const params = new HttpParams().set('date', date);
    return this.http.get<AbsentEmployees[]>(
      `${adminEmployeeUrl}/attendance/employees-absent`,
      { params }
    );
  }
  getEmployeesOnLeave(date: string) {
    const params = new HttpParams().set('date', date);
    return this.http.get<LeaveEmployees[]>(
      `${adminEmployeeUrl}/employees-on-leave`,
      { params }
    );
  }
  getEmployeesOnWFH(date: string) {
    const params = new HttpParams().set('date', date);
    return this.http.get<LeaveEmployees[]>(
      `${adminEmployeeUrl}/employees-wfh`,
      { params }
    );
  }
}
