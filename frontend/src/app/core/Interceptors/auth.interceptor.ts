import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { TokenService } from '../Services/token.service';
import { AuthService } from '../Services/auth.service';
import {
  BehaviorSubject,
  catchError,
  filter,
  switchMap,
  take,
  throwError,
} from 'rxjs';
import { Router } from '@angular/router';
import { RoleService } from '../Services/role.service';
import { ToastrService } from 'ngx-toastr';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenService = inject(TokenService);
  const authService = inject(AuthService);
  const roleService = inject(RoleService);
  const router = inject(Router);
  const toast = inject(ToastrService);
  let accessToken = tokenService.getAccessToken();
  const refreshTokenSubject: BehaviorSubject<string | null> =
    new BehaviorSubject<string | null>(null);
  let isRefreshing = false;
  let authReq = req;
  if (!req.url.includes('/public/refresh-token') && accessToken != null) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${accessToken}`,
      },
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (
        error.status === 498 &&
        tokenService.getRefreshToken() &&
        req.url != 'http://localhost:9090/public/refresh-token'
      ) {
        if (!isRefreshing) {
          isRefreshing = true;
          refreshTokenSubject.next(null);
          return authService.refreshToken().pipe(
            switchMap((newAccessToken: string) => {
              isRefreshing = false;
              tokenService.setAccessToken(newAccessToken);
              refreshTokenSubject.next(newAccessToken);

              const clonedReq = req.clone({
                setHeaders: {
                  Authorization: `Bearer ${newAccessToken}`,
                },
              });
              return next(clonedReq);
            }),
            catchError((refreshError) => {
              if (refreshError.status === 498) {
                isRefreshing = false;
                tokenService.clearTokens();
                roleService.roleSubject.next(null);
                roleService.isLoggedin.next(false);

                router.navigate(['/login']);
                return throwError(() => console.log('error handled'));
              }
              return throwError(() => console.log('error handled'));
            })
          );
        } else {
          return refreshTokenSubject.pipe(
            filter((token) => token !== null),
            take(1),
            switchMap((newAccessToken) => {
              const clonedReq = req.clone({
                setHeaders: {
                  Authorization: `Bearer ${newAccessToken!}`,
                },
              });
              return next(clonedReq);
            })
          );
        }
      } else if (error.status === 401) {
        toast.error('Unauthorized user', 'Error', {
          timeOut: 3000,
          closeButton: true,
        });
        tokenService.clearTokens();
        roleService.roleSubject.next(null);
        roleService.isLoggedin.next(false);

        router.navigate(['/login']);
        return throwError(() => {
          console.log('Error Occured');
        });
      } else {
        return throwError(() => {});
      }
    })
  );
};
