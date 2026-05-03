import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {environment} from '../../../environments/environment';
import {SeasonRequest, SeasonResponse} from '../models/season.models';

@Injectable({providedIn: 'root'})
export class SeasonService {
  private readonly apiBase = `${environment.apiBaseUrl}`;

  constructor(private readonly http: HttpClient) {
  }

  create(leagueId: number, payload: SeasonRequest): Observable<SeasonResponse> {
    return this.http.post<SeasonResponse>(`${this.apiBase}/leagues/${leagueId}/seasons`, payload);
  }

  listByLeague(leagueId: number): Observable<SeasonResponse[]> {
    return this.http.get<SeasonResponse[]>(`${this.apiBase}/leagues/${leagueId}/seasons`);
  }

  getById(seasonId: number): Observable<SeasonResponse> {
    return this.http.get<SeasonResponse>(`${this.apiBase}/seasons/${seasonId}`);
  }

  update(seasonId: number, payload: SeasonRequest): Observable<SeasonResponse> {
    return this.http.put<SeasonResponse>(`${this.apiBase}/seasons/${seasonId}`, payload);
  }

  activate(seasonId: number): Observable<SeasonResponse> {
    return this.http.patch<SeasonResponse>(`${this.apiBase}/seasons/${seasonId}/activate`, {});
  }

  finish(seasonId: number): Observable<SeasonResponse> {
    return this.http.patch<SeasonResponse>(`${this.apiBase}/seasons/${seasonId}/finish`, {});
  }
}
