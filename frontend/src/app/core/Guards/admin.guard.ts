import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { RoleService } from '../Services/role.service';
import { map } from 'rxjs';
import { TokenService } from '../Services/token.service';
import { ToastrService } from 'ngx-toastr';

export const adminGuard: CanActivateFn = (route, state) => {
  const roleService = inject(RoleService);
  const toastr = inject(ToastrService);
  const tokenService = inject(TokenService);
  const router = inject(Router);
  return roleService.roleSubject.pipe(
    map((role) => {
      if (role === 'ADMIN') {
        return true;
      } else if (role === 'USER') {
        router.navigate(['/employee/dashboard']);
        toastr.error('Access Denied!', 'Error', {
          timeOut: 3000,
          closeButton: true,
        });
        return false;
      } else if (role === null) {
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
