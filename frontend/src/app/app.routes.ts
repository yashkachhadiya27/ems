import { Routes } from '@angular/router';
import { LoginComponent } from './pages/Common/login/login.component';
import { adminGuard } from './core/Guards/admin.guard';
import { employeeGuard } from './core/Guards/employee.guard';
import { formLeaveGuard } from './core/Guards/form-leave.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: 'login',
    component: LoginComponent,
    title: 'Login',
  },
  {
    path: 'callback',
    loadComponent: () =>
      import('./pages/Common/callback/callback.component').then(
        (com) => com.CallbackComponent
      ),
  },
  {
    path: 'register',
    title: 'Register',
    canDeactivate: [formLeaveGuard],
    loadComponent: () =>
      import('./pages/Common/register/register.component').then(
        (com) => com.RegisterComponent
      ),
  },
  {
    path: 'adminEmployee',
    children: [
      {
        path: 'notice',
        title: 'Notice',
        loadComponent: () =>
          import('./pages/Employee/notice/notice.component').then(
            (com) => com.NoticeComponent
          ),
      },
    ],
  },
  {
    path: 'admin',
    canActivate: [adminGuard],
    children: [
      {
        path: 'dashboard',
        title: 'Dashboard',
        loadComponent: () =>
          import(
            './pages/Admin/admin-dashboard/admin-dashboard.component'
          ).then((com) => com.AdminDashboardComponent),
      },
      {
        path: 'manage-employee',
        title: 'Manage Employee',
        loadComponent: () =>
          import(
            './pages/Admin/manage-employee/manage-employee.component'
          ).then((com) => com.ManageEmployeeComponent),
      },
      {
        path: 'add-employee',
        title: 'Add Employee',
        loadComponent: () =>
          import('./pages/Admin/add-employee/add-employee.component').then(
            (com) => com.AddEmployeeComponent
          ),
        canDeactivate: [formLeaveGuard],
      },
      {
        path: 'leave-history',
        title: 'Leave History',
        loadComponent: () =>
          import('./pages/Admin/leave-history/leave-history.component').then(
            (com) => com.LeaveHistoryComponent
          ),
      },
      {
        path: 'manage-leave',
        title: 'Manage Leave',
        loadComponent: () =>
          import('./pages/Admin/manage-leave/manage-leave.component').then(
            (com) => com.ManageLeaveComponent
          ),
      },
      {
        path: 'attendance',
        title: 'Attendance',
        loadComponent: () =>
          import('./pages/Admin/attendance/attendance.component').then(
            (com) => com.AttendanceComponent
          ),
      },
      {
        path: 'add-salary',
        title: 'Add Salary',
        loadComponent: () =>
          import('./pages/Admin/add-salary/add-salary.component').then(
            (com) => com.AddSalaryComponent
          ),
      },
      {
        path: 'pay-salary',
        title: 'Pay Salary',
        loadComponent: () =>
          import('./pages/Admin/pay-salary/pay-salary.component').then(
            (com) => com.PaySalaryComponent
          ),
      },
      {
        path: 'manage-department',
        title: 'Manage Department',
        loadComponent: () =>
          import(
            './pages/Admin/manage-department/manage-department.component'
          ).then((com) => com.ManageDepartmentComponent),
      },
      {
        path: 'notices',
        title: 'Notice',
        loadComponent: () =>
          import('./pages/Admin/all-notice/all-notice.component').then(
            (com) => com.AllNoticeComponent
          ),
      },
    ],
  },
  // Employee Routes
  {
    path: 'employee',
    canActivate: [employeeGuard],
    children: [
      {
        path: 'dashboard',
        title: 'Dashboard',
        loadComponent: () =>
          import(
            './pages/Employee/employee-dashboard/employee-dashboard.component'
          ).then((com) => com.EmployeeDashboardComponent),
      },
      {
        path: 'todo',
        title: 'Todo',
        loadComponent: () =>
          import('./pages/Common/todo/todo.component').then(
            (com) => com.TodoComponent
          ),
      },
      {
        path: 'apply-leave',
        title: 'Apply Leave',
        loadComponent: () =>
          import('./pages/Employee/apply-leave/apply-leave.component').then(
            (com) => com.ApplyLeaveComponent
          ),
      },
      {
        path: 'leave-history',
        title: 'Leave History',
        loadComponent: () =>
          import('./pages/Employee/leave-history/leave-history.component').then(
            (com) => com.LeaveHistoryComponent
          ),
      },
      {
        path: 'emp-salary',
        title: 'Employee Salary',
        loadComponent: () =>
          import('./pages/Employee/emp-salary/emp-salary.component').then(
            (com) => com.EmpSalaryComponent
          ),
      },
      {
        path: 'update-profile',
        title: 'Update Profile',
        loadComponent: () =>
          import(
            './pages/Employee/update-profile/update-profile.component'
          ).then((com) => com.UpdateProfileComponent),
      },
      {
        path: 'chat',
        title: 'Chat',
        loadComponent: () =>
          import('./pages/Employee/chat/chat.component').then(
            (com) => com.ChatComponent
          ),
      },
    ],
  },
  {
    path: '**',
    title: 'Not Found',
    loadComponent: () =>
      import('./pages/Common/not-found/not-found.component').then(
        (com) => com.NotFoundComponent
      ),
  },
];
