import { CurrencyPipe, DatePipe, TitleCasePipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import { User } from '../../../core/Interfaces/EmployeeModel';
import { AgePipe } from '../../../core/Pipes/age.pipe';
import { EmployeeService } from '../../../core/Services/employee.service';
@Component({
  selector: 'app-user-details-popup',
  standalone: true,
  imports: [
    MatButtonModule,
    MatDialogModule,
    DatePipe,
    CurrencyPipe,
    AgePipe,
    TitleCasePipe,
  ],
  templateUrl: './user-details-popup.component.html',
  styleUrl: './user-details-popup.component.css',
})
export class UserDetailsPopupComponent implements OnInit {
  private data = inject(MAT_DIALOG_DATA);
  constructor(private ref: MatDialogRef<UserDetailsPopupComponent>) {}
  private employeeService = inject(EmployeeService);
  userData: User = {
    fname: '',
    mname: '',
    lname: '',
    email: '',
    image: '',
    gender: '',
    department: '',
    dateOfJoining: '',
    dateOfBirth: '',
    phone: '',
    skills: [],
    address: {
      street: '',
      country: '',
      city: '',
      district: '',
      state: '',
      postalcode: 0,
    },
    experience: [
      {
        company: '',
        position: '',
        totalExp: 0,
        startDate: new Date(),
        endDate: new Date(),
      },
    ],
  };
  formatDate(date: Date): string {
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();

    return `${day}-${month}-${year}`;
  }
  ngOnInit(): void {
    this.employeeService.getUserData(this.data.email).subscribe({
      next: (res: User) => {
        this.userData = res;
        if (!this.data.image) {
          this.employeeService.getEmployeeImage(this.userData.image).subscribe({
            next: (blob) => {
              const objectURL = URL.createObjectURL(blob);
              this.userData.image = objectURL;
            },
          });
        } else {
          this.employeeService.getProfileImage().subscribe({
            next: (blob) => {
              const objectURL = URL.createObjectURL(blob);
              this.userData.image = objectURL;
            },
          });
        }
      },
    });
  }
  downloadProfile() {
    const profileElement = document.getElementById('profile');

    if (profileElement) {
      const elementsToHide = profileElement.querySelectorAll('.exclude');
      elementsToHide.forEach((ele: any) => {
        ele.style.display = 'none';
      });
      html2canvas(profileElement, { scale: 2 }).then((canvas) => {
        const imgData = canvas.toDataURL('image/jpeg', 1);
        const pdf = new jsPDF('p', 'mm', 'a4');
        const imgWidth = 190;
        const pageHeight = pdf.internal.pageSize.height;
        const imgHeight = (canvas.height * imgWidth) / canvas.width;
        const position = (pageHeight - imgHeight) / 2;

        pdf.addImage(imgData, 'PNG', 10, position, imgWidth, imgHeight);
        pdf.save(`${this.userData.fname} ${this.userData.lname}.pdf`);
        elementsToHide.forEach((ele: any) => {
          ele.style.display = '';
        });
      });
    }
  }
}
