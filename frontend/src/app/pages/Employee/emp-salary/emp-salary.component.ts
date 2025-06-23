import { CurrencyPipe, DatePipe, TitleCasePipe } from '@angular/common';
import {
  Component,
  ElementRef,
  ViewChild,
  inject,
  signal,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';

import { FormsModule } from '@angular/forms';
import { MatOptionModule } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSelectModule } from '@angular/material/select';

import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import { pagination } from '../../../core/Interfaces/AuthModel';
import { SalaryService } from '../../../core/Services/salary.service';
import { SalaryDetailsPopupComponent } from '../../Admin/salary-details-popup/salary-details-popup.component';
@Component({
  selector: 'app-emp-salary',
  standalone: true,
  imports: [
    MatTableModule,
    MatSortModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatTooltipModule,
    CurrencyPipe,
    FormsModule,
    MatOptionModule,
    MatSelectModule,
    MatChipsModule,
    DatePipe,
    TitleCasePipe,
    MatProgressBarModule,
  ],
  providers: [TitleCasePipe, DatePipe],
  templateUrl: './emp-salary.component.html',
  styleUrl: './emp-salary.component.css',
})
export class EmpSalaryComponent {
  private salaryService = inject(SalaryService);
  constructor(
    private dialog: MatDialog,
    private titleCasePipe: TitleCasePipe,
    private datePipe: DatePipe
  ) {}

  @ViewChild(MatSort) sort!: MatSort;
  dataSource: any;
  departmentData: any;
  userId!: number;
  searchdata: string = '';
  itemsPerPage = 5;
  pageSizeOptions = [5, 10, 15, 20];
  pageSortResponse: any;
  isDownloading = signal<boolean>(false);
  page = signal<pagination>({
    pageNumber: 0,
    pageSize: 5,
  });
  pageInfo: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  } = { size: 5, number: 0, totalElements: 0, totalPages: 0 };
  @ViewChild('myInput') searchElement!: ElementRef;

  displayColumns: string[] = ['month', 'totalSal', 'Status', 'action'];
  ngOnInit(): void {
    this.userId = +(localStorage.getItem('empId') as string);
    this.loadData(this.page());
  }

  nextPage() {
    this.page.update((current) => ({
      ...current,
      pageNumber: (current.pageNumber || 0) + 1,
    }));
    this.loadData(this.page());
  }
  prevPage() {
    this.page.update((current) => ({
      ...current,
      pageNumber: Math.max((current.pageNumber || 0) - 1, 0),
    }));
    this.loadData(this.page());
  }

  pageNumber = 0;
  loadData(page: pagination) {
    this.salaryService
      .employeeSalary(this.userId, this.searchdata, page)
      .subscribe({
        next: (res) => {
          this.pageSortResponse = res;
          this.pageInfo = this.pageSortResponse.page;
          this.pageSortResponse.content.forEach((employee: any) => {
            const data = this.totalSalaryCalc(employee.salary);
            employee.totalSal = data.totalSal;
            employee.totalIncome = data.totalIncome;
            employee.totalDeductions = data.totalDeductions;
          });
          this.dataSource = new MatTableDataSource(
            this.pageSortResponse.content
          );
          this.dataSource.sort = this.sort;
        },
      });
  }
  totalSalaryCalc(salary: any) {
    const totalIncome =
      salary.basicPay +
      salary.dearnessAllowance +
      salary.houseRentAllowance +
      salary.medicalAllowance +
      salary.corporateAttireAllowance +
      salary.regularBonus;

    const totalDeductions =
      salary.taxDeducted + salary.professionalTax + salary.providentFund;
    const totalSal = totalIncome - totalDeductions;
    return { totalSal, totalIncome, totalDeductions };
  }

  getSalaryForDate() {
    this.page.update((current) => ({
      ...current,
      pageNumber: 0,
    }));

    this.loadData(this.page());
  }
  paySalary(element: any) {
    this.salaryService.paySalary(element.salary.id).subscribe({
      next: (res: any) => {
        element.salary.paid = true;
      },
    });
  }
  onItemsPerPageChange(event: any) {
    this.page.update((current) => ({
      ...current,
      pageNumber: 0,
      pageSize: event.value,
    }));
    this.loadData(this.page());
  }
  salaryDetails(element: any) {
    this.dialog.open(SalaryDetailsPopupComponent, {
      width: '50%',
      height: '75%',
      enterAnimationDuration: '350ms',
      exitAnimationDuration: '350ms',
      data: {
        empData: element,
      },
    });
  }
  downloadPaySlip(element: any) {
    this.isDownloading.set(true);
    setTimeout(() => {
      const tempDiv = document.createElement('div');
      tempDiv.innerHTML = `
    <div class="container salary-slip-container">
      <div class="p-4">
        <h2 class="text-center mb-4 fw-bold">Salary Slip</h2>

        <div class="text-left mb-3 mt-3">
          <h4>ARGUSOFT INDIA LIMITED</h4>
          <h5>A 66, GIDC Sector - 25, Gandhinagar - 382016, Gujarat, India</h5>
        </div>

        <div class="row employee-details mb-4 mt-4">
          <div class="col-md-6">
            <p><span class="fs-4 fw-medium">Name :</span> <span class="fs-5">${this.titleCasePipe.transform(
              element.fullName
            )}</span></p>
            <p><span class="fs-4 fw-medium">Designation :</span> <span class="fs-5">${
              element.department
            }</span></p>
            <p><span class="fs-4 fw-medium">Salary for Month :</span> <span class="fs-5">${this.datePipe.transform(
              element.salary.month,
              'dd-MM-yyyy'
            )}</span></p>
            <p><span class="fs-4 fw-medium">Paid On :</span> <span class="fs-5">${this.datePipe.transform(
              element.salary.paidOn,
              'dd-MM-yyyy'
            )}</span></p>
          </div>
          <div class="col-md-6">
            <p><span class="fs-4 fw-medium">Emp No :</span> <span class="fs-5">${
              element.salary.register_id
            }</span></p>
            <p><span class="fs-4 fw-medium">Bank Account :</span><span class="fs-5"> XYZ</span></p>
            <p><span class="fs-4 fw-medium">Bank Name :</span><span class="fs-5"> Axis Bank</span></p>
          </div>
        </div>

        <div class="table-responsive">
          <table class="table" style="border:1px solid black">
            <thead class="thead-dark" >
              <tr class="text-center" style="border:1px solid black;">
                <th class="fs-3" style="border:1px solid black;">Income (INR)</th>
                <th class="fs-3" style="border:1px solid black;">Deductions (INR)</th>
              </tr>
            </thead>
            <tbody>
              <tr class="fs-5" style="border:1px solid black;">
                <td style="border:1px solid black;">Basic Pay: ${
                  element.salary.basicPay
                }</td>
                <td style="border:1px solid black;">Tax Deducted: ${
                  element.salary.taxDeducted
                }</td>
              </tr>
              <tr class="fs-5" style="border:1px solid black;">
                <td style="border:1px solid black;">Dearness Allowance: ${
                  element.salary.dearnessAllowance
                }</td>
                <td style="border:1px solid black;">Professional Tax: ${
                  element.salary.professionalTax
                }</td>
              </tr>
              <tr class="fs-5" style="border:1px solid black;">
                <td style="border:1px solid black;">House Rent Allowance: ${
                  element.salary.houseRentAllowance
                }</td>
                <td style="border:1px solid black;">Provident Fund: ${
                  element.salary.providentFund
                }</td>
              </tr>
              <tr class="fs-5" style="border:1px solid black;">
                <td style="border:1px solid black;">Conveyance Allowance: 0</td>
                <td style="border:1px solid black;">Advance Adjustment: 0</td>
              </tr>
              <tr class="fs-5" style="border:1px solid black;">
                <td style="border:1px solid black;">Medical Allowance: ${
                  element.salary.medicalAllowance
                }</td>
                <td style="border:1px solid black;">Meal Voucher: 0</td>
              </tr>
              <tr class="fs-5" style="border:1px solid black;">
                <td style="border:1px solid black;">Special Allowance: 0</td>
                <td style="border:1px solid black;">Other Deductions: 0</td>
              </tr>
              <tr class="fs-5" style="border:1px solid black;">
                <td style="border:1px solid black;">Experience Allowance: 0</td>
                <td style="border:1px solid black;"></td>
              </tr>
              <tr class="fs-5" style="border:1px solid black;">
                <td style="border:1px solid black;">Corporate Attire Allowance: ${
                  element.salary.corporateAttireAllowance
                }</td>
                <td style="border:1px solid black;"></td>
              </tr>
              <tr class="fs-5" style="border:1px solid black;">
                <td style="border:1px solid black;">Regular Bonus: ${
                  element.salary.regularBonus
                }</td>
                <td style="border:1px solid black;"></td>
              </tr>
              <tr class="fs-5" style="border:1px solid black;">
                <td style="border:1px solid black;">Loyalty Bonus: 0</td>
                <td style="border:1px solid black;"></td>
              </tr>
              <tr class="fs-5" style="border:1px solid black;">
                <td style="border:1px solid black;">Variable Component: 0</td>
                <td style="border:1px solid black;"></td>
              </tr>
              <tr class="fs-5" style="border:1px solid black;"> 
                <td style="border:1px solid black;">Accomplishment Bonus: 0</td>
                <td style="border:1px solid <div class="container salary-slip-container">
      <div  black;"></td>
              </tr>
              <tr class="fs-5" style="border:1px solid black;">
                <td style="border:1px solid black;"><strong>Gross Salary: &#8377;${
                  element.totalIncome
                }</strong></td>
                <td style="border:1px solid black;"><strong>Total Deductions: &#8377;${
                  element.totalDeductions
                }</strong></td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Footer - Net Salary -->
        <div class="text-right mt-3">
          <h5><strong>Net Salary (INR): &#8377;${element.totalSal}</strong></h5>
        </div>
        <p style="color:red;">*This is a computer generated Statement and does not require a signature or seal.</p>
      </div>
    </div>
  `;

      document.body.appendChild(tempDiv);

      html2canvas(tempDiv, { scale: 1.5 }).then((canvas) => {
        const imgData = canvas.toDataURL('image/jpeg', 1);
        const doc = new jsPDF('p', 'mm', 'a4');
        doc.addImage(imgData, 'JPEG', 10, 10, 190, 0);
        doc.save('salary_slip.pdf');
        document.body.removeChild(tempDiv);
      });
      this.isDownloading.set(false);
    });
  }
}
