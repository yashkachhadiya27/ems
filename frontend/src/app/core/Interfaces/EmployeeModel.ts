import { address, experience } from './AuthModel';

export interface EditEmployee {
  fname: string;
  mname: string;
  lname: string;
  email: string;
  department: string;
  phone: string;
  dateOfBirth: string;
  dateOfJoining: string;
}
export interface User {
  fname: string;

  mname: string;

  lname: string;

  email: string;

  image: string;

  gender: string;

  department: string;

  dateOfJoining: string;

  dateOfBirth: string;

  phone: string;

  skills: string[];

  address: address;

  experience: experience[];
}
export interface LeaveDashboardDTO {
  remainingLeaves: number;
  wfhLeavesRemaining: number;
  remainingRestrictedLeaves: number;
  currentQuarterLeavesUsed: number;
  totalLeaves: number;
}
export interface AbsentEmployees {
  fullName: string;
  image: string;
}
export interface LeaveEmployees {
  fullName: string;
  image: string;
  leaveType: string;
  leaveFromDate: string;
  leaveToDate: string;
}
export interface Notification {
  id: number;
  registerId: number;
  message: string;
}
