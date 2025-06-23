import { CanActivateFn, Router } from '@angular/router';
import { RoleService } from '../Services/role.service';
import { inject } from '@angular/core';
import { map } from 'rxjs';
import { TokenService } from '../Services/token.service';
import { ToastrService } from 'ngx-toastr';

export const employeeGuard: CanActivateFn = (route, state) => {
  const roleService = inject(RoleService);
  const tokenService = inject(TokenService);
  const router = inject(Router);
  const toastr = inject(ToastrService);

  return roleService.roleSubject.pipe(
    map((role) => {
      if (role === 'USER') {
        return true;
      } else if (role === 'ADMIN') {
        router.navigate(['/admin/dashboard']);
        toastr.error('Access Denied!', 'Error', {
          timeOut: 3000,
          closeButton: true,
        });
        return false;
      } else if (role === null) {
        //to remove the loop occured due to roleService.roleSubject.next(null); and again goes into else and again set null and again call this due to subscriber called every time when new value emit
        router.navigate(['/login']);
        return false;
      } else {
        roleService.isLoggedin.next(false);
        tokenService.clearTokens();
        roleService.roleSubject.next(null);
        return false;
      }
    })
  );
};
