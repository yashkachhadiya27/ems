import { TitleCasePipe } from '@angular/common';
import {
  Component,
  computed,
  inject,
  input,
  OnInit,
  signal,
} from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { admin, common, employee } from '../../../core/Constants/sidenavItems';
import { sidenav } from '../../../core/Interfaces/SidenavModel';
import { AuthService } from '../../../core/Services/auth.service';
import { EmployeeService } from '../../../core/Services/employee.service';
import { RoleService } from '../../../core/Services/role.service';
import { MenuItemComponent } from '../menu-item/menu-item.component';
import { MatDialog } from '@angular/material/dialog';
import { UserDetailsPopupComponent } from '../../Admin/user-details-popup/user-details-popup.component';

@Component({
  selector: 'app-sidenav',
  standalone: true,
  imports: [
    MatSidenavModule,
    MatListModule,
    RouterModule,
    MatTooltipModule,
    MatIconModule,
    MenuItemComponent,
    TitleCasePipe,
  ],
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.css',
})
export class SidenavComponent implements OnInit {
  private roleService = inject(RoleService);
  private authService = inject(AuthService);
  private dialog = inject(MatDialog);
  private employeeService = inject(EmployeeService);
  isCollapsed = input.required<boolean>();
  sidenavItem: sidenav[] = common;
  isLoggedin = signal<boolean>(false);
  role = signal<string>('');
  userEmail = signal<string>('');
  userInfo = signal<{ image: string; name: string; department: string }>({
    image: '',
    name: '',
    department: '',
  });
  sideNavWidth = computed(() => (this.isCollapsed() ? '5rem' : '13rem'));
  profilePicSize = computed(() => (this.isCollapsed() ? '48' : '100'));
  ngOnInit(): void {
    this.roleService.isLoggedin.subscribe((isLogin) => {
      this.isLoggedin.set(isLogin);
      if (localStorage.getItem('email') != null) {
        this.userEmail.set(localStorage.getItem('email') as string);
        this.authService
          .getUserNameDepartment(localStorage.getItem('email'))
          .subscribe({
            next: (res: {
              image: string;
              name: string;
              department: string;
            }) => {
              this.employeeService.getProfileImage().subscribe({
                next: (blob) => {
                  const objectURL = URL.createObjectURL(blob);
                  res.image = objectURL;
                  this.userInfo.set(res);
                },
              });
            },
          });
      }

      this.roleService.roleSubject.subscribe((res) => {
        if (res == 'USER') {
          this.sidenavItem = employee;
          this.role.set('USER');
        } else if (res == 'ADMIN') {
          this.sidenavItem = admin;
          this.role.set('ADMIN');
        } else {
          this.sidenavItem = common;
          this.role.set('');
        }
      });
    });
  }
  viewDetail() {
    this.dialog.open(UserDetailsPopupComponent, {
      width: '50%',
      enterAnimationDuration: '350ms',
      exitAnimationDuration: '350ms',
      data: {
        email: this.userEmail(),
        image: this.userInfo().image,
      },
    });
  }
}
