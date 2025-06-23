import { Component, inject, OnInit } from '@angular/core';
import { RoleService } from '../../../core/Services/role.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [],
  templateUrl: './not-found.component.html',
  styleUrl: './not-found.component.css',
})
export class NotFoundComponent {
  private roleService = inject(RoleService);
  private route = inject(Router);
  // ngOnInit(): void {
  //   this.roleService.isLoggedin.next(false);
  //   this.roleService.roleSubject.next(null);
  //   this.route.navigate(['/login']);
  // }
}
