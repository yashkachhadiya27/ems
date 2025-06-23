import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { adminUrl, adminEmployeeUrl, employeeUrl } from '../Constants/apiUrl';
import { paginationSorting, RegisterForm } from '../Interfaces/AuthModel';
import {
  EditEmployee,
  LeaveDashboardDTO,
  User,
} from '../Interfaces/EmployeeModel';
import { take } from 'rxjs';
import { idName, project, UserProjectDetail } from '../Interfaces/ProjectModel';
import { ChatUser } from '../Interfaces/MessageModel';
@Injectable({
  providedIn: 'root',
})
export class EmployeeService {
  private http = inject(HttpClient);
  constructor() {}
  getAllEmployee(keyword: string | null, data: paginationSorting) {
    let params = new HttpParams()
      .set('keyword', keyword ? keyword : '')
      .set('pageNumber', data.pageNumber)
      .set('pageSize', data.pageSize)
      .set('sortBy', data.sortBy)
      .set('sortOrder', data.sortOrder);
    return this.http.get(`${adminUrl}/getAllEmployee`, { params });
  }
  getSearchedEmployees(keyword: string | null, data: paginationSorting) {
    let params = new HttpParams()
      .set('keyword', keyword ? keyword : '')
      .set('pageNumber', data.pageNumber)
      .set('pageSize', data.pageSize)
      .set('sortBy', data.sortBy)
      .set('sortOrder', data.sortOrder);
    return this.http.get(`${adminUrl}/getSearchedEmployees`, { params });
  }
  getEmployeeImage(link: string) {
    return this.http.get(link, { responseType: 'blob' });
  }
  getProfileImage() {
    return this.http.get(`${adminEmployeeUrl}/profileImage`, {
      responseType: 'blob',
    });
  }
  deleteEmployee(email: string) {
    return this.http.delete(`${adminUrl}/deleteEmployee/${email}`);
  }
  getEditEmployeeDetail(email: string) {
    return this.http.get<EditEmployee>(
      `${adminUrl}/getEditEmployeeDetail/${email}`
    );
  }
  editEmployeeDetail(email: string, emp: FormData) {
    return this.http.patch(`${adminUrl}/editEmployee/${email}`, emp);
  }
  getUserData(email: string) {
    return this.http.get<User>(`${adminEmployeeUrl}/getUserData/${email}`);
  }
  addEmployee(formData: FormData) {
    return this.http.post(`${adminUrl}/addEmployee`, formData);
  }
  updateProfile(userId: number, data: FormData) {
    return this.http.patch(`${employeeUrl}/updateProfile/${userId}`, data);
  }
  downloadCsv() {
    return this.http.get(`${adminUrl}/download/employees`, {
      responseType: 'blob',
      observe: 'response',
    });
  }
  getNotDoneTasks(userId: number) {
    return this.http.get(`${employeeUrl}/notDoneTaks/${userId}`);
  }
  getTodaysBirthdays() {
    return this.http.get<string[]>(`${adminEmployeeUrl}/todayBirthday`);
  }
  getTodayWorkAnniversarries() {
    return this.http.get<string[]>(
      `${adminEmployeeUrl}/todayWorkAnniversarries`
    );
  }
  getIdName() {
    return this.http.get<idName[]>(`${employeeUrl}/getIdName`);
  }
  addProject(data: project) {
    return this.http.post(`${employeeUrl}/addProject`, data);
  }
  getProjects(userId: number) {
    return this.http.get<project[]>(`${employeeUrl}/getProject/${userId}`);
  }
  getEmployeesInProject(userId: number) {
    return this.http.get<UserProjectDetail[]>(
      `${employeeUrl}/getEmployeesInProject/${userId}`
    );
  }
  getLeaveDashboard(registerId: number) {
    return this.http.get<LeaveDashboardDTO>(
      `${employeeUrl}/dashboard/${registerId}`
    );
  }
  getTotalPendingLeaveOfEmployee(registerId: number) {
    return this.http.get(
      `${employeeUrl}/totalPendingLeaveOfEmployee/${registerId}`
    );
  }
  getNotifications(registerId: number) {
    return this.http.get<Notification[]>(
      `${employeeUrl}/notificationForEmp/${registerId}`
    );
  }
  removeNotification(registerId: number) {
    return this.http.delete(`${employeeUrl}/removeNotification/${registerId}`);
  }
  searchUsersByEmail(email: string) {
    return this.http.get<ChatUser[]>(`${employeeUrl}/search?query=${email}`);
  }
}
