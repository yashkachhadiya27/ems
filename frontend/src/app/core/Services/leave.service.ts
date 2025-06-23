import { inject, Injectable } from '@angular/core';
import { adminUrl, employeeUrl } from '../Constants/apiUrl';
import { HttpClient, HttpParams } from '@angular/common/http';
import { pagination, paginationSorting } from '../Interfaces/AuthModel';
@Injectable({
  providedIn: 'root',
})
export class LeaveService {
  private http = inject(HttpClient);
  constructor() {}
  applyLeave(email: string, data: FormData) {
    return this.http.post<{ message: string; code: number }>(
      `${employeeUrl}/leaveRequest/${email}`,
      data
    );
  }
  getAllLeave(keyword: string | null, data: paginationSorting) {
    let params = new HttpParams()
      .set('keyword', keyword ? keyword : '')
      .set('pageNumber', data.pageNumber)
      .set('pageSize', data.pageSize)
      .set('sortBy', data.sortBy)
      .set('sortOrder', data.sortOrder);
    return this.http.get(`${adminUrl}/getAllLeave`, { params });
  }
  getAllPendingLeave(keyword: string | null, data: paginationSorting) {
    let params = new HttpParams()
      .set('keyword', keyword ? keyword : '')
      .set('pageNumber', data.pageNumber)
      .set('pageSize', data.pageSize)
      .set('sortBy', data.sortBy)
      .set('sortOrder', data.sortOrder);
    return this.http.get(`${adminUrl}/getAllPendingLeave`, { params });
  }
  approveLeave(id: number) {
    return this.http.post(`${adminUrl}/approveLeave/${id}`, {});
  }
  rejectLeave(id: number) {
    return this.http.post(`${adminUrl}/rejectLeave/${id}`, {});
  }

  getAllLeaveOfEmployee(userId: number, data: pagination) {
    let params = new HttpParams()
      .set('pageNumber', data.pageNumber)
      .set('pageSize', data.pageSize);
    return this.http.get(`${employeeUrl}/getAllLeaveOfEmployee/${userId}`, {
      params,
    });
  }
  updateLeaveRequest(id: number, data: FormData) {
    return this.http.patch<{ message: string; code: number }>(
      `${employeeUrl}/updateLeave/${id}`,
      data
    );
  }
  deleteLeave(id: number) {
    return this.http.delete(`${employeeUrl}/deleteLeave/${id}`);
  }
}
