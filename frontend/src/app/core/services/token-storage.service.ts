import {Injectable} from '@angular/core';

import {AuthTokens} from '../models/auth.models';

@Injectable({providedIn: 'root'})
export class TokenStorageService {
  private static readonly ACCESS_TOKEN_KEY = 'auth.accessToken';
  private static readonly REFRESH_TOKEN_KEY = 'auth.refreshToken';

  setTokens(tokens: AuthTokens): void {
    localStorage.setItem(TokenStorageService.ACCESS_TOKEN_KEY, tokens.accessToken);
    localStorage.setItem(TokenStorageService.REFRESH_TOKEN_KEY, tokens.refreshToken);
  }

  getAccessToken(): string | null {
    return localStorage.getItem(TokenStorageService.ACCESS_TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(TokenStorageService.REFRESH_TOKEN_KEY);
  }

  clear(): void {
    localStorage.removeItem(TokenStorageService.ACCESS_TOKEN_KEY);
    localStorage.removeItem(TokenStorageService.REFRESH_TOKEN_KEY);
  }
}
