import { NgClass } from '@angular/common';
import { Component, inject } from '@angular/core';
import {
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
import { DepartmentService } from '../../../core/Services/department.service';

@Component({
  selector: 'app-edit-department-popup',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass, MatButtonModule, MatDialogModule],
  templateUrl: './edit-department-popup.component.html',
  styleUrl: './edit-department-popup.component.css',
})
export class EditDepartmentPopupComponent {
  private departmentService = inject(DepartmentService);
  toast: ToastrService = inject(ToastrService);
  private data = inject(MAT_DIALOG_DATA);

  constructor(private ref: MatDialogRef<EditDepartmentPopupComponent>) {}
  ngOnInit(): void {
    this.editDepartmentForm.setValue({
      departmentName: this.data.deptName,
    });
  }
  editDepartmentForm = new FormGroup({
    departmentName: new FormControl('', [Validators.required]),
  });
  get departmentName(): FormControl {
    return this.editDepartmentForm.get('departmentName') as FormControl;
  }
  submitEditDepartmentForm() {
    this.departmentService
      .editDepartment(this.data.id, this.editDepartmentForm.value as FormData)
      .subscribe((res) => {
        this.toast.success('Edited Successfully', 'Success', {
          timeOut: 3000,
          closeButton: true,
        });
      });
    this.editDepartmentForm.reset();
    this.ref.close();
  }
}
