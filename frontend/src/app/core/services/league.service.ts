import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {environment} from '../../../environments/environment';
import {
  JoinByCodeRequest,
  LeagueJoinResponse,
  LeagueMemberResponse,
  LeagueRequest,
  LeagueResponse
} from '../models/league.models';

@Injectable({providedIn: 'root'})
export class LeagueService {
  private readonly leaguesApi = `${environment.apiBaseUrl}/leagues`;

  constructor(private readonly http: HttpClient) {
  }

  create(payload: LeagueRequest): Observable<LeagueResponse> {
    return this.http.post<LeagueResponse>(this.leaguesApi, payload);
  }

  list(): Observable<LeagueResponse[]> {
    return this.http.get<LeagueResponse[]>(this.leaguesApi);
  }

  getById(leagueId: number): Observable<LeagueResponse> {
    return this.http.get<LeagueResponse>(`${this.leaguesApi}/${leagueId}`);
  }

  update(leagueId: number, payload: LeagueRequest): Observable<LeagueResponse> {
    return this.http.put<LeagueResponse>(`${this.leaguesApi}/${leagueId}`, payload);
  }

  delete(leagueId: number): Observable<void> {
    return this.http.delete<void>(`${this.leaguesApi}/${leagueId}`);
  }

  join(leagueId: number): Observable<LeagueJoinResponse> {
    return this.http.post<LeagueJoinResponse>(`${this.leaguesApi}/${leagueId}/join`, {});
  }

  joinByCode(payload: JoinByCodeRequest): Observable<LeagueJoinResponse> {
    return this.http.post<LeagueJoinResponse>(`${this.leaguesApi}/join-by-code`, payload);
  }

  members(leagueId: number): Observable<LeagueMemberResponse[]> {
    return this.http.get<LeagueMemberResponse[]>(`${this.leaguesApi}/${leagueId}/members`);
  }

  approveMember(leagueId: number, memberId: number): Observable<LeagueMemberResponse> {
    return this.http.patch<LeagueMemberResponse>(`${this.leaguesApi}/${leagueId}/members/${memberId}/approve`, {});
  }

  rejectMember(leagueId: number, memberId: number): Observable<LeagueMemberResponse> {
    return this.http.patch<LeagueMemberResponse>(`${this.leaguesApi}/${leagueId}/members/${memberId}/reject`, {});
  }
}
