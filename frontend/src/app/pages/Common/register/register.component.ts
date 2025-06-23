import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
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
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { address } from '../../../core/Interfaces/AuthModel';
import { AuthService } from '../../../core/Services/auth.service';
import { DepartmentService } from '../../../core/Services/department.service';
import { VerifyOtpPopupComponent } from '../verify-otp-popup/verify-otp-popup.component';

@Component({
  selector: 'app-register',
  standalone: true,

  imports: [
    ReactiveFormsModule,
    CommonModule,
    MatDialogModule,
    MatCardModule,
    MatTooltipModule,
    MatIconModule,
    MatButtonModule,
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent implements OnInit {
  private dialog = inject(MatDialog);
  private authService = inject(AuthService);
  private toast = inject(ToastrService);
  private departmentService = inject(DepartmentService);
  private router = inject(Router);
  private isSubmitted = signal<boolean>(false);
  // isSubmitBtnVisible = signal<boolean>(false);
  deptArray: { id: number; departmentName: string }[] = [];
  countryArray: string[] = ['India', 'USA', 'Canada', 'Germany'];

  ngOnInit(): void {
    this.departmentService.getDepartments().subscribe({
      next: (res) => {
        this.deptArray = res as { id: number; departmentName: string }[];
      },
    });
  }
  signupForm = new FormGroup(
    {
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
      password: new FormControl('', [
        Validators.required,
        Validators.pattern(
          /^(?=\D*\d)(?=[^a-z]*[a-z])(?=.*[$@$!%*?&])(?=[^A-Z]*[A-Z]).{8,}$/
        ),
      ]),
      cPassword: new FormControl('', [Validators.required]),
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
    },
    { validators: this.passwordMatchValidator }
  );
  AddExperience() {
    const frmgroup = new FormGroup(
      {
        company: new FormControl(null, [Validators.required]),
        position: new FormControl(null, [Validators.required]),
        totalExp: new FormControl({ value: null, disabled: true }),
        startDate: new FormControl(null, {
          validators: [Validators.required],
          updateOn: 'blur',
        }),
        endDate: new FormControl(null, {
          validators: [Validators.required],
          updateOn: 'change',
        }),
      },
      { validators: this.totalExperienceValidator }
    );

    (<FormArray>this.signupForm.get('experience')).push(frmgroup);
  }
  DeleteSkill(index: number) {
    const controls = <FormArray>this.signupForm.get('skills');
    controls.removeAt(index);
  }
  AddSkills() {
    (<FormArray>this.signupForm.get('skills')).push(
      new FormControl(null, Validators.required)
    );
  }
  dateInPastValidator(control: AbstractControl) {
    const selectedDate = new Date(control?.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return selectedDate < today ? null : { notInPast: true };
  }
  passwordMatchValidator(control: AbstractControl) {
    const passwordControl = control.get('password');
    const confirmPasswordControl = control.get('cPassword');

    if (
      (passwordControl?.touched || passwordControl?.dirty) &&
      (confirmPasswordControl?.touched || confirmPasswordControl?.dirty)
    ) {
      const password = passwordControl?.value;
      const confirmPassword = confirmPasswordControl?.value;

      if (!password || !confirmPassword) {
        return null;
      }

      return password === confirmPassword ? null : { mismatch: true };
    }

    return null;
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
    return this.signupForm.get('fname') as FormControl;
  }
  get mname(): FormControl {
    return this.signupForm.get('mname') as FormControl;
  }
  get lname(): FormControl {
    return this.signupForm.get('lname') as FormControl;
  }
  get email(): FormControl {
    return this.signupForm.get('email') as FormControl;
  }
  get phone(): FormControl {
    return this.signupForm.get('phone') as FormControl;
  }
  get gender(): FormControl {
    return this.signupForm.get('gender') as FormControl;
  }
  get department(): FormControl {
    return this.signupForm.get('department') as FormControl;
  }
  get dob(): FormControl {
    return this.signupForm.get('dateOfBirth') as FormControl;
  }
  get doj(): FormControl {
    return this.signupForm.get('dateOfJoining') as FormControl;
  }
  get image(): FormControl {
    return this.signupForm.get('image') as FormControl;
  }
  get password(): FormControl {
    return this.signupForm.get('password') as FormControl;
  }
  get cPassword(): FormControl {
    return this.signupForm.get('cPassword') as FormControl;
  }
  get street(): FormControl {
    return this.signupForm.get('address.street') as FormControl;
  }
  get postal(): FormControl {
    return this.signupForm.get('address.postal') as FormControl;
  }
  get district(): FormControl {
    return this.signupForm.get('address.district') as FormControl;
  }
  get state(): FormControl {
    return this.signupForm.get('address.state') as FormControl;
  }
  get city(): FormControl {
    return this.signupForm.get('address.city') as FormControl;
  }
  get country(): FormControl {
    return this.signupForm.get('address.country') as FormControl;
  }
  get skills(): FormArray {
    return this.signupForm.get('skills') as FormArray;
  }
  get experience(): FormArray {
    return this.signupForm.get('experience') as FormArray;
  }

  filetoUpload!: File;
  onChangeFileField(event: any) {
    this.filetoUpload = event.target.files[0];
  }

  DeleteExperience(index: number) {
    const frmArray = <FormArray>this.signupForm.get('experience');
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
    formData.append('password', this.password.value);
    formData.append('skills', this.skills.value);
    Object.keys(addressObj).forEach((key) => {
      const value = addressObj[key as keyof address];
      formData.append(key, String(value));
    });
    formData.append('experience', JSON.stringify(this.experience.value));
    formData.append('image', this.filetoUpload);
    return formData;
  }
  verifyEmail() {
    this.isSubmitted.set(true);
    const formData: FormData = this.formData();
    this.authService.isUserExist(this.email.value).subscribe({
      next: (resp) => {
        if (resp.code == 404) {
          this.authService.sendOtp(this.email.value).subscribe({
            next: (resp) => {
              this.toast.success('OTP Sent to ' + this.email.value, 'Success', {
                timeOut: 3000,
                closeButton: true,
              });
              const _verifyOtpPopup = this.dialog.open(
                VerifyOtpPopupComponent,
                {
                  width: '50%',
                  enterAnimationDuration: '350ms',
                  exitAnimationDuration: '350ms',
                  data: { formData, type: 'submit' },
                }
              );
              _verifyOtpPopup.afterClosed().subscribe((status) => {
                // this.isSubmitBtnVisible.set(status);
                if (status === 'success') {
                  this.signupForm.reset();
                  this.router.navigate(['/login']);
                }
              });
            },
            error: (error) => {
              this.toast.error('Error occurred While sending Email', 'Error', {
                timeOut: 3000,
                closeButton: true,
              });
            },
          });
        } else {
          this.toast.error('Already Exist', 'Error', {
            timeOut: 3000,
            closeButton: true,
          });
        }
      },
      error: (error) => {
        this.toast.error('Some intenal error occured', 'Error', {
          timeOut: 3000,
          closeButton: true,
        });
      },
    });
  }
  canExit() {
    if (this.signupForm.dirty && !this.isSubmitted()) {
      return confirm('You have unsaved Changes.Do you want to navigate away?');
    } else {
      return true;
    }
  }
}
