import { CurrencyPipe } from '@angular/common';
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
import { MatChipsModule } from '@angular/material/chips';
import { MatOptionModule } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSelectModule } from '@angular/material/select';
import {
  debounceTime,
  distinctUntilChanged,
  fromEvent,
  map,
  switchMap,
} from 'rxjs';
import { pagination } from '../../../core/Interfaces/AuthModel';
import { EmployeeService } from '../../../core/Services/employee.service';
import { SalaryService } from '../../../core/Services/salary.service';
import { SalaryDetailsPopupComponent } from '../salary-details-popup/salary-details-popup.component';
@Component({
  selector: 'app-pay-salary',
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
  ],
  templateUrl: './pay-salary.component.html',
  styleUrl: './pay-salary.component.css',
})
export class PaySalaryComponent {
  private salaryService = inject(SalaryService);
  private employeeService = inject(EmployeeService);
  constructor(private dialog: MatDialog) {}
  @ViewChild(MatSort) sort!: MatSort;
  dataSource: any;
  departmentData: any;
  searchdata: string = '';
  itemsPerPage = 5;
  pageSizeOptions = [5, 10, 15, 20];
  pageSortResponse: any;
  dateField: Date = new Date();
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

  displayColumns: string[] = [
    'image',
    'name',
    'email',
    'department',
    'salary',
    'action',
  ];
  ngOnInit(): void {
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
      .getEmpSalaryPayDetails(this.searchdata, this.dateField, page)
      .subscribe({
        next: (res) => {
          this.pageSortResponse = res;
          this.pageInfo = this.pageSortResponse.page;
          this.pageSortResponse.content.forEach((employee: any) => {
            const data = this.totalSalaryCalc(employee.salary);
            employee.totalSal = data.totalSal;
            employee.totalIncome = data.totalIncome;
            employee.totalDeductions = data.totalDeductions;
            this.employeeService.getEmployeeImage(employee.image).subscribe({
              next: (blob) => {
                const objectURL = URL.createObjectURL(blob);
                employee.imageURL = objectURL;
              },
            });
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
  ngAfterViewInit(): void {
    const sarchTerm = fromEvent(this.searchElement.nativeElement, 'keyup');
    sarchTerm
      .pipe(
        map((event: any) => event.target.value),
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((query) => {
          this.page.update((current) => ({
            pageSize: 5,
            pageNumber: 0,
            sortBy: 'id',
            sortOrder: 'asc',
          }));
          this.itemsPerPage = 5;
          return this.salaryService.getEmpSalaryPayDetails(
            query,
            this.dateField,
            this.page()
          );
        })
      )
      .subscribe({
        next: (res) => {
          this.pageSortResponse = res;
          this.pageInfo = this.pageSortResponse.page;
          this.pageSortResponse.content.forEach((employee: any) => {
            const data = this.totalSalaryCalc(employee.salary);
            employee.totalSal = data.totalSal;
            employee.totalIncome = data.totalIncome;
            employee.totalDeductions = data.totalDeductions;
            this.employeeService.getEmployeeImage(employee.image).subscribe({
              next: (blob) => {
                const objectURL = URL.createObjectURL(blob);
                employee.imageURL = objectURL;
              },
            });
          });
          this.dataSource = new MatTableDataSource(
            this.pageSortResponse.content
          );
          this.dataSource.sort = this.sort;
        },
        error: (err) => {
          console.error('Error fetching data', err);
        },
      });
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
        this.loadData(this.page());
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
}
