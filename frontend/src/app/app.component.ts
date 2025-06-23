import { Component, inject, OnInit, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { SidenavComponent } from './pages/Common/sidenav/sidenav.component';
import { HeaderComponent } from './pages/Common/header/header.component';
import { RoleService } from './core/Services/role.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SidenavComponent, HeaderComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  private roleService = inject(RoleService);
  private router = inject(Router);

  title = 'EMS-Frontend';
  isSidebarCollapsed = signal(false);
  role: string | null | undefined;
  isLoggedin: boolean | null | undefined;
  isLoginRoute: any;
  isCollapsed(value: boolean) {
    this.isSidebarCollapsed.set(value);
  }
  ngOnInit(): void {
    this.roleService.roleSubject.subscribe({
      next: (res) => {
        this.role = res;
      },
    });
    this.roleService.isLoggedin.subscribe({
      next: (res) => {
        this.isLoggedin = res;
      },
    });
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.isLoginRoute = event.url;
        if (
          this.role &&
          !this.isLoggedin &&
          (this.isLoginRoute === '/' || this.isLoginRoute === '/login')
        ) {
          console.log('Called');

          console.log(this.role);

          this.redirectBasedOnRole(this.role);
        }
      }
    });
  }
  private redirectBasedOnRole(role: string | null) {
    if (role === 'ADMIN') {
      this.router.navigate(['/admin/dashboard']);
    } else if (role === 'USER') {
      this.router.navigate(['/employee/dashboard']);
    } else {
      this.router.navigate(['/login']);
    }
  }
}
