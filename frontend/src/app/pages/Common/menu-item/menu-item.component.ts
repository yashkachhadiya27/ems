import { Component, input, OnInit, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { sidenav } from '../../../core/Interfaces/SidenavModel';
import { animate, style, transition, trigger } from '@angular/animations';

@Component({
  selector: 'app-menu-item',
  standalone: true,
  animations: [
    trigger('expandContractMenu', [
      transition(':enter', [
        style({ opacity: 0, height: '0px' }),
        animate('500ms ease-in-out', style({ opacity: 1, height: '*' })),
      ]),
      transition(':leave', [
        animate('500ms ease-in-out', style({ opacity: 0, height: '0px' })),
      ]),
    ]),
  ],
  imports: [MatTooltipModule, RouterModule, MatIconModule, MatListModule],
  templateUrl: './menu-item.component.html',
  styleUrl: './menu-item.component.css',
})
export class MenuItemComponent {
  item = input.required<sidenav>();
  isCollapsed = input.required<boolean>();
  nestedMenuOpen = signal<boolean>(false);
  toggleNested() {
    if (!this.item().subLinks) {
      return;
    }
    this.nestedMenuOpen.set(!this.nestedMenuOpen());
  }
}
