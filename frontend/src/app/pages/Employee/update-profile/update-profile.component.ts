import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ToastrService } from 'ngx-toastr';
import { address } from '../../../core/Interfaces/AuthModel';
import { User } from '../../../core/Interfaces/EmployeeModel';
import { EmployeeService } from '../../../core/Services/employee.service';
import { VerifyEmailPopupComponent } from '../../Common/verify-email-popup/verify-email-popup.component';
import { UpdateProfileImagePopupComponent } from '../update-profile-image-popup/update-profile-image-popup.component';
import { ChangePasswordPopupComponent } from '../../Common/change-password-popup/change-password-popup.component';
@Component({
  selector: 'app-update-profile',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    MatCardModule,
    MatTooltipModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    MatDialogModule,
  ],
  templateUrl: './update-profile.component.html',
  styleUrl: './update-profile.component.css',
})
export class UpdateProfileComponent implements OnInit {
  private employeeService = inject(EmployeeService);
  private toast = inject(ToastrService);
  private dialog = inject(MatDialog);
  userData!: User;
  userEmail!: string;
  userId!: number;
  deptArray: string[] = [
    'Frontend Developer',
    'Backend Developer',
    'Fullstack Developer',
    'Data Analyst',
    'Finance',
    'Marketing',
    'HR',
  ];
  countryArray: string[] = ['India', 'USA', 'Canada', 'Germany'];

  ngOnInit(): void {
    this.userEmail = localStorage.getItem('email') as string;
    this.userId = +(localStorage.getItem('empId') as string);
    this.employeeService.getUserData(this.userEmail).subscribe({
      next: (res: User) => {
        this.userData = res;
        this.updateProfileForm.patchValue({
          fname: this.userData.fname,
          mname: this.userData.mname,
          lname: this.userData.lname,
          phone: this.userData.phone,
          gender: this.userData.gender,
          department: this.userData.department,
          dateOfBirth: this.userData.dateOfBirth,
          dateOfJoining: this.userData.dateOfJoining,
          address: {
            street: this.userData.address.street,
            country: this.userData.address.country,
            city: this.userData.address.city,
            district: this.userData.address.district,
            postal: this.userData.address.postalcode,
            state: this.userData.address.state,
          },
        });
        this.setSkills(this.userData.skills);
      },
    });
  }

  updateProfileForm = new FormGroup({
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
    address: new FormGroup({
      street: new FormControl('', Validators.required),
      country: new FormControl('India', Validators.required),
      city: new FormControl('', Validators.required),
      district: new FormControl('', Validators.required),
      state: new FormControl('', Validators.required),
      postal: new FormControl(0, Validators.required),
    }),
    skills: new FormArray([]),
  });

  DeleteSkill(index: number) {
    const controls = <FormArray>this.updateProfileForm.get('skills');
    controls.removeAt(index);
  }
  AddSkills() {
    (<FormArray>this.updateProfileForm.get('skills')).push(
      new FormControl(null, Validators.required)
    );
  }
  setSkills(skills: string[]) {
    skills.forEach((skill: string) => {
      this.skills.push(new FormControl(skill));
    });
  }
  get fname(): FormControl {
    return this.updateProfileForm.get('fname') as FormControl;
  }
  get mname(): FormControl {
    return this.updateProfileForm.get('mname') as FormControl;
  }
  get lname(): FormControl {
    return this.updateProfileForm.get('lname') as FormControl;
  }
  get phone(): FormControl {
    return this.updateProfileForm.get('phone') as FormControl;
  }
  get gender(): FormControl {
    return this.updateProfileForm.get('gender') as FormControl;
  }
  get department(): FormControl {
    return this.updateProfileForm.get('department') as FormControl;
  }
  get dob(): FormControl {
    return this.updateProfileForm.get('dateOfBirth') as FormControl;
  }
  get doj(): FormControl {
    return this.updateProfileForm.get('dateOfJoining') as FormControl;
  }

  get street(): FormControl {
    return this.updateProfileForm.get('address.street') as FormControl;
  }
  get postal(): FormControl {
    return this.updateProfileForm.get('address.postal') as FormControl;
  }
  get district(): FormControl {
    return this.updateProfileForm.get('address.district') as FormControl;
  }
  get state(): FormControl {
    return this.updateProfileForm.get('address.state') as FormControl;
  }
  get city(): FormControl {
    return this.updateProfileForm.get('address.city') as FormControl;
  }
  get country(): FormControl {
    return this.updateProfileForm.get('address.country') as FormControl;
  }
  get skills(): FormArray {
    return this.updateProfileForm.get('skills') as FormArray;
  }
  dateInPastValidator(control: AbstractControl) {
    const selectedDate = new Date(control?.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return selectedDate < today ? null : { notInPast: true };
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
    const formData = new FormData();
    formData.append('fname', this.fname.value);
    formData.append('mname', this.mname.value);
    formData.append('lname', this.lname.value);
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
    return formData;
  }
  updateProfile() {
    const formData = this.formData();
    this.employeeService.updateProfile(this.userId, formData).subscribe({
      next: (resp) => {
        this.toast.success('Profile Updated Successfully', 'Success', {
          timeOut: 3000,
          closeButton: true,
        });
      },
      error: (error) => {
        this.toast.error('Some intenal error occured', 'Error', {
          timeOut: 3000,
          closeButton: true,
        });
      },
    });
  }
  changeEmail() {
    this.dialog.open(VerifyEmailPopupComponent, {
      width: '50%',
      enterAnimationDuration: '350ms',
      exitAnimationDuration: '350ms',
      data: {
        type: 'update',
        oldEmail: this.userEmail,
        placeholder: 'New Email',
      },
    });
  }
  changeProfileImage() {
    this.dialog.open(UpdateProfileImagePopupComponent, {
      width: '50%',
      enterAnimationDuration: '350ms',
      exitAnimationDuration: '350ms',
      data: {
        email: this.userEmail,
      },
    });
  }
  changePassword() {
    this.dialog.open(ChangePasswordPopupComponent, {
      width: '50%',
      enterAnimationDuration: '350ms',
      exitAnimationDuration: '350ms',
      data: {
        email: this.userEmail,
      },
    });
  }
}
