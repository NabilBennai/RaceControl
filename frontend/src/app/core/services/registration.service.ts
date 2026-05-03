import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {environment} from '../../../environments/environment';
import {
  RegistrationRequest,
  RegistrationResponse,
  RegistrationStatusRequest
} from '../models/registration.models';

@Injectable({providedIn: 'root'})
export class RegistrationService {
  private readonly apiBase = `${environment.apiBaseUrl}`;

  constructor(private readonly http: HttpClient) {
  }

  create(raceId: number, payload: RegistrationRequest): Observable<RegistrationResponse> {
    return this.http.post<RegistrationResponse>(`${this.apiBase}/races/${raceId}/registrations`, payload);
  }

  list(raceId: number): Observable<RegistrationResponse[]> {
    return this.http.get<RegistrationResponse[]>(`${this.apiBase}/races/${raceId}/registrations`);
  }

  deleteMine(raceId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBase}/races/${raceId}/registrations/me`);
  }

  updateStatus(raceId: number, registrationId: number, payload: RegistrationStatusRequest): Observable<RegistrationResponse> {
    return this.http.patch<RegistrationResponse>(`${this.apiBase}/races/${raceId}/registrations/${registrationId}/status`, payload);
  }
}
