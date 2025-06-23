import { sidenav } from '../Interfaces/SidenavModel';
export const common: sidenav[] = [
  {
    routerLink: '/login',
    name: 'Login',
    class: 'fa-solid fa-right-to-bracket',
  },
  {
    routerLink: '/register',
    name: 'Register',
    class: 'fa-solid fa-user-plus',
  },
];
export const admin: sidenav[] = [
  {
    routerLink: '/admin/dashboard',
    name: 'Dashboard',
    class: 'fa-solid fa-table-columns',
  },
  {
    routerLink: '/admin/manage-department',
    name: 'Department',
    class: 'fa-solid fa-building-user ps-2',
  },
  {
    routerLink: '/admin/add-employee',
    name: 'Add staff',
    class: 'fa-solid fa-people-group',
  },
  {
    routerLink: '/admin/manage-employee',
    name: 'Manage staff',
    class: 'fa-solid fa-people-roof',
  },
  {
    routerLink: '/admin/notices',
    name: 'Notice',
    class: 'fa-solid fa-file-lines',
  },
  // {
  //   routerLink: '/admin/attendance',
  //   name: 'Attendace',
  //   class: 'fa-solid fa-eye',
  // },
  {
    name: 'Salary',
    class: 'fa-solid fa-hand-holding-dollar',
    subLinks: [
      {
        routerLink: '/admin/add-salary',
        name: 'Add',
        class: 'fa-solid fa-plus',
      },
      {
        routerLink: '/admin/pay-salary',
        name: 'Manage',
        class: 'fa-solid fa-money-bill-transfer',
      },
    ],
  },
  {
    name: 'Leave',
    class: 'fa-solid fa-person-walking-arrow-right',
    subLinks: [
      {
        routerLink: '/admin/manage-leave',
        name: 'Manage',
        class: 'fa-solid fa-person-walking-arrow-right',
      },
      {
        routerLink: '/admin/leave-history',
        name: 'History',
        class: 'fa-solid fa-clock-rotate-left',
      },
    ],
  },
];

export const employee: sidenav[] = [
  {
    routerLink: '/employee/dashboard',
    name: 'Dashboard',
    class: 'fa-solid fa-table-columns',
  },
  {
    routerLink: '/employee/emp-salary',
    name: 'Salary',
    class: 'fa-solid fa-hand-holding-dollar',
  },
  {
    routerLink: '/employee/apply-leave',
    name: 'Apply Leave',
    class: 'fa-solid fa-person-walking-arrow-right',
  },
  {
    routerLink: '/employee/leave-history',
    name: 'Leave History',
    class: 'fa-solid fa-clock-rotate-left',
  },
  {
    routerLink: '/employee/todo',
    name: 'To-Do List',
    class: 'fa-solid fa-clipboard-list',
  },
  {
    routerLink: '/employee/update-profile',
    name: 'Update Profile',
    class: 'fa-solid fa-pen',
  },
  {
    routerLink: '/employee/chat',
    name: 'Chat',
    class: 'fa-regular fa-comment-dots',
  },
];
