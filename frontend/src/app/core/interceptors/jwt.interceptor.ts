import {HttpErrorResponse, HttpInterceptorFn} from '@angular/common/http';
import {inject} from '@angular/core';
import {catchError, throwError} from 'rxjs';

import {ToastService} from '../../shared/services/toast.service';
import {TokenStorageService} from '../services/token-storage.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenStorage = inject(TokenStorageService);
  const toastService = inject(ToastService);
  const accessToken = tokenStorage.getAccessToken();

  const request = accessToken
    ? req.clone({
      setHeaders: {
        Authorization: `Bearer ${accessToken}`
      }
    })
    : req;

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      const isAuthRoute = req.url.includes('/auth/login') || req.url.includes('/auth/register');
      if (!isAuthRoute) {
        if (error.status === 401) {
          toastService.error('Session expirée', 'Reconnectez-vous pour continuer.');
        } else if (error.status === 403) {
          toastService.error('Accès refusé', 'Vous n\'avez pas les droits nécessaires.');
        } else if (error.status >= 500) {
          toastService.error('Erreur serveur', 'Une erreur interne est survenue.');
        } else if (error.status > 0) {
          toastService.error('Requête échouée', error.error?.message ?? 'Veuillez réessayer.');
        }
      }

      return throwError(() => error);
    })
  );
};
