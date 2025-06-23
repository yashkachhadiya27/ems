import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { AngularEditorModule } from '@kolkov/angular-editor';
import { Notice } from '../../../core/Interfaces/NoticeModel';
import { AngularEditorConfig } from '@kolkov/angular-editor';
import { NoticeService } from '../../../core/Services/notice.service';
import { MatIconModule } from '@angular/material/icon';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-add-notice-popup',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
    AngularEditorModule,
    FormsModule,
    MatIconModule,
  ],
  templateUrl: './add-notice-popup.component.html',
  styleUrl: './add-notice-popup.component.css',
})
export class AddNoticePopupComponent {
  private noticeService = inject(NoticeService);
  toast: ToastrService = inject(ToastrService);
  today!: string;
  currentDate!: Date;
  constructor() {
    this.currentDate = new Date();
    this.today = this.currentDate.toISOString().split('T')[0];
  }
  notice: Notice = { title: '', content: '', deadline: '' };
  editorConfig: AngularEditorConfig = {
    editable: true,
    spellcheck: true,
    height: '8rem',
    minHeight: '5rem',
    enableToolbar: true,
    placeholder: 'Enter notice content here...',
    translate: 'no',
    toolbarHiddenButtons: [['undo', 'redo'], ['insertVideo']],
  };
  compareDate() {
    const selectedDate = new Date(this.notice.deadline);
    return selectedDate < this.currentDate;
  }
  addNotice() {
    this.noticeService.addNotice(this.notice).subscribe({
      next: (res) => {
        this.toast.success('Notice added Successfully', 'Success', {
          timeOut: 3000,
          closeButton: true,
        });
      },
    });
  }
}
