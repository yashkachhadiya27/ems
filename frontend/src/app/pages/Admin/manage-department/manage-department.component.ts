import { CurrencyPipe, DatePipe } from '@angular/common';
import {
  Component,
  ElementRef,
  OnInit,
  ViewChild,
  inject,
  signal,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';

import { FormsModule } from '@angular/forms';
import { MatOptionModule } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSelectModule } from '@angular/material/select';
import * as alertify from 'alertifyjs';
import { ToastrService } from 'ngx-toastr';
import {
  debounceTime,
  distinctUntilChanged,
  fromEvent,
  map,
  switchMap,
} from 'rxjs';
import { paginationSorting } from '../../../core/Interfaces/AuthModel';
import { DepartmentService } from '../../../core/Services/department.service';
import { AddDepartmentPopupComponent } from '../add-department-popup/add-department-popup.component';
import { EditDepartmentPopupComponent } from '../edit-department-popup/edit-department-popup.component';
@Component({
  selector: 'app-manage-department',
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
    MatMenuModule,
    DatePipe,
    CurrencyPipe,
    FormsModule,
    MatOptionModule,
    MatSelectModule,
  ],
  templateUrl: './manage-department.component.html',
  styleUrl: './manage-department.component.css',
})
export class ManageDepartmentComponent implements OnInit {
  private departmentService = inject(DepartmentService);
  toast: ToastrService = inject(ToastrService);
  constructor(private dialog: MatDialog) {}
  @ViewChild(MatSort) sort!: MatSort;
  dataSource: any;
  departmentData: any;
  searchdata: string = '';
  itemsPerPage = 5;
  pageSizeOptions = [5, 10, 15, 20];
  pageSortResponse: any;
  pageAndSort = signal<paginationSorting>({
    pageNumber: 0,
    pageSize: 5,
    sortBy: 'id',
    sortOrder: 'asc',
  });
  pageInfo: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  } = { size: 5, number: 0, totalElements: 0, totalPages: 0 };
  @ViewChild('myInput') searchElement!: ElementRef;

  displayColumns: string[] = ['id', 'name', 'action'];
  ngOnInit(): void {
    this.loadData(this.pageAndSort());
  }

  nextPage() {
    this.pageAndSort.update((current) => ({
      ...current,
      pageNumber: (current.pageNumber || 0) + 1,
    }));
    this.loadData(this.pageAndSort());
  }
  prevPage() {
    this.pageAndSort.update((current) => ({
      ...current,
      pageNumber: Math.max((current.pageNumber || 0) - 1, 0),
    }));
    this.loadData(this.pageAndSort());
  }

  pageNumber = 0;
  loadData(pageAndSort: paginationSorting) {
    this.departmentService
      .getAllDepartment(this.searchdata, pageAndSort)
      .subscribe({
        next: (res) => {
          this.pageSortResponse = res;
          this.pageInfo = this.pageSortResponse.page;
          // this.pageSortResponse.content.forEach((ele: any) => {
          //   ele.count = cnt++;
          // });
          this.dataSource = new MatTableDataSource(
            this.pageSortResponse.content
          );
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
          this.pageAndSort.update((current) => ({
            pageSize: 5,
            pageNumber: 0,
            sortBy: 'id',
            sortOrder: 'asc',
          }));
          this.itemsPerPage = 5;
          return this.departmentService.getAllDepartment(
            query,
            this.pageAndSort()
          );
        })
      )
      .subscribe({
        next: (res) => {
          this.pageSortResponse = res;
          this.pageInfo = this.pageSortResponse.page;
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
  addDepartment() {
    const _addDepartment = this.dialog.open(AddDepartmentPopupComponent, {
      enterAnimationDuration: '350ms',
      exitAnimationDuration: '350ms',
    });
    _addDepartment.afterClosed().subscribe({
      next: (res) => {
        this.pageAndSort.set({
          pageNumber: 0,
          pageSize: 5,
          sortBy: 'id',
          sortOrder: 'asc',
        });
        this.itemsPerPage = 5;
        this.loadData(this.pageAndSort());
      },
    });
  }
  editDepartment(element: any) {
    const _editUserPopup = this.dialog.open(EditDepartmentPopupComponent, {
      enterAnimationDuration: '350ms',
      exitAnimationDuration: '350ms',
      data: {
        id: element.id,
        deptName: element.departmentName,
      },
    });
    _editUserPopup.afterClosed().subscribe((r) => {
      this.itemsPerPage = 5;
      this.pageAndSort.set({
        pageNumber: 0,
        pageSize: 5,
        sortBy: 'id',
        sortOrder: 'asc',
      });

      this.loadData(this.pageAndSort());
    });
  }
  deleteDepartment(id: any) {
    alertify
      .confirm(
        'Delete Department',
        'Are you Sure to Delete Department?',
        () => {
          this.departmentService.deleteDepartment(id).subscribe((res) => {
            this.toast.success('Department Deleted Successfully', 'Success', {
              timeOut: 3000,
              closeButton: true,
            });
            this.itemsPerPage = 5;
            this.pageAndSort.set({
              pageNumber: 0,
              pageSize: 5,
              sortBy: 'id',
              sortOrder: 'asc',
            });
            this.loadData(this.pageAndSort());
          });
        },
        function () {}
      )
      .set('labels', { ok: 'Yes', cancel: 'No' })
      .set({ transition: 'flipx' })
      .set('movable', false);
  }
  onItemsPerPageChange(event: any) {
    this.pageAndSort.update((current) => ({
      ...current,
      pageNumber: 0,
      pageSize: event.value,
    }));
    this.loadData(this.pageAndSort());
  }
}
