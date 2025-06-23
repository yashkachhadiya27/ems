import { CurrencyPipe, DatePipe } from '@angular/common';
import {
  Component,
  ElementRef,
  inject,
  signal,
  ViewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatOptionModule } from '@angular/material/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { pagination } from '../../../core/Interfaces/AuthModel';
import { LeaveService } from '../../../core/Services/leave.service';
import { UpdateLeavePopupComponent } from '../update-leave-popup/update-leave-popup.component';
import * as alertify from 'alertifyjs';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-leave-history',
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
    MatChipsModule,
  ],
  templateUrl: './leave-history.component.html',
  styleUrl: './leave-history.component.css',
})
export class LeaveHistoryComponent {
  private leaveService = inject(LeaveService);
  private dialog = inject(MatDialog);
  private toast = inject(ToastrService);
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('myInput') searchElement!: ElementRef;
  itemsPerPage = 5;
  pageSizeOptions = [5, 10, 15, 20];
  searchdata!: string;
  dataSource: any;
  pageSortResponse: any;
  pageInfo: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  } = { size: 5, number: 0, totalElements: 0, totalPages: 0 };
  totalemp!: number;
  userId!: number;
  page = signal<pagination>({
    pageNumber: 0,
    pageSize: 5,
  });

  displayColumns: string[] = [
    'leaveType',
    'reason',
    'leaveFromDate',
    'leaveToDate',
    'status',
  ];
  ngOnInit(): void {
    this.userId = +(localStorage.getItem('empId') as string);
    this.loadSearchData(this.page());
  }
  loadSearchData(page: pagination) {
    this.leaveService.getAllLeaveOfEmployee(this.userId, page).subscribe({
      next: (res) => {
        this.pageSortResponse = res;
        this.pageInfo = this.pageSortResponse.page;
        this.dataSource = new MatTableDataSource(this.pageSortResponse.content);
        this.dataSource.sort = this.sort;
      },
    });
  }
  prevPage() {
    this.page.update((current) => ({
      ...current,
      pageNumber: Math.max((current.pageNumber || 0) - 1, 0),
    }));
    this.loadSearchData(this.page());
  }
  nextPage() {
    this.page.update((current) => ({
      ...current,
      pageNumber: (current.pageNumber || 0) + 1,
    }));
    this.loadSearchData(this.page());
  }
  onItemsPerPageChange(event: any) {
    this.page.update((current) => ({
      ...current,
      pageNumber: 0,
      pageSize: event.value,
    }));
    this.loadSearchData(this.page());
  }
  updateLeave(element: any) {
    const _updateLeave = this.dialog.open(UpdateLeavePopupComponent, {
      width: '50%',
      enterAnimationDuration: '350ms',
      exitAnimationDuration: '350ms',

      data: {
        leaveData: {
          id: element.id,
          reason: element.reason,
          leaveFromDate: element.leaveFromDate,
          leaveToDate: element.leaveToDate,
          leaveType: element.leaveType,
        },
      },
    });
    _updateLeave.afterClosed().subscribe({
      next: (res) => {
        this.loadSearchData(this.page());
      },
    });
  }
  deleteLeave(id: number) {
    alertify
      .confirm(
        'Remove User',
        'Are you Sure to remove this user?',
        () => {
          this.leaveService.deleteLeave(id).subscribe({
            next: (res) => {
              this.toast.success('Leave Deleted Successfully', 'Success', {
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
}
