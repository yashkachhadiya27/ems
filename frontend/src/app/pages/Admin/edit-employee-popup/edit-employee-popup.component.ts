import { NgClass } from '@angular/common';
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
import { ToastrService } from 'ngx-toastr';
import { EditEmployee } from '../../../core/Interfaces/EmployeeModel';
import { EmployeeService } from '../../../core/Services/employee.service';
import { DepartmentService } from '../../../core/Services/department.service';
@Component({
  selector: 'app-edit-employee-popup',
  standalone: true,
  imports: [MatButtonModule, MatDialogModule, NgClass, ReactiveFormsModule],
  templateUrl: './edit-employee-popup.component.html',
  styleUrl: './edit-employee-popup.component.css',
})
export class EditEmployeePopupComponent implements OnInit {
  private toastr = inject(ToastrService);
  private data = inject(MAT_DIALOG_DATA);
  private employeeService = inject(EmployeeService);
  private departmentService = inject(DepartmentService);
  deptArray: { id: number; departmentName: string }[] = [];

  userData: EditEmployee = {
    fname: '',
    mname: '',
    lname: '',
    email: '',
    department: '',
    phone: '',
    dateOfBirth: '',
    dateOfJoining: '',
  };
  constructor(private ref: MatDialogRef<EditEmployeePopupComponent>) {}
  ngOnInit(): void {
    this.employeeService.getEditEmployeeDetail(this.data.email).subscribe({
      next: (res: EditEmployee) => {
        this.userData = res;
        this.editEmpForm.setValue({
          fname: this.userData.fname,
          mname: this.userData.mname,
          lname: this.userData.lname,
          email: this.userData.email,
          department: this.userData.department,
          phone: this.userData.phone,
          dateOfBirth: this.userData.dateOfBirth,
          dateOfJoining: this.userData.dateOfJoining,
        });
      },
    });
    this.departmentService.getDepartments().subscribe({
      next: (res) => {
        this.deptArray = res as { id: number; departmentName: string }[];
      },
    });
  }
  editEmpForm = new FormGroup({
    fname: new FormControl('', [
      Validators.required,
      Validators.pattern("([a-zA-Z',.-]+( [a-zA-Z',.-]+)*){3,30}"),
    ]),
    mname: new FormControl('', [
      Validators.required,
      Validators.pattern("([a-zA-Z',.-]+( [a-zA-Z',.-]+)*){3,30}"),
    ]),
    lname: new FormControl('', [
      Validators.required,
      Validators.pattern("([a-zA-Z',.-]+( [a-zA-Z',.-]+)*){3,30}"),
    ]),
    email: new FormControl('', [
      Validators.required,
      Validators.pattern(
        '^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$'
      ),
    ]),
    phone: new FormControl('', [
      Validators.required,
      Validators.minLength(10),
      Validators.maxLength(10),
      Validators.pattern('^[6-9][0-9]{9}$'),
    ]),
    department: new FormControl('', [Validators.required]),
    dateOfBirth: new FormControl('', [
      Validators.required,
      this.dateInPastValidator,
    ]),
    dateOfJoining: new FormControl('', {
      validators: [Validators.required, this.dateInPastValidator],
    }),
  });
  get fname(): FormControl {
    return this.editEmpForm.get('fname') as FormControl;
  }
  get mname(): FormControl {
    return this.editEmpForm.get('mname') as FormControl;
  }
  get lname(): FormControl {
    return this.editEmpForm.get('lname') as FormControl;
  }
  get email(): FormControl {
    return this.editEmpForm.get('email') as FormControl;
  }
  get phone(): FormControl {
    return this.editEmpForm.get('phone') as FormControl;
  }
  get department(): FormControl {
    return this.editEmpForm.get('department') as FormControl;
  }
  get dateOfBirth(): FormControl {
    return this.editEmpForm.get('dateOfBirth') as FormControl;
  }
  get dateOfJoining(): FormControl {
    return this.editEmpForm.get('dateOfJoining') as FormControl;
  }
  closePopup() {
    this.ref.close(false);
  }
  dateInPastValidator(control: AbstractControl) {
    const val = control?.value;
    if (val) {
      const selectedDate = new Date(val);

      const today = new Date();
      today.setHours(0, 0, 0, 0);
      console.log(selectedDate < today);

      return selectedDate < today ? null : { notInPast: true };
    }
    return null;
  }
  editEmployee() {
    const formData = new FormData();
    formData.append('fname', this.fname.value);
    formData.append('mname', this.mname.value);
    formData.append('lname', this.lname.value);
    formData.append('email', this.email.value);
    formData.append('department', this.department.value);
    formData.append('phone', this.phone.value);
    formData.append('dateOfBirth', this.dateOfBirth.value);
    formData.append('dateOfJoining', this.dateOfJoining.value);
    this.employeeService
      .editEmployeeDetail(this.data.email, formData)
      .subscribe((res) => {
        this.ref.close(true);

        this.toastr.success('Edited Successfully', 'Success', {
          timeOut: 3000,
          closeButton: true,
        });
      });
  }
}
