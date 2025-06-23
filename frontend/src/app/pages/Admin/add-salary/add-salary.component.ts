import { CurrencyPipe, DatePipe } from '@angular/common';
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
@Component({
  selector: 'app-add-salary',
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
    DatePipe,
  ],
  templateUrl: './add-salary.component.html',
  styleUrl: './add-salary.component.css',
})
export class AddSalaryComponent {
  private salaryService = inject(SalaryService);
  private employeeService = inject(EmployeeService);
  today: Date = new Date();
  @ViewChild(MatSort) sort!: MatSort;
  dataSource: any;
  departmentData: any;
  searchdata: string = '';
  itemsPerPage = 5;
  pageSizeOptions = [5, 10, 15, 20];
  pageSortResponse: any;
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
    'department',
    'email',
    'basic',
    'allowance',
    'total',
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
    this.salaryService.getAllEmpDetails(this.searchdata, page).subscribe({
      next: (res) => {
        this.pageSortResponse = res;
        this.pageInfo = this.pageSortResponse.page;
        this.pageSortResponse.content.forEach((employee: any) => {
          employee.basic = 0;
          employee.allowance = 0;
          this.employeeService.getEmployeeImage(employee.image).subscribe({
            next: (blob) => {
              const objectURL = URL.createObjectURL(blob);
              employee.imageURL = objectURL;
            },
          });
        });
        this.dataSource = new MatTableDataSource(this.pageSortResponse.content);
        this.dataSource.sort = this.sort;
      },
    });
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
          return this.salaryService.getAllEmpDetails(query, this.page());
        })
      )
      .subscribe({
        next: (res) => {
          this.pageSortResponse = res;
          this.pageInfo = this.pageSortResponse.page;
          this.pageSortResponse.content.forEach((employee: any) => {
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
  onItemsPerPageChange(event: any) {
    this.page.update((current) => ({
      ...current,
      pageNumber: 0,
      pageSize: event.value,
    }));
    this.loadData(this.page());
  }
  addSalary(element: any) {
    const formData = new FormData();
    formData.append('basic', element.basic);
    formData.append('allowance', element.allowance);
    this.salaryService.addEmpSalary(element.id, formData).subscribe((res) => {
      element.basic = 0;
      element.allowance = 0;
    });
  }
  calculateTotal(row: any): void {
    row.total = row.basic + row.allowance;
  }
}
