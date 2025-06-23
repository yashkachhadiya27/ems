export interface address {
  street: string;
  country: string;
  city: string;
  district: string;
  state: string;
  postalcode: number;
}
export interface experience {
  company: string;
  position: string;
  totalExp: number;
  startDate: Date;
  endDate: Date;
}
export interface RegisterForm {
  fname: string;
  mname: string;
  lname: string;
  email: string;
  phone: number;
  gender: string;
  department: string;
  dateOfBirth: Date;
  dateOfJoining: Date;
  image: File;
  password: string;
  cPassword: string;
  street: string;
  postal: number;
  district: string;
  state: string;
  city: string;
  country: string;
  skills?: string[];
  experience?: experience[];
}
export interface login {
  email: string;
  password: string;
}
export interface loginResponse {
  accessToken: string;
  refreshToken: string;
  role: string;
  empId: number;
}
export interface forgotPasswordResp {
  status: boolean;
  email: string;
  message: string;
}
export interface paginationSorting {
  pageNumber: number;
  pageSize: number;
  sortBy: string;
  sortOrder: string;
}
export interface pagination {
  pageNumber: number;
  pageSize: number;
}
