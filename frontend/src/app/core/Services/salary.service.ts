import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { adminUrl, employeeUrl } from '../Constants/apiUrl';
import { pagination } from '../Interfaces/AuthModel';

@Injectable({
  providedIn: 'root',
})
export class SalaryService {
  private http = inject(HttpClient);
  constructor() {}

  getAllEmpDetails(keyword: string, data: pagination) {
    let params = new HttpParams()
      .set('keyword', keyword ? keyword : '')
      .set('pageNumber', data.pageNumber)
      .set('pageSize', data.pageSize);
    return this.http.get(`${adminUrl}/getEmpDetailsSalary`, { params });
  }
  getEmpSalaryPayDetails(keyword: string, date: Date, data: pagination) {
    const actualDate = new Date(date);
    let formattedDate = actualDate
      ? `${actualDate.getMonth() + 1}-${actualDate.getFullYear()}`
      : '';

    let params = new HttpParams()
      .set('keyword', keyword ? keyword : '')
      .set('date', formattedDate)
      .set('pageNumber', data.pageNumber)
      .set('pageSize', data.pageSize);
    return this.http.get(`${adminUrl}/getEmpSalaryPayDetails`, { params });
  }
  paySalary(salaryId: number) {
    return this.http.patch(`${adminUrl}/paySalary/${salaryId}`, {});
  }
  addEmpSalary(userId: number, data: FormData) {
    return this.http.post(`${adminUrl}/addEmpSalary/${userId}`, data);
  }
  employeeSalary(userId: number, date: any, data: pagination) {
    let params = new HttpParams()
      .set('date', date.slice(0, 7))
      .set('pageNumber', data.pageNumber)
      .set('pageSize', data.pageSize);
    return this.http.get(`${employeeUrl}/getEmpAllSalary/${userId}`, {
      params,
    });
  }
}
