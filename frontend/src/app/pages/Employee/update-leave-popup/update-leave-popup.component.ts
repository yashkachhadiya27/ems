import { Component, inject, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { LeaveService } from '../../../core/Services/leave.service';
import { ToastrService } from 'ngx-toastr';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-update-leave-popup',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule, ReactiveFormsModule, NgClass],
  templateUrl: './update-leave-popup.component.html',
  styleUrl: './update-leave-popup.component.css',
})
export class UpdateLeavePopupComponent implements OnInit {
  public data = inject(MAT_DIALOG_DATA);
  private ref = inject(MatDialogRef<UpdateLeavePopupComponent>);
  private leaveService = inject(LeaveService);
  private toast = inject(ToastrService);
  employeeEmail!: string;
  ngOnInit(): void {
    this.employeeEmail = localStorage.getItem('email') as string;
    this.updateLeaveForm.patchValue({
      reason: this.data.leaveData.reason,
      leaveFromDate: this.data.leaveData.leaveFromDate,
      leaveToDate: this.data.leaveData.leaveToDate,
      leaveType: this.data.leaveData.leaveType,
    });
  }
  TypeOfLeave: string[] = [
    'Restricted Holiday',
    'Leaves with Pay',
    'Loss of Pay',
    'Work From Home',
  ];

  updateLeaveForm = new FormGroup(
    {
      reason: new FormControl('', Validators.required),
      leaveFromDate: new FormControl('', [
        Validators.required,
        this.fromDateValidator,
      ]),
      leaveToDate: new FormControl('', {
        validators: [Validators.required],
        updateOn: 'change',
      }),
      leaveType: new FormControl('', Validators.required),
    },
    { validators: this.dateRangeValidator }
  );
  get reason(): FormControl {
    return this.updateLeaveForm.get('reason') as FormControl;
  }
  get leaveFromDate(): FormControl {
    return this.updateLeaveForm.get('leaveFromDate') as FormControl;
  }
  get leaveToDate(): FormControl {
    return this.updateLeaveForm.get('leaveToDate') as FormControl;
  }
  get leaveType(): FormControl {
    return this.updateLeaveForm.get('leaveType') as FormControl;
  }
  dateRangeValidator(control: AbstractControl) {
    const fromDateControl = control.get('leaveFromDate');
    const toDateControl = control.get('leaveToDate');

    if (
      (fromDateControl?.touched || fromDateControl?.dirty) &&
      (toDateControl?.touched || toDateControl?.dirty)
    ) {
      const fromDate = fromDateControl.value;
      const toDate = toDateControl.value;
      if (!fromDate || !toDate) {
        return null;
      }

      const from = new Date(fromDate);
      const to = new Date(toDate);
      const today = new Date();
      if (to < from) {
        return { toDateBeforeFromDate: true };
      }
    }

    return null;
  }
  fromDateValidator(control: AbstractControl) {
    const fromDate = control.value;
    if (!fromDate) {
      return null;
    }

    const from = new Date(fromDate);
    const today = new Date();
    if (from < today) {
      return { fromDateInPast: true };
    }

    return null;
  }
  setFormData() {
    const formData = new FormData();
    formData.append('reason', this.reason.value);
    formData.append('leaveFromDate', this.leaveFromDate.value);
    formData.append('leaveToDate', this.leaveToDate.value);
    formData.append('leaveType', this.leaveType.value);
    return formData;
  }
  updateLeave() {
    const formData: FormData = this.setFormData();

    this.leaveService
      .updateLeaveRequest(this.data.leaveData.id, formData)
      .subscribe({
        next: (res: { message: string; code: number }) => {
          if (res.code === 200) {
            this.toast.success('Leave Updated Successfully', 'Success', {
              timeOut: 3000,
              closeButton: true,
            });
            this.ref.close();
          } else {
            this.toast.error('Leave Not Updated ', 'Error', {
              timeOut: 3000,
              closeButton: true,
            });
          }
        },
        error: (error) => {
          this.toast.error('Leave Not Updated ', 'Error', {
            timeOut: 3000,
            closeButton: true,
          });
        },
      });
  }
}
