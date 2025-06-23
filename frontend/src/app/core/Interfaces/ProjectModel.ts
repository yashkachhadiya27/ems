export interface project {
  projectName: string;
  projectJoinDate: string;
  projectEndDate: string;
  reportingToId: number;
  technologies: string[];
  userId: number;
}
export interface idName {
  id: number;
  fullName: string;
}
export interface UserProjectDetail {
  fullName: string;
  userId: number;
  email: string;
  phone: string;
  projectName: string;
}
