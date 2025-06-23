import { inject, Injectable } from '@angular/core';
import { adminUrl, publicUrl } from '../Constants/apiUrl';
import { HttpClient, HttpParams } from '@angular/common/http';
import { paginationSorting } from '../Interfaces/AuthModel';
@Injectable({
  providedIn: 'root',
})
export class DepartmentService {
  private http = inject(HttpClient);
  constructor() {}
  addDepartment(data: FormData) {
    return this.http.post(`${adminUrl}/addDepartment`, data);
  }
  getAllDepartment(keyword: string | null, data: paginationSorting) {
    let params = new HttpParams()
      .set('keyword', keyword ? keyword : '')
      .set('pageNumber', data.pageNumber)
      .set('pageSize', data.pageSize)
      .set('sortBy', data.sortBy)
      .set('sortOrder', data.sortOrder);
    return this.http.get(`${adminUrl}/getAllDepartment`, { params });
  }
  deleteDepartment(id: number) {
    return this.http.delete(`${adminUrl}/deleteDepartment/${id}`);
  }
  editDepartment(id: number, data: FormData) {
    return this.http.patch(`${adminUrl}/editDepartment/${id}`, data);
  }
  getDepartments() {
    return this.http.get(`${publicUrl}/getDepartments`);
  }
}
