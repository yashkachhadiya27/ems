import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { employeeUrl } from '../Constants/apiUrl';
import { Todo } from '../Interfaces/TodoModel';

@Injectable({
  providedIn: 'root',
})
export class TodoService {
  private http = inject(HttpClient);
  addTask(data: Todo) {
    return this.http.post(`${employeeUrl}/addTask`, data);
  }
  getAllTask(id: number) {
    return this.http.get(`${employeeUrl}/getAllTask/${id}`);
  }
  updateTaskStatus(taskId: number, status: string) {
    let params = new HttpParams().set('status', status);
    return this.http.patch(`${employeeUrl}/changeStatus/${taskId}`, null, {
      params,
    });
  }
  deleteTask(taskId: number | undefined) {
    return this.http.delete(`${employeeUrl}/deleteTask/${taskId}`);
  }
  updateTask(taskId: number, description: string) {
    return this.http.patch(`${employeeUrl}/updateTask/${taskId}`, description, {
      headers: { 'Content-Type': 'text/plain' },
    });
  }
}
