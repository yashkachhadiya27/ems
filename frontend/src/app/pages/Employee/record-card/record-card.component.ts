import { DatePipe } from '@angular/common';
import { Component, inject, input, model, OnInit, signal } from '@angular/core';
import { AttendanceService } from '../../../core/Services/attendance.service';
import { EmployeeService } from '../../../core/Services/employee.service';
import {
  AbsentEmployees,
  LeaveEmployees,
} from '../../../core/Interfaces/EmployeeModel';

@Component({
  selector: 'app-record-card',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './record-card.component.html',
  styleUrl: './record-card.component.css',
})
export class RecordCardComponent {
  private attendanceService = inject(AttendanceService);
  private employeeService = inject(EmployeeService);
  title = input.required<string>();
  records = model.required<any>();
  recordDate = signal<Date>(new Date());
  today: Date;
  constructor() {
    this.today = new Date();
  }
  nextDayRecord(event: any) {
    event.stopPropagation();
    this.recordDate().setDate(this.recordDate().getDate() + 1);
    if (this.recordDate().getDay() === 0) {
      this.recordDate().setDate(this.recordDate().getDate() + 1);
    }
    this.recordDate.set(new Date(this.recordDate()));

    this.getRecords(this.recordDate());
  }
  previousDayRecord(event: any) {
    event.stopPropagation();
    this.recordDate().setDate(this.recordDate().getDate() - 1);
    if (this.recordDate().getDay() === 0) {
      this.recordDate().setDate(this.recordDate().getDate() - 1);
    }
    this.recordDate.set(new Date(this.recordDate()));
    this.getRecords(this.recordDate());
  }
  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  getRecords(date: Date) {
    const recordDate: string = this.formatDate(date);
    switch (this.title()) {
      case 'Absent Records': {
        this.attendanceService.getAbsentEmployees(recordDate).subscribe({
          next: (res: AbsentEmployees[]) => {
            this.records.set(res);
            this.records().forEach((employee: AbsentEmployees) => {
              this.employeeService.getEmployeeImage(employee.image).subscribe({
                next: (blob) => {
                  const objectURL = URL.createObjectURL(blob);
                  employee.image = objectURL;
                },
              });
            });
          },
        });
        break;
      }
      case 'Leaves Records': {
        this.attendanceService.getEmployeesOnLeave(recordDate).subscribe({
          next: (res: LeaveEmployees[]) => {
            this.records.set(res);
            this.records().forEach((employee: AbsentEmployees) => {
              this.employeeService.getEmployeeImage(employee.image).subscribe({
                next: (blob) => {
                  const objectURL = URL.createObjectURL(blob);
                  employee.image = objectURL;
                },
              });
            });
          },
        });
        break;
      }
      case 'WFH Records': {
        this.attendanceService.getEmployeesOnWFH(recordDate).subscribe({
          next: (res: LeaveEmployees[]) => {
            this.records.set(res);
            this.records().forEach((employee: AbsentEmployees) => {
              this.employeeService.getEmployeeImage(employee.image).subscribe({
                next: (blob) => {
                  const objectURL = URL.createObjectURL(blob);
                  employee.image = objectURL;
                },
              });
            });
          },
        });
        break;
      }
    }
  }
}
