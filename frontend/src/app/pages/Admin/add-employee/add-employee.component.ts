import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import {
  AbstractControl,
  FormArray,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatTooltipModule } from '@angular/material/tooltip';
import { address } from '../../../core/Interfaces/AuthModel';
import { ToastrService } from 'ngx-toastr';
import { EmployeeService } from '../../../core/Services/employee.service';
import { DepartmentService } from '../../../core/Services/department.service';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
@Component({
  selector: 'app-add-employee',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    MatCardModule,
    MatTooltipModule,
    MatIconModule,
    MatButtonModule,
  ],
  templateUrl: './add-employee.component.html',
  styleUrl: './add-employee.component.css',
})
export class AddEmployeeComponent implements OnInit {
  private employeeService = inject(EmployeeService);
  private toast = inject(ToastrService);
  private departmentService = inject(DepartmentService);
  private isSubmitted = signal<boolean>(false);
  deptArray: { id: number; departmentName: string }[] = [];

  countryArray: string[] = ['India', 'USA', 'Canada', 'Germany'];
  ngOnInit(): void {
    this.departmentService.getDepartments().subscribe({
      next: (res) => {
        this.deptArray = res as { id: number; departmentName: string }[];
      },
    });
  }
  addEmpForm = new FormGroup({
    fname: new FormControl('', [
      Validators.required,
      Validators.pattern('[a-zA-Z]{3,30}'),
    ]),
    mname: new FormControl('', [
      Validators.required,
      Validators.pattern('[a-zA-Z]{3,30}'),
    ]),
    lname: new FormControl('', [
      Validators.required,
      Validators.pattern('[a-zA-Z]{3,30}'),
    ]),
    email: new FormControl('', [
      Validators.required,
      Validators.pattern('^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}$'),
    ]),
    phone: new FormControl('', [
      Validators.required,
      Validators.minLength(10),
      Validators.maxLength(10),
      Validators.pattern('^[6-9][0-9]{9}$'),
    ]),
    gender: new FormControl('male', [Validators.required]),
    department: new FormControl('', [Validators.required]),
    dateOfBirth: new FormControl('', [
      Validators.required,
      this.dateInPastValidator,
    ]),
    dateOfJoining: new FormControl('', {
      validators: [Validators.required, this.dateInPastValidator],
    }),
    image: new FormControl('', [Validators.required]),
    address: new FormGroup({
      street: new FormControl(null, Validators.required),
      country: new FormControl('India', Validators.required),
      city: new FormControl(null, Validators.required),
      district: new FormControl(null, Validators.required),
      state: new FormControl(null, Validators.required),
      postal: new FormControl(null, Validators.required),
    }),
    skills: new FormArray([]),
    experience: new FormArray([]),
  });
  AddExperience() {
    const frmgroup = new FormGroup(
      {
        company: new FormControl(null, [Validators.required]),
        position: new FormControl(null, [Validators.required]),
        totalExp: new FormControl({ value: null, disabled: true }),
        startDate: new FormControl(null, {
          validators: [Validators.required],
          updateOn: 'change',
        }),
        endDate: new FormControl(null, {
          validators: [Validators.required],
          updateOn: 'change',
        }),
      },
      { validators: this.totalExperienceValidator }
    );

    (<FormArray>this.addEmpForm.get('experience')).push(frmgroup);
  }
  DeleteSkill(index: number) {
    const controls = <FormArray>this.addEmpForm.get('skills');
    controls.removeAt(index);
  }
  AddSkills() {
    (<FormArray>this.addEmpForm.get('skills')).push(
      new FormControl(null, Validators.required)
    );
  }
  dateInPastValidator(control: AbstractControl) {
    const selectedDate = new Date(control?.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return selectedDate < today ? null : { notInPast: true };
  }

  totalExperienceValidator(group: AbstractControl) {
    const startDate = group.get('startDate')?.value;
    const endDate = group.get('endDate')?.value;
    const totalExpControl = group.get('totalExp');
    const today = new Date();
    if (startDate && endDate) {
      const start = new Date(startDate);
      const end = new Date(endDate);

      today.setHours(0, 0, 0, 0);
      start.setHours(0, 0, 0, 0);
      end.setHours(0, 0, 0, 0);
      if (start >= today || end >= today) {
        return { dateInvalid: true };
      }
      if (start > end) {
        return { dateOrderInvalid: true };
      }

      const yearsDiff = end.getFullYear() - start.getFullYear();
      const monthsDiff = end.getMonth() - start.getMonth();
      const totalMonths = yearsDiff * 12 + monthsDiff;

      const years = Math.floor(totalMonths / 12);
      const months = totalMonths % 12;

      const totalExp = `${years} Year ${
        months < 10 ? '0' + months : months
      } Month`;

      totalExpControl?.setValue(totalExp, { emitEvent: false, onlySelf: true });

      return null;
    }

    return null;
  }

  get fname(): FormControl {
    return this.addEmpForm.get('fname') as FormControl;
  }
  get mname(): FormControl {
    return this.addEmpForm.get('mname') as FormControl;
  }
  get lname(): FormControl {
    return this.addEmpForm.get('lname') as FormControl;
  }
  get email(): FormControl {
    return this.addEmpForm.get('email') as FormControl;
  }
  get phone(): FormControl {
    return this.addEmpForm.get('phone') as FormControl;
  }
  get gender(): FormControl {
    return this.addEmpForm.get('gender') as FormControl;
  }
  get department(): FormControl {
    return this.addEmpForm.get('department') as FormControl;
  }
  get dob(): FormControl {
    return this.addEmpForm.get('dateOfBirth') as FormControl;
  }
  get doj(): FormControl {
    return this.addEmpForm.get('dateOfJoining') as FormControl;
  }
  get image(): FormControl {
    return this.addEmpForm.get('image') as FormControl;
  }

  get street(): FormControl {
    return this.addEmpForm.get('address.street') as FormControl;
  }
  get postal(): FormControl {
    return this.addEmpForm.get('address.postal') as FormControl;
  }
  get district(): FormControl {
    return this.addEmpForm.get('address.district') as FormControl;
  }
  get state(): FormControl {
    return this.addEmpForm.get('address.state') as FormControl;
  }
  get city(): FormControl {
    return this.addEmpForm.get('address.city') as FormControl;
  }
  get country(): FormControl {
    return this.addEmpForm.get('address.country') as FormControl;
  }
  get skills(): FormArray {
    return this.addEmpForm.get('skills') as FormArray;
  }
  get experience(): FormArray {
    return this.addEmpForm.get('experience') as FormArray;
  }

  filetoUpload!: File;
  onChangeFileField(event: any) {
    this.filetoUpload = event.target.files[0];
  }

  DeleteExperience(index: number) {
    const frmArray = <FormArray>this.addEmpForm.get('experience');
    frmArray.removeAt(index);
  }
  formData() {
    const addressObj: address = {
      street: this.street.value,
      postalcode: this.postal.value,
      district: this.district.value,
      state: this.state.value,
      city: this.city.value,
      country: this.country.value,
    };

    this.experience.controls.forEach((control) => {
      const group = control as FormGroup;
      group.get('totalExp')?.enable();
    });
    const formData = new FormData();
    formData.append('fname', this.fname.value);
    formData.append('mname', this.mname.value);
    formData.append('lname', this.lname.value);
    formData.append('email', this.email.value);
    formData.append('phone', this.phone.value);
    formData.append('gender', this.gender.value);
    formData.append('department', this.department.value);
    formData.append('dateOfBirth', this.dob.value);
    formData.append('dateOfJoining', this.doj.value);
    formData.append('skills', this.skills.value);
    Object.keys(addressObj).forEach((key) => {
      const value = addressObj[key as keyof address];
      formData.append(key, String(value));
    });
    formData.append('experience', JSON.stringify(this.experience.value));
    formData.append('image', this.filetoUpload);
    formData.append('otpCode', '0');
    return formData;
  }
  addEmployee() {
    this.isSubmitted.set(true);
    const formData = this.formData();
    this.addEmpForm.reset();
    this.toast.success('Employee Added Successfully', 'Success', {
      timeOut: 3000,
      closeButton: true,
    });
    this.employeeService.addEmployee(formData).subscribe({
      next: (resp) => {},
      error: (error) => {
        this.toast.error('Some intenal error occured', 'Error', {
          timeOut: 3000,
          closeButton: true,
        });
      },
    });
  }
  canExit() {
    if (this.addEmpForm.dirty && !this.isSubmitted()) {
      return confirm('You have unsaved Changes.Do you want to navigate away?');
    } else {
      return true;
    }
  }
}
