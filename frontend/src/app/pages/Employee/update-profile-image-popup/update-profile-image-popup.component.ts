import { NgClass } from '@angular/common';
import { Component, inject } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../../core/Services/auth.service';
import { RoleService } from '../../../core/Services/role.service';

@Component({
  selector: 'app-update-profile-image-popup',
  standalone: true,
  imports: [NgClass, ReactiveFormsModule, MatButtonModule, MatDialogModule],
  templateUrl: './update-profile-image-popup.component.html',
  styleUrl: './update-profile-image-popup.component.css',
})
export class UpdateProfileImagePopupComponent {
  private data = inject(MAT_DIALOG_DATA);
  private authService = inject(AuthService);
  private roleService = inject(RoleService);
  private toast = inject(ToastrService);
  updateImageForm = new FormGroup({
    image: new FormControl('', Validators.required),
  });
  get image(): FormControl {
    return this.updateImageForm.get('image') as FormControl;
  }

  filetoUpload!: File;
  onChangeFileField(event: any) {
    this.filetoUpload = event.target.files[0];
  }

  submitUpdateImageForm() {
    const formData = new FormData();
    formData.append('image', this.filetoUpload);
    this.authService.updateProfileImage(this.data.email, formData).subscribe({
      next: () => {
        this.toast.success('Image Updated Successfully', 'Success', {
          timeOut: 3000,
          closeButton: true,
        });
        this.roleService.isLoggedin.next(true);
      },
      error: () => {
        this.toast.error('Something went wrong!', 'Error', {
          timeOut: 3000,
          closeButton: true,
        });
      },
    });
  }
}
