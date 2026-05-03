import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {environment} from '../../../environments/environment';
import {RaceRequest, RaceResponse, RaceStatusRequest} from '../models/race.models';

@Injectable({providedIn: 'root'})
export class RaceService {
  private readonly apiBase = `${environment.apiBaseUrl}`;

  constructor(private readonly http: HttpClient) {
  }

  create(seasonId: number, payload: RaceRequest): Observable<RaceResponse> {
    return this.http.post<RaceResponse>(`${this.apiBase}/seasons/${seasonId}/races`, payload);
  }

  listBySeason(seasonId: number): Observable<RaceResponse[]> {
    return this.http.get<RaceResponse[]>(`${this.apiBase}/seasons/${seasonId}/races`);
  }

  getById(raceId: number): Observable<RaceResponse> {
    return this.http.get<RaceResponse>(`${this.apiBase}/races/${raceId}`);
  }

  update(raceId: number, payload: RaceRequest): Observable<RaceResponse> {
    return this.http.put<RaceResponse>(`${this.apiBase}/races/${raceId}`, payload);
  }

  delete(raceId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBase}/races/${raceId}`);
  }

  updateStatus(raceId: number, payload: RaceStatusRequest): Observable<RaceResponse> {
    return this.http.patch<RaceResponse>(`${this.apiBase}/races/${raceId}/status`, payload);
  }
}
