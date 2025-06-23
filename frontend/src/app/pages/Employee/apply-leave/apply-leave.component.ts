import { NgClass } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { ToastrService } from 'ngx-toastr';
import { LeaveService } from '../../../core/Services/leave.service';
@Component({
  selector: 'app-apply-leave',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass, MatButtonModule],
  templateUrl: './apply-leave.component.html',
  styleUrl: './apply-leave.component.css',
})
export class ApplyLeaveComponent implements OnInit {
  private leaveService = inject(LeaveService);
  private toast = inject(ToastrService);
  employeeEmail!: string;
  ngOnInit(): void {
    this.employeeEmail = localStorage.getItem('email') as string;
  }
  TypeOfLeave: string[] = [
    'Restricted Holiday',
    'Leaves with Pay',
    'Loss of Pay',
    'Work From Home',
  ];

  applyLeaveForm = new FormGroup(
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
    return this.applyLeaveForm.get('reason') as FormControl;
  }
  get leaveFromDate(): FormControl {
    return this.applyLeaveForm.get('leaveFromDate') as FormControl;
  }
  get leaveToDate(): FormControl {
    return this.applyLeaveForm.get('leaveToDate') as FormControl;
  }
  get leaveType(): FormControl {
    return this.applyLeaveForm.get('leaveType') as FormControl;
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
  applyLeave() {
    const formData: FormData = this.setFormData();
    this.leaveService.applyLeave(this.employeeEmail, formData).subscribe({
      next: (res: { message: string; code: number }) => {
        if (res.code === 200) {
          this.applyLeaveForm.reset();
          this.toast.success('Leave Applied Successfully', 'Success', {
            timeOut: 3000,
            closeButton: true,
          });
        } else {
          this.toast.error('Leave Not Applied ', 'Error', {
            timeOut: 3000,
            closeButton: true,
          });
        }
      },
      error: (error) => {
        this.toast.error('Leave Not Applied ', 'Error', {
          timeOut: 3000,
          closeButton: true,
        });
      },
    });
  }
}
