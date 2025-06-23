import { HttpClient } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/Services/auth.service';
import { RoleService } from '../../../core/Services/role.service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
@Component({
  selector: 'app-callback',
  standalone: true,
  imports: [MatProgressSpinnerModule],
  templateUrl: './callback.component.html',
  styleUrl: './callback.component.css',
})
export class CallbackComponent {
  private authService = inject(AuthService);
  private roleService = inject(RoleService);
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private router = inject(Router);

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params['code'] !== undefined) {
        this.authService.getToken(params['code']).subscribe({
          next: (result) => {
            if (result === 'ADMIN') {
              this.roleService.isLoggedin.next(true);
              this.router.navigate(['/admin/dashboard']);
            } else if (result === 'USER') {
              this.roleService.isLoggedin.next(true);
              this.router.navigate(['/employee/dashboard']);
            } else if (result === 2) {
              this.router.navigate(['/register']);
            } else {
              this.router.navigate(['/login']);
            }
          },
        });
      }
    });
  }
}
