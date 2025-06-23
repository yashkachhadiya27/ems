import { Component, inject, OnInit } from '@angular/core';
import {
  FormArray,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatChipInputEvent, MatChipsModule } from '@angular/material/chips';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { provideNativeDateAdapter } from '@angular/material/core';
import { EmployeeService } from '../../../core/Services/employee.service';
import { idName, project } from '../../../core/Interfaces/ProjectModel';
import { ToastrService } from 'ngx-toastr';
@Component({
  selector: 'app-add-project-popup',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatChipsModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatDialogModule,
  ],
  providers: [provideNativeDateAdapter()],
  templateUrl: './add-project-popup.component.html',
  styleUrl: './add-project-popup.component.css',
})
export class AddProjectPopupComponent implements OnInit {
  private data = inject(MAT_DIALOG_DATA);
  private toast = inject(ToastrService);
  private employeeService = inject(EmployeeService);
  separatorKeysCodes: number[] = [ENTER, COMMA];
  addOnBlur = true;
  projectForm: FormGroup;
  technologies: string[] = [];
  projects = [
    'Fresh Concepts',
    'IRIS IFile',
    'eGov TTMS (JCF)',
    '	MEDplat',
    'KPMG - SOFY',
    'DSG',
  ];
  employees: idName[] = [];
  isProjectTextInput = false;
  constructor(private ref: MatDialogRef<AddProjectPopupComponent>) {
    this.projectForm = new FormGroup({
      project: new FormControl('', Validators.required),
      isProjectNotPresent: new FormControl(false),
      joiningDate: new FormControl('', Validators.required),
      endDate: new FormControl(''),
      reportingTo: new FormControl('', Validators.required),
      technology: new FormControl(''),
    });
  }
  ngOnInit(): void {
    this.employeeService.getIdName().subscribe({
      next: (res: idName[]) => {
        this.employees = res;
      },
    });
  }
  myFilter = (d: Date | null): boolean => {
    const day = (d || new Date()).getDay();
    return day !== 0 && day !== 6;
  };
  get technologiesArray(): FormArray {
    return this.projectForm.get('technologies') as FormArray;
  }
  toggleProjectInput(): void {
    this.isProjectTextInput = this.projectForm.get(
      'isProjectNotPresent'
    )?.value;
    this.projectForm.get('project')?.reset();
    this.projectForm
      .get('project')
      ?.setValidators(this.isProjectTextInput ? [Validators.required] : null);
    this.projectForm.get('project')?.updateValueAndValidity();
  }
  addTechnology(event: MatChipInputEvent): void {
    const input = event.input;
    const value = (event.value || '').trim();

    if (value) {
      this.technologies.push(value);
    }

    if (input) {
      input.value = '';
    }
  }

  removeTechnology(tech: string): void {
    const index = this.technologies.indexOf(tech);

    if (index >= 0) {
      this.technologies.splice(index, 1);
    }
  }
  convertDate(value: any) {
    const date = new Date(value);
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const year = date.getFullYear();
    return `${year}-${month}-${day}`;
  }
  onSubmit(): void {
    const formData: project = {
      projectName: this.projectForm.get('project')?.value,
      reportingToId: this.projectForm.get('reportingTo')?.value.id,
      projectJoinDate: this.convertDate(
        this.projectForm.get('joiningDate')?.value
      ),
      projectEndDate: this.convertDate(this.projectForm.get('endDate')?.value),
      technologies: this.technologies,
      userId: this.data.id,
    };
    this.employeeService.addProject(formData).subscribe({
      next: (res) => {
        this.toast.success('Added Successfully', 'Success', {
          timeOut: 3000,
          closeButton: true,
        });
        this.ref.close();
      },
    });
  }

  onCancel(): void {
    this.projectForm.reset();
    this.technologies = [];
  }
}
