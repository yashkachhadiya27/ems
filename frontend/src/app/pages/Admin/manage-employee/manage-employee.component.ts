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
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';
import * as alertify from 'alertifyjs';
import { saveAs } from 'file-saver';
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
import { EditEmployeePopupComponent } from '../edit-employee-popup/edit-employee-popup.component';
import { UserDetailsPopupComponent } from '../user-details-popup/user-details-popup.component';
@Component({
  selector: 'app-manage-employee',
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
    DatePipe,
    CurrencyPipe,
    RouterLink,
    RouterLinkActive,
    RouterOutlet,
    MatDialogModule,
    FormsModule,
    MatOptionModule,
    MatSelectModule,
  ],
  templateUrl: './manage-employee.component.html',
  styleUrl: './manage-employee.component.css',
})
export class ManageEmployeeComponent implements OnInit {
  private employeeService = inject(EmployeeService);
  private toast: ToastrService = inject(ToastrService);
  private dialog: MatDialog = inject(MatDialog);
  private route: Router = inject(Router);
  @ViewChild(MatSort) sort!: MatSort;
  itemsPerPage = 5;
  pageSizeOptions = [5, 10, 15, 20];
  @ViewChild('myInput') searchElement!: ElementRef;

  dataSource: any;
  pageSortResponse: any;
  pageInfo: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  } = { size: 5, number: 0, totalElements: 0, totalPages: 0 };
  totalemp!: number;
  searchdata!: string;
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
    'gender',
    'phone',
    'department',
    'dateOfBirth',
    'dateOfJoining',
    'action',
  ];
  ngOnInit(): void {
    this.loadSearchData(this.pageAndSort());
  }

  loadSearchData(pageAndSort: paginationSorting) {
    this.employeeService
      .getAllEmployee(this.searchdata, pageAndSort)
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
  nextPage() {
    this.pageAndSort.update((current) => ({
      ...current,
      pageNumber: (current.pageNumber || 0) + 1,
    }));
    this.loadSearchData(this.pageAndSort());
  }
  prevPage() {
    this.pageAndSort.update((current) => ({
      ...current,
      pageNumber: Math.max((current.pageNumber || 0) - 1, 0),
    }));
    this.loadSearchData(this.pageAndSort());
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
          return this.employeeService.getAllEmployee(query, this.pageAndSort());
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

  addEmployee() {
    this.route.navigate(['/admin/add-employee']);
  }
  editEmployee(email: string) {
    const _editUserPopup = this.dialog.open(EditEmployeePopupComponent, {
      width: '50%',
      enterAnimationDuration: '350ms',
      exitAnimationDuration: '350ms',
      data: {
        email: email,
      },
    });
    _editUserPopup.afterClosed().subscribe((r) => {
      if (r) {
        this.pageAndSort.set({
          pageNumber: 0,
          pageSize: 5,
          sortBy: 'id',
          sortOrder: 'asc',
        });
        this.itemsPerPage = 5;
        this.loadSearchData(this.pageAndSort());
      }
    });
  }
  deleteEmployee(email: string) {
    alertify
      .confirm(
        'Remove User',
        'Are you Sure to remove this user?',
        () => {
          this.employeeService.deleteEmployee(email).subscribe({
            next: (res) => {
              this.pageAndSort.set({
                pageNumber: 0,
                pageSize: 5,
                sortBy: 'id',
                sortOrder: 'asc',
              });
              this.itemsPerPage = 5;
              this.loadSearchData(this.pageAndSort());
              this.toast.success('Deleted Successfully', 'Success', {
                timeOut: 3000,
                closeButton: true,
              });
            },
          });
        },
        function () {}
      )
      .set('labels', { ok: 'Yes', cancel: 'No' })
      .set({ transition: 'flipx' })
      .set('movable', false);
  }
  moreInfoEmployee(email: string) {
    this.dialog.open(UserDetailsPopupComponent, {
      width: '50%',
      enterAnimationDuration: '350ms',
      exitAnimationDuration: '350ms',
      data: {
        email: email,
      },
    });
  }
  onItemsPerPageChange(event: any) {
    this.pageAndSort.update((current) => ({
      ...current,
      pageNumber: 0,
      pageSize: event.value,
    }));
    this.loadSearchData(this.pageAndSort());
  }
  donwloadEmpData() {
    this.employeeService.downloadCsv().subscribe((res) => {
      if (res.body) {
        const blob = new Blob([res.body], { type: 'text/csv' });
        saveAs(blob, 'employees.csv');
      }
    });
  }
}
// loadData(pageAndSort: paginationSorting) {
//   this.employeeService.getAllEmployee(pageAndSort).subscribe({
//     next: (res) => {
//       this.pageSortResponse = res;
//       this.pageInfo = this.pageSortResponse.page;

//       this.pageSortResponse.content.forEach((employee: any) => {
//         this.employeeService.getEmployeeImage(employee.image).subscribe({
//           next: (blob) => {
//             const objectURL = URL.createObjectURL(blob);
//             employee.imageURL = objectURL;
//           },
//         });
//       });
//       this.dataSource = new MatTableDataSource(this.pageSortResponse.content);
//       this.dataSource.sort = this.sort;
//     },
//   });
// }
