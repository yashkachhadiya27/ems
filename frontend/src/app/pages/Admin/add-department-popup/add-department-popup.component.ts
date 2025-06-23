import { NgClass } from '@angular/common';
import { Component, inject } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';

import { ToastrService } from 'ngx-toastr';
import { DepartmentService } from '../../../core/Services/department.service';
@Component({
  selector: 'app-add-department-popup',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass, MatButtonModule, MatDialogModule],
  templateUrl: './add-department-popup.component.html',
  styleUrl: './add-department-popup.component.css',
})
export class AddDepartmentPopupComponent {
  private departmentService = inject(DepartmentService);
  toast: ToastrService = inject(ToastrService);
  constructor(private ref: MatDialogRef<AddDepartmentPopupComponent>) {}
  addDepartmentForm = new FormGroup({
    departmentName: new FormControl('', [Validators.required]),
  });
  get departmentName(): FormControl {
    return this.addDepartmentForm.get('departmentName') as FormControl;
  }
  submitAddDepartmentForm() {
    this.departmentService
      .addDepartment(this.addDepartmentForm.value as FormData)
      .subscribe((res) => {
        this.toast.success('Added Successfully', 'Success', {
          timeOut: 3000,
          closeButton: true,
        });
      });
    this.addDepartmentForm.reset();
    this.ref.close();
  }
}
