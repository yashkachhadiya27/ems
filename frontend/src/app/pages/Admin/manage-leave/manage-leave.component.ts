import { CurrencyPipe, DatePipe } from '@angular/common';
import {
  Component,
  ElementRef,
  inject,
  OnInit,
  signal,
  ViewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatOptionModule } from '@angular/material/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
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
import { EmployeeService } from '../../../core/Services/employee.service';
import { LeaveService } from '../../../core/Services/leave.service';
@Component({
  selector: 'app-manage-leave',
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
    RouterLink,
    RouterLinkActive,
    RouterOutlet,
    MatDialogModule,
    FormsModule,
    MatOptionModule,
    MatSelectModule,
    MatProgressBarModule,
  ],
  templateUrl: './manage-leave.component.html',
  styleUrl: './manage-leave.component.css',
})
export class ManageLeaveComponent implements OnInit {
  private leaveService = inject(LeaveService);
  private employeeService = inject(EmployeeService);
  private toast: ToastrService = inject(ToastrService);

  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('myInput') searchElement!: ElementRef;
  itemsPerPage = 5;
  pageSizeOptions = [5, 10, 15, 20];
  searchdata!: string;
  dataSource: any;
  pageSortResponse: any;
  isLoading = signal<boolean>(false);
  pageInfo: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  } = { size: 5, number: 0, totalElements: 0, totalPages: 0 };
  totalemp!: number;
  pageAndSort = signal<paginationSorting>({
    pageNumber: 0,
    pageSize: 5,
    sortBy: 'id',
    sortOrder: 'asc',
  });

  displayColumns: string[] = [
    'image',
    'fullName',
    'email',
    'department',
    'leaveType',
    'reason',
    'leaveFromDate',
    'leaveToDate',
    'status',
    'action',
  ];
  ngOnInit(): void {
    this.loadSearchData(this.pageAndSort());
  }
  loadSearchData(pageAndSort: paginationSorting) {
    this.leaveService
      .getAllPendingLeave(this.searchdata, pageAndSort)
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
          return this.leaveService.getAllPendingLeave(
            query,
            this.pageAndSort()
          );
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
  prevPage() {
    this.pageAndSort.update((current) => ({
      ...current,
      pageNumber: Math.max((current.pageNumber || 0) - 1, 0),
    }));
    this.loadSearchData(this.pageAndSort());
  }
  nextPage() {
    this.pageAndSort.update((current) => ({
      ...current,
      pageNumber: (current.pageNumber || 0) + 1,
    }));
    this.loadSearchData(this.pageAndSort());
  }
  onItemsPerPageChange(event: any) {
    this.pageAndSort.update((current) => ({
      ...current,
      pageNumber: 0,
      pageSize: event.value,
    }));
    this.loadSearchData(this.pageAndSort());
  }
  approveLeave(element: any) {
    alertify
      .confirm(
        'Approve Leave',
        'Are you Sure to Approve the leave of ' + element.fullName + ' ?',
        () => {
          this.isLoading.set(true);

          this.leaveService.approveLeave(element.id).subscribe((res) => {
            this.isLoading.set(false);
            this.toast.info('Leave Approved For ' + element.fullName, 'Info', {
              timeOut: 3000,
              closeButton: true,
            });
            this.pageAndSort.update((current) => ({
              pageSize: 5,
              pageNumber: 0,
              sortBy: 'id',
              sortOrder: 'asc',
            }));
            this.itemsPerPage = 5;
            this.loadSearchData(this.pageAndSort());
          });
        },
        function () {}
      )
      .set('labels', { ok: 'Yes', cancel: 'No' })
      .set({ transition: 'flipx' })
      .set('movable', false);
  }
  rejectLeave(element: any) {
    alertify
      .confirm(
        'Reject Leave',
        'Are you Sure to Reject the leave of ' + element.fullName + ' ?',
        () => {
          this.isLoading.set(true);
          this.leaveService.rejectLeave(element.id).subscribe((res) => {
            this.isLoading.set(false);

            this.toast.info('Leave Rejected For ' + element.fullName, 'Info', {
              timeOut: 3000,
              closeButton: true,
            });
            this.pageAndSort.update((current) => ({
              pageSize: 5,
              pageNumber: 0,
              sortBy: 'id',
              sortOrder: 'asc',
            }));
            this.itemsPerPage = 5;
            this.loadSearchData(this.pageAndSort());
          });
        },
        function () {}
      )
      .set('labels', { ok: 'Yes', cancel: 'No' })
      .set({ transition: 'flipx' })
      .set('movable', false);
  }
}
