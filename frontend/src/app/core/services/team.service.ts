import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {environment} from '../../../environments/environment';
import {TeamMemberRequest, TeamRequest, TeamResponse} from '../models/team.models';

@Injectable({providedIn: 'root'})
export class TeamService {
  private readonly apiBase = `${environment.apiBaseUrl}`;

  constructor(private readonly http: HttpClient) {
  }

  create(seasonId: number, payload: TeamRequest): Observable<TeamResponse> {
    return this.http.post<TeamResponse>(`${this.apiBase}/seasons/${seasonId}/teams`, payload);
  }

  listBySeason(seasonId: number): Observable<TeamResponse[]> {
    return this.http.get<TeamResponse[]>(`${this.apiBase}/seasons/${seasonId}/teams`);
  }

  update(teamId: number, payload: TeamRequest): Observable<TeamResponse> {
    return this.http.put<TeamResponse>(`${this.apiBase}/teams/${teamId}`, payload);
  }

  delete(teamId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBase}/teams/${teamId}`);
  }

  addMember(teamId: number, payload: TeamMemberRequest): Observable<TeamResponse> {
    return this.http.post<TeamResponse>(`${this.apiBase}/teams/${teamId}/members`, payload);
  }

  removeMember(teamId: number, memberId: number): Observable<TeamResponse> {
    return this.http.delete<TeamResponse>(`${this.apiBase}/teams/${teamId}/members/${memberId}`);
  }
}
