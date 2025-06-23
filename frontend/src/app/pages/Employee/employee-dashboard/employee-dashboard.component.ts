import { TitleCasePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router, RouterModule } from '@angular/router';
import * as Highcharts from 'highcharts';
import { HighchartsChartModule } from 'highcharts-angular';
import {
  AbsentEmployees,
  LeaveDashboardDTO,
  LeaveEmployees,
} from '../../../core/Interfaces/EmployeeModel';
import {
  project,
  UserProjectDetail,
} from '../../../core/Interfaces/ProjectModel';
import { AttendanceService } from '../../../core/Services/attendance.service';
import { EmployeeService } from '../../../core/Services/employee.service';
import { AddProjectPopupComponent } from '../add-project-popup/add-project-popup.component';
import { LogsPopupComponent } from '../logs-popup/logs-popup.component';
import { RecordCardComponent } from '../record-card/record-card.component';
import { RecordPopupComponent } from '../record-popup/record-popup.component';
import { Notice } from '../../../core/Interfaces/NoticeModel';
import { NoticeService } from '../../../core/Services/notice.service';

@Component({
  selector: 'app-employee-dashboard',
  standalone: true,
  imports: [
    HighchartsChartModule,
    MatDialogModule,
    MatButtonToggleModule,
    MatChipsModule,
    MatIconModule,
    RouterModule,
    MatExpansionModule,
    TitleCasePipe,
    MatTooltipModule,
    RecordCardComponent,
  ],
  templateUrl: './employee-dashboard.component.html',
  styleUrl: './employee-dashboard.component.css',
})
export class EmployeeDashboardComponent implements OnInit {
  private attendanceService = inject(AttendanceService);
  private employeeService = inject(EmployeeService);
  private noticeService = inject(NoticeService);
  private route = inject(Router);
  private dialog = inject(MatDialog);
  Highcharts = Highcharts;
  chartOptions: any;
  currentMonth!: number;
  currentYear!: number;
  userId!: number;
  selectedFilter: string = 'all';
  tasks: any = [];
  displayTasks: any = signal<any[]>(this.tasks);
  todoTasks: any = [];
  progressTasks: any = [];
  empProjects: project[] = [];
  noticeData: Notice[] = [];
  empUnderProject: UserProjectDetail[] = [];
  absentEmployees: AbsentEmployees[] = [];
  EmployeesOnLeave: LeaveEmployees[] = [];
  EmployeesOnWfh: LeaveEmployees[] = [];
  noticeIndex = signal<number>(0);
  leavesToShow: LeaveDashboardDTO = {
    remainingLeaves: 0,
    remainingRestrictedLeaves: 0,
    wfhLeavesRemaining: 0,
    currentQuarterLeavesUsed: 0,
    totalLeaves: 0,
  };
  ngOnInit(): void {
    this.userId = +(localStorage.getItem('empId') as string);
    if (this.userId === 0) {
      return;
    }
    this.employeeService.getLeaveDashboard(this.userId).subscribe({
      next: (res: LeaveDashboardDTO) => {
        this.leavesToShow = res;
        this.leaveWithPayChart(this.leavesToShow);
        this.loadwfhChart(this.leavesToShow);
        this.RestrictedleaveChart(this.leavesToShow);
      },
    });
    const now = new Date();
    this.currentMonth = now.getMonth() + 1;
    this.currentYear = now.getFullYear();
    this.loadAttendance();
    this.loadNotDoneTasks(this.userId);
    this.employeeService.getProjects(this.userId).subscribe({
      next: (res: project[]) => {
        this.empProjects = res;
      },
    });

    this.employeeService
      .getEmployeesInProject(this.userId)
      .subscribe((res: UserProjectDetail[]) => (this.empUnderProject = res));

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
    this.noticeService.getActiveNotices().subscribe({
      next: (res) => {
        this.noticeData = res;
      },
    });
  }

  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }
  step = signal(1);

  setStep(index: number) {
    this.step.set(index);
  }

  nextStep() {
    this.step.update((i) => i + 1);
  }

  prevStep() {
    this.step.update((i) => i - 1);
  }
  nextNotice() {
    this.noticeIndex.update((val) => val + 1);
    console.log(this.noticeIndex());
  }
  previousNotice() {
    this.noticeIndex.update((val) => val - 1);
    console.log(this.noticeIndex());
  }

  showNotice() {
    this.route.navigate(['/adminEmployee/notice'], {
      queryParams: {
        data: JSON.stringify(this.noticeData[this.noticeIndex()]),
      },
    });
  }
  loadNotDoneTasks(userId: number) {
    this.employeeService.getNotDoneTasks(userId).subscribe({
      next: (res) => {
        this.tasks = res;
        this.displayTasks.set(this.tasks);
        this.tasks.forEach((task: any) => {
          if (task.status === 'IN_PROGRESS') {
            this.progressTasks.push(task);
          } else {
            this.todoTasks.push(task);
          }
        });
      },
    });
  }
  onFilterChange(event: any) {
    this.selectedFilter = event.value;
    if (this.selectedFilter === 'progress') {
      this.displayTasks.set(this.progressTasks);
    } else if (this.selectedFilter === 'todo') {
      this.displayTasks.set(this.todoTasks);
    } else {
      this.displayTasks.set(this.tasks);
    }
  }
  redirectToTodo() {
    this.route.navigate(['/employee/todo']);
  }
  moreDetails(user: UserProjectDetail) {
    return `Name:${user.fullName},
            Email:${user.email},    
            Contact:${user.phone}`;
  }
  addProject() {
    const _addProjectDialog = this.dialog.open(AddProjectPopupComponent, {
      width: '30%',
      enterAnimationDuration: '350ms',
      exitAnimationDuration: '350ms',
      data: {
        id: this.userId,
      },
    });
    _addProjectDialog.afterClosed().subscribe({
      next: () => {
        this.employeeService.getProjects(this.userId).subscribe({
          next: (res: project[]) => {
            this.empProjects = res;
          },
        });
      },
    });
  }
  formatTime(seconds: number): string {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    return `${hours}h ${minutes}m`;
  }
  loadAttendance() {
    interface CustomPoint extends Highcharts.Point {
      fullDate: string;
    }

    this.attendanceService
      .getAttendanceByMonth(this.userId, this.currentMonth, this.currentYear)
      .subscribe((data) => {
        if (!data[0]) {
          this.currentMonth = new Date().getMonth() + 1;
          return;
        }
        const today = new Date().toISOString().split('T')[0];
        const monthName = new Date(data[0].attendanceDate).toLocaleString(
          'default',
          { month: 'long' }
        );

        const filteredData = data
          .filter(
            (entry: { attendanceDate: string }) =>
              entry.attendanceDate !== today
          )
          .sort(
            (a: { attendanceDate: string }, b: { attendanceDate: string }) =>
              new Date(a.attendanceDate).getTime() -
              new Date(b.attendanceDate).getTime()
          );

        const formattedDates = filteredData.map(
          (entry: { attendanceDate: string }) => {
            const dateObj = new Date(entry.attendanceDate);
            return {
              day: dateObj.getDate(),
              fullDate: entry.attendanceDate,
            };
          }
        );

        this.chartOptions = {
          chart: {
            type: 'line',
            renderTo: 'attendanceChart',
            backgroundColor: 'transparent',
          },
          credits: {
            enabled: false,
          },
          title: {
            text: ``,
          },
          xAxis: {
            categories: formattedDates.map((d: { day: any }) => d.day),
            title: {
              text: `Dates (${monthName})`,
            },
          },
          yAxis: {
            title: {
              text: 'Time (in hours)',
            },
          },
          series: [
            {
              name: 'Total In Time',
              data: filteredData.map((entry: { totalInTime: number }) =>
                Math.floor(entry.totalInTime / 3600)
              ),
              color: '#903564',
              lineWidth: 2,
            },
            {
              name: 'Total Break Time',
              data: filteredData.map((entry: { totalBreakTime: number }) =>
                Math.floor(entry.totalBreakTime / 3600)
              ),
              color: '#666666',
              lineWidth: 2,
            },
            {
              name: 'Total Working Time',
              data: filteredData.map(
                (entry: { totalInTime: number; totalBreakTime: number }) =>
                  Math.floor((entry.totalInTime - entry.totalBreakTime) / 3600)
              ),
              color: 'grey',
              lineWidth: 2,
            },
          ],
          plotOptions: {
            series: {
              point: {
                events: {
                  click: (event: { point: CustomPoint }) => {
                    const clickedPointIndex = event.point.index;
                    const fullDate = formattedDates[clickedPointIndex].fullDate;
                    const logDetails = filteredData[clickedPointIndex];

                    this.openLogDetails(fullDate, logDetails);
                  },
                  mouseOver: function () {
                    const point = this as unknown as Highcharts.Point;
                    if (point.graphic) {
                      point.graphic.element.style.fill = 'red';
                    }
                  },
                  mouseOut: function () {
                    const point = this as unknown as Highcharts.Point;
                    if (point.graphic) {
                      point.graphic.element.style.fill = '';
                    }
                  },
                },
              },
            },
          },
          tooltip: {
            shared: false,
            useHTML: true,
            formatter: function (
              this: Highcharts.TooltipFormatterContextObject
            ): string {
              const seriesName = this.series.name;
              const value = Math.floor(+(this.y as number));
              return `
            <div style="text-align: center;">
              ${seriesName}<br/>
              Time: <b>${value} hours</b>
            </div>
          `;
            },
            style: {
              color: 'black',
              backgroundColor: 'transparent',
              borderRadius: '5px',
              padding: '10px',
            },
          },
        };

        Highcharts.chart('attendanceChart', this.chartOptions);
      });
  }

  openLogDetails(date: string, logDetails: any) {
    this.attendanceService
      .getLogsForDay(this.userId, date)
      .subscribe((logs) => {
        this.dialog.open(LogsPopupComponent, {
          width: '50%',
          enterAnimationDuration: '350ms',
          exitAnimationDuration: '350ms',
          data: {
            logs,
            date,
            inTime: logDetails.totalInTime / 3600,
            breakTime: logDetails.totalBreakTime / 3600,
            workingTime:
              (logDetails.totalInTime - logDetails.totalBreakTime) / 3600,
          },
        });
      });
  }

  changeMonth(increment: number) {
    this.currentMonth += increment;
    if (this.currentMonth > 12) {
      this.currentMonth = 1;
      this.currentYear++;
    } else if (this.currentMonth < 1) {
      this.currentMonth = 12;
      this.currentYear--;
    }
    this.loadAttendance();
  }

  loadwfhChart(leavesToShow: LeaveDashboardDTO) {
    this.chartOptions = {
      chart: {
        renderTo: 'wfhQuotaContainer',
        type: 'pie',
        backgroundColor: 'transparent',
        events: {
          load: function () {
            const chart = this as unknown as Highcharts.Chart;
            const centerX = chart.plotLeft + chart.plotWidth / 2;
            const centerY = chart.plotTop + chart.plotHeight / 2;

            chart.renderer
              .text(leavesToShow.wfhLeavesRemaining + '', centerX, centerY)
              .attr({
                align: 'center',
                verticalAlign: 'middle',
              })
              .css({
                color: '#141d31',
                fontSize: '20px',
                fontWeight: 'bold',
              })
              .add();
          },
        },
      },
      title: null,
      plotOptions: {
        pie: {
          innerSize: '70%',
          size: '110%',
          center: ['50%', '50%'],
          dataLabels: {
            enabled: false,
          },
          borderWidth: 0,
        },
      },
      series: [
        {
          name: 'WFH Quota',
          data: [
            {
              name: 'Used WFH',
              y: leavesToShow.wfhLeavesRemaining,
              color: '#141d31',
            },
            {
              name: 'Remaining WFH',
              y: 2 - leavesToShow.wfhLeavesRemaining,
              color: 'grey',
            },
          ],
        },
      ],
      tooltip: {
        enabled: false,
      },
      credits: {
        enabled: false,
      },
      exporting: {
        enabled: false,
      },
    };
    Highcharts.chart('wfhQuotaContainer', this.chartOptions);
  }
  leavesWithPayChartOptions: any;
  restrictedLeavesChartOptions: any;
  leaveWithPayChart(leavesToShow: LeaveDashboardDTO) {
    this.leavesWithPayChartOptions = {
      chart: {
        renderTo: 'leavesWithPayContainer',
        type: 'pie',
        backgroundColor: 'transparent',
        marginTop: 0,
        events: {
          load: function () {
            const chart = this as unknown as Highcharts.Chart;
            const centerX = chart.plotLeft + chart.plotWidth / 2;
            const centerY = chart.plotTop + chart.plotHeight / 2;
            const val = leavesToShow.remainingLeaves;
            chart.renderer
              .text(val + '', centerX, centerY)
              .attr({
                align: 'center',
                verticalAlign: 'middle',
              })
              .css({
                color: '#141d31',
                fontSize: '20px',
                fontWeight: 'bold',
              })
              .add();
          },
        },
      },
      title: {
        text: 'Leaves with Pay',
        align: 'center',
        verticalAlign: 'top',
        paddingBottom: 0,
        style: {
          fontSize: '16px',
          fontWeight: '500',
          color: '#141d31',
        },
      },
      plotOptions: {
        pie: {
          innerSize: '70%',
          size: '60%',
          center: ['50%', '50%'],
          dataLabels: {
            enabled: false,
          },
          borderWidth: 0,
        },
      },
      series: [
        {
          name: 'Leave Quota',
          data: [
            {
              name: 'Leaves with Pay',
              y: leavesToShow.totalLeaves - leavesToShow.remainingLeaves,
              color: '#7f878f',
            },
            {
              name: 'Leaves with Pay',
              y: leavesToShow.remainingLeaves,
              color: '#141d31',
            },
          ],
        },
      ],
      tooltip: {
        enabled: false,
      },
      credits: {
        enabled: false,
      },
    };

    Highcharts.chart('leavesWithPayContainer', this.leavesWithPayChartOptions);
  }
  RestrictedleaveChart(leavesToShow: LeaveDashboardDTO) {
    this.restrictedLeavesChartOptions = {
      chart: {
        renderTo: 'restrictedLeavesContainer',
        type: 'pie',
        backgroundColor: 'transparent',
        marginTop: 0,
        events: {
          load: function () {
            const chart = this as unknown as Highcharts.Chart;
            const centerX = chart.plotLeft + chart.plotWidth / 2;
            const centerY = chart.plotTop + chart.plotHeight / 2;

            chart.renderer
              .text(
                leavesToShow.remainingRestrictedLeaves + '',
                centerX,
                centerY
              )
              .attr({
                align: 'center',
                verticalAlign: 'middle',
              })
              .css({
                color: '#141d31',
                fontSize: '20px',
                fontWeight: 'bold',
              })
              .add();
          },
        },
      },
      title: {
        text: 'Restricted Leaves',
        align: 'center',
        verticalAlign: 'top',
        style: {
          fontSize: '16px',
          fontWeight: '500',
          color: '#141d31',
        },
      },
      plotOptions: {
        pie: {
          innerSize: '70%',
          size: '60%',
          center: ['50%', '50%'],
          dataLabels: {
            enabled: false,
          },
          borderWidth: 0,
        },
      },
      series: [
        {
          name: 'Restricted Leave Quota',
          data: [
            {
              name: 'Used Leaves',
              y: leavesToShow.remainingRestrictedLeaves,
              color: '#141d31',
            },
            {
              name: 'Available Leaves',
              y: 2 - leavesToShow.remainingRestrictedLeaves,
              color: '#7f878f',
            },
          ],
        },
      ],
      tooltip: {
        enabled: false,
      },
      credits: {
        enabled: false,
      },
    };

    Highcharts.chart(
      'restrictedLeavesContainer',
      this.restrictedLeavesChartOptions
    );
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
