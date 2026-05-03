import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {environment} from '../../../environments/environment';
import {DriverStandingResponse, StandingsResponse, TeamStandingResponse} from '../models/standing.models';

@Injectable({providedIn: 'root'})
export class StandingService {
  private readonly apiBase = `${environment.apiBaseUrl}`;

  constructor(private readonly http: HttpClient) {
  }

  drivers(seasonId: number): Observable<DriverStandingResponse[]> {
    return this.http.get<DriverStandingResponse[]>(`${this.apiBase}/seasons/${seasonId}/standings/drivers`);
  }

  teams(seasonId: number): Observable<TeamStandingResponse[]> {
    return this.http.get<TeamStandingResponse[]>(`${this.apiBase}/seasons/${seasonId}/standings/teams`);
  }

  recalculate(seasonId: number): Observable<StandingsResponse> {
    return this.http.post<StandingsResponse>(`${this.apiBase}/seasons/${seasonId}/standings/recalculate`, {});
  }
}
