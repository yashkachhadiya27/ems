import { DatePipe, TitleCasePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { DayFromDatesPipe } from '../../../core/Pipes/day-from-dates.pipe';

@Component({
  selector: 'app-record-popup',
  standalone: true,
  imports: [
    MatDialogModule,
    DatePipe,
    DayFromDatesPipe,
    MatButtonModule,
    TitleCasePipe,
  ],
  templateUrl: './record-popup.component.html',
  styleUrl: './record-popup.component.css',
})
export class RecordPopupComponent {
  public data = inject(MAT_DIALOG_DATA);
}
