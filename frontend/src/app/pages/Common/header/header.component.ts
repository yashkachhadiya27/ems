import { NgClass, NgStyle, TitleCasePipe } from '@angular/common';
import { Component, inject, OnInit, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Router } from '@angular/router';
import { AttendanceService } from '../../../core/Services/attendance.service';
import { EmployeeService } from '../../../core/Services/employee.service';
import { RoleService } from '../../../core/Services/role.service';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../../core/Services/auth.service';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDialog } from '@angular/material/dialog';
import { NotificationPopupComponent } from '../../Employee/notification-popup/notification-popup.component';
@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSlideToggleModule,
    FormsModule,
    NgClass,
    MatMenuModule,
    NgStyle,
    TitleCasePipe,
    MatBadgeModule,
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent implements OnInit {
  private toast = inject(ToastrService);
  private roleService = inject(RoleService);
  private attendanceService = inject(AttendanceService);
  private employeeService = inject(EmployeeService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private dialog = inject(MatDialog);
  isBadgeDisplay = true;
  birthdays: string[] = [];
  workAnniversarries: string[] = [];
  hasBirthday = signal<boolean>(false);
  hasWork = signal<boolean>(false);
  collapsed = signal(false);
  isCollapsed = output<boolean>();
  isLoggedin = signal<boolean>(false);
  isCheckedIn: boolean = false;
  userRole = signal<string>('');
  isSunday = signal<boolean>(false);
  flowers: { position: string; delay: number }[] = [];
  notificationData: Notification[] = [];
  notificationCount: number = 0;
  onCollapsedBtnClick() {
    this.collapsed.set(!this.collapsed());
    this.isCollapsed.emit(this.collapsed());
  }
  constructor() {
    this.isSunday.set(new Date().getDay() === 0);
  }
  ngOnInit(): void {
    this.roleService.isLoggedin.subscribe((res) => {
      this.isLoggedin.set(res);

      if (res) {
        this.employeeService.getTodaysBirthdays().subscribe((data) => {
          this.birthdays = data;
          this.hasBirthday.set(this.birthdays.length > 0);
        });
        this.employeeService.getTodayWorkAnniversarries().subscribe((data) => {
          this.workAnniversarries = data;
          this.hasWork.set(this.workAnniversarries.length > 0);
        });
        if (this.userRole() === 'USER') {
          this.fetchAttendanceStatus();
          this.fetchNotification();
        }
      }
    });
    this.roleService.roleSubject.subscribe((res) => {
      if (res) {
        this.userRole.set(res);
        this.isLoggedin.set(true);
      } else {
        this.isLoggedin.set(false);
      }
    });
    if (this.userRole() === 'USER') {
      this.fetchAttendanceStatus();
      this.fetchNotification();
    }
    if (this.userRole() !== '') {
      this.employeeService.getTodaysBirthdays().subscribe((data) => {
        this.birthdays = data;
        this.hasBirthday.set(this.birthdays.length > 0);
      });
      this.employeeService.getTodayWorkAnniversarries().subscribe((data) => {
        this.workAnniversarries = data;
        this.hasWork.set(this.workAnniversarries.length > 0);
      });
    }
    this.generateFlowerPositions();
  }
  navigateToDashboard() {
    if (this.userRole() === 'USER') {
      this.router.navigate(['/employee/dashboard']);
    }
    if (this.userRole() === 'ADMIN') {
      this.router.navigate(['/admin/dashboard']);
    }
  }
  generateFlowerPositions(): void {
    const flowerCount = 7;
    for (let i = 0; i < flowerCount; i++) {
      const leftPosition = Math.random() * 100;
      const delay = Math.random() * 3;
      this.flowers.push({ position: `${leftPosition}%`, delay });
    }
  }
  fetchAttendanceStatus() {
    const empId = localStorage.getItem('empId');
    if (empId != null) {
      this.attendanceService.getStatus(+empId).subscribe({
        next: (res: boolean) => {
          this.isCheckedIn = res;
        },
        error: (error) => {
          console.error('Error fetching attendance status', error);
        },
      });
    } else {
      this.isCheckedIn = false;
    }
  }
  fetchNotification() {
    const empId = +(localStorage.getItem('empId') as string);

    this.employeeService.getNotifications(empId).subscribe({
      next: (res: Notification[]) => {
        this.notificationData = res;
        this.notificationCount = this.notificationData.length;
      },
    });
  }
  logout(): void {
    this.authService.logout();
    localStorage.clear();
    this.roleService.roleSubject.next(null);
    this.roleService.isLoggedin.next(false);
    this.router.navigate(['/login']);
  }
  toggleInOut() {
    const empId = localStorage.getItem('empId');
    if (empId !== null) {
      if (this.isCheckedIn) {
        this.attendanceService.markIn(+empId).subscribe({
          next: () => {
            this.toast.success('Checked In Successfully', 'Success', {
              timeOut: 3000,
              closeButton: true,
            });
          },
          error: (error) => {
            this.toast.error('Error during Check In', 'Error', {
              timeOut: 3000,
              closeButton: true,
            });
          },
        });
      } else {
        this.attendanceService.markOut(+empId).subscribe({
          next: () => {
            this.toast.success('Checked Out Successfully', 'Success', {
              timeOut: 3000,
              closeButton: true,
            });
          },
          error: (error) => {
            this.toast.error('Error during Checkout', 'Error', {
              timeOut: 3000,
              closeButton: true,
            });
          },
        });
      }
    }
  }
  openNotificationDialog() {
    this.isBadgeDisplay = false;
    const _notificationPopup = this.dialog.open(NotificationPopupComponent, {
      width: '30%',
      height: '50%',
      enterAnimationDuration: '350ms',
      exitAnimationDuration: '350ms',
      data: {
        notification: this.notificationData,
      },
    });
    _notificationPopup.afterClosed().subscribe({
      next: (res) => {
        if (res === 'Removed') {
          this.isBadgeDisplay = false;
          this.notificationData = [];
          this.notificationCount = 0;
        } else {
          this.isBadgeDisplay = true;
        }
      },
    });
  }
}
