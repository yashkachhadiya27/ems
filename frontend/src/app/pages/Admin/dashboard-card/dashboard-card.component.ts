import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-dashboard-card',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './dashboard-card.component.html',
  styleUrl: './dashboard-card.component.css',
})
export class DashboardCardComponent {
  count = input.required<number>();
  title = input.required<string>();
  classArr = input.required<string[]>();
  link = input<string>();
}
