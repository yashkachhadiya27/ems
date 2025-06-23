import { Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { TimeConvertPipe } from '../../../core/Pipes/time-convert.pipe';
import { DatePipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MinuteHourPipe } from '../../../core/Pipes/minute-hour.pipe';

@Component({
  selector: 'app-logs-popup',
  standalone: true,
  imports: [
    TimeConvertPipe,
    DatePipe,
    MatDialogModule,
    MatButtonModule,
    MinuteHourPipe,
  ],
  templateUrl: './logs-popup.component.html',
  styleUrl: './logs-popup.component.css',
})
export class LogsPopupComponent implements OnInit {
  public data = inject(MAT_DIALOG_DATA);
  ngOnInit(): void {
    console.log(this.data);
  }
}
