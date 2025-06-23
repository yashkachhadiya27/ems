import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-salary-details-popup',
  standalone: true,
  imports: [MatDialogModule, DatePipe, DecimalPipe],
  templateUrl: './salary-details-popup.component.html',
  styleUrl: './salary-details-popup.component.css',
})
export class SalaryDetailsPopupComponent implements OnInit {
  private data = inject(MAT_DIALOG_DATA);
  salaryData!: any;
  empData!: any;
  ngOnInit(): void {
    this.salaryData = this.data.empData.salary;
    this.empData = this.data.empData;
  }
}
