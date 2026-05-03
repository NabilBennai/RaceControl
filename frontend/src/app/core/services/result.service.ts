import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {environment} from '../../../environments/environment';
import {RaceResultRequest, RaceResultResponse} from '../models/result.models';

@Injectable({providedIn: 'root'})
export class ResultService {
  private readonly apiBase = `${environment.apiBaseUrl}`;

  constructor(private readonly http: HttpClient) {
  }

  create(raceId: number, payload: RaceResultRequest): Observable<RaceResultResponse> {
    return this.http.post<RaceResultResponse>(`${this.apiBase}/races/${raceId}/results`, payload);
  }

  get(raceId: number): Observable<RaceResultResponse> {
    return this.http.get<RaceResultResponse>(`${this.apiBase}/races/${raceId}/results`);
  }

  update(raceId: number, payload: RaceResultRequest): Observable<RaceResultResponse> {
    return this.http.put<RaceResultResponse>(`${this.apiBase}/races/${raceId}/results`, payload);
  }

  delete(raceId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBase}/races/${raceId}/results`);
  }
}
