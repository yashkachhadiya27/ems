import { DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { EmployeeService } from '../../../core/Services/employee.service';

@Component({
  selector: 'app-notification-popup',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule, DatePipe],
  templateUrl: './notification-popup.component.html',
  styleUrl: './notification-popup.component.css',
})
export class NotificationPopupComponent {
  private employeeService = inject(EmployeeService);
  public data = inject(MAT_DIALOG_DATA);
  private ref = inject(MatDialogRef<NotificationPopupComponent>);

  removeNotification() {
    this.employeeService
      .removeNotification(this.data.notification[0].registerId)
      .subscribe({
        next: (res) => {
          this.ref.close('Removed');
        },
      });
  }
}
