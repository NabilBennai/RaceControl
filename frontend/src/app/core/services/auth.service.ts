import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable, tap} from 'rxjs';

import {environment} from '../../../environments/environment';
import {AuthTokens, LoginRequest, MeResponse, RegisterRequest} from '../models/auth.models';
import {TokenStorageService} from './token-storage.service';

@Injectable({providedIn: 'root'})
export class AuthService {
  private readonly authApi = `${environment.apiBaseUrl}/auth`;

  constructor(
    private readonly http: HttpClient,
    private readonly tokenStorage: TokenStorageService
  ) {
  }

  login(payload: LoginRequest): Observable<AuthTokens> {
    return this.http.post<AuthTokens>(`${this.authApi}/login`, payload).pipe(
      tap((tokens) => this.tokenStorage.setTokens(tokens))
    );
  }

  register(payload: RegisterRequest): Observable<AuthTokens> {
    return this.http.post<AuthTokens>(`${this.authApi}/register`, payload).pipe(
      tap((tokens) => this.tokenStorage.setTokens(tokens))
    );
  }

  refresh(): Observable<AuthTokens> {
    return this.http
      .post<AuthTokens>(`${this.authApi}/refresh`, {
        refreshToken: this.tokenStorage.getRefreshToken()
      })
      .pipe(tap((tokens) => this.tokenStorage.setTokens(tokens)));
  }

  logout(): Observable<void> {
    return this.http
      .post<void>(`${this.authApi}/logout`, {
        refreshToken: this.tokenStorage.getRefreshToken()
      })
      .pipe(tap(() => this.tokenStorage.clear()));
  }

  me(): Observable<MeResponse> {
    return this.http.get<MeResponse>(`${environment.apiBaseUrl.replace(/\/$/, '')}/me`);
  }

  isAuthenticated(): boolean {
    return Boolean(this.tokenStorage.getAccessToken());
  }

  hasAdminRole(): boolean {
    return this.tokenStorage.getAccessToken()?.includes('admin') ?? false;
  }
}
