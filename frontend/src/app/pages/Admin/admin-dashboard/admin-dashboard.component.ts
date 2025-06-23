import { CurrencyPipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import Highcharts from 'highcharts';
import {
  AbsentEmployees,
  LeaveEmployees,
} from '../../../core/Interfaces/EmployeeModel';
import { AdminDashboardService } from '../../../core/Services/admin-dashboard.service';
import { AttendanceService } from '../../../core/Services/attendance.service';
import { EmployeeService } from '../../../core/Services/employee.service';
import { RecordCardComponent } from '../../Employee/record-card/record-card.component';
import { RecordPopupComponent } from '../../Employee/record-popup/record-popup.component';
import { AddNoticePopupComponent } from '../add-notice-popup/add-notice-popup.component';
import { DashboardCardComponent } from '../dashboard-card/dashboard-card.component';
@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CurrencyPipe,
    RouterModule,
    RecordCardComponent,
    DashboardCardComponent,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css',
})
export class AdminDashboardComponent implements OnInit {
  private dashboardService = inject(AdminDashboardService);
  private employeeService = inject(EmployeeService);
  private attendanceService = inject(AttendanceService);
  private dialog = inject(MatDialog);
  Highcharts: typeof Highcharts = Highcharts;
  chartOptions!: Highcharts.Options;
  deptCount!: number;
  empCount!: number;
  empDeptData: any;
  pendingLeaveCount!: number;
  absentEmployees: AbsentEmployees[] = [];
  EmployeesOnLeave: LeaveEmployees[] = [];
  EmployeesOnWfh: LeaveEmployees[] = [];

  ngOnInit(): void {
    this.dashboardService.totalDepartment().subscribe((res) => {
      this.deptCount = res as number;
    });
    this.dashboardService.totalEmployee().subscribe((res) => {
      this.empCount = res as number;
    });
    this.dashboardService.totalPendingLeave().subscribe((res) => {
      this.pendingLeaveCount = res as number;
    });
    this.loadChart();
    this.attendanceService
      .getAbsentEmployees(this.formatDate(new Date()))
      .subscribe({
        next: (res: AbsentEmployees[]) => {
          this.absentEmployees = res;
          this.absentEmployees.forEach((employee: AbsentEmployees) => {
            this.employeeService.getEmployeeImage(employee.image).subscribe({
              next: (blob) => {
                const objectURL = URL.createObjectURL(blob);
                employee.image = objectURL;
              },
            });
          });
        },
      });
    this.attendanceService
      .getEmployeesOnLeave(this.formatDate(new Date()))
      .subscribe({
        next: (res: LeaveEmployees[]) => {
          this.EmployeesOnLeave = res;
          this.EmployeesOnLeave.forEach((employee: AbsentEmployees) => {
            this.employeeService.getEmployeeImage(employee.image).subscribe({
              next: (blob) => {
                const objectURL = URL.createObjectURL(blob);
                employee.image = objectURL;
              },
            });
          });
        },
      });
    this.attendanceService
      .getEmployeesOnWFH(this.formatDate(new Date()))
      .subscribe({
        next: (res: LeaveEmployees[]) => {
          this.EmployeesOnWfh = res;
          this.EmployeesOnWfh.forEach((employee: AbsentEmployees) => {
            this.employeeService.getEmployeeImage(employee.image).subscribe({
              next: (blob) => {
                const objectURL = URL.createObjectURL(blob);
                employee.image = objectURL;
              },
            });
          });
        },
      });
  }
  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }
  addNotice() {
    this.dialog.open(AddNoticePopupComponent, {
      width: '50%',
      enterAnimationDuration: '350ms',
      exitAnimationDuration: '350ms',
    });
  }
  loadChart() {
    this.dashboardService.getDepartmentEmployeeCount().subscribe((data) => {
      this.empDeptData = data;
      const chartData = this.empDeptData.map(
        (item: { department: any; employeeCount: any }) => ({
          name: item.department,
          y: item.employeeCount,
        })
      );

      this.chartOptions = {
        chart: {
          type: 'pie',
          renderTo: 'departmentChart',
          backgroundColor: 'transparent',
        },
        credits: {
          enabled: false,
        },
        title: {
          text: 'Total Employees by Department',
        },
        series: [
          {
            name: 'Employees',
            data: chartData,
            type: 'pie',
          },
        ],
        plotOptions: {
          pie: {
            dataLabels: {
              enabled: true,
              format: '<b>{point.name}</b>: {point.y}',
              style: {
                fontSize: '12px',
              },
            },
          },
        },
        tooltip: {
          pointFormat: '{series.name}: <b>{point.y}</b>',
        },
      };

      Highcharts.chart('departmentChart', this.chartOptions);
    });
  }
  openRecord(type: string) {
    let title: string = '';
    let records;
    switch (type) {
      case 'Leave': {
        title = 'Leaves Records';
        records = this.EmployeesOnLeave;
        break;
      }
      case 'WFH': {
        title = 'WFH Records';
        records = this.EmployeesOnWfh;
        break;
      }
      case 'Absent': {
        title = 'Absent Records';
        records = this.absentEmployees;
      }
    }

    this.dialog.open(RecordPopupComponent, {
      width: '50%',
      enterAnimationDuration: '350ms',
      exitAnimationDuration: '350ms',
      data: {
        title,
        records,
      },
    });
  }
}
