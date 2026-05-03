import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {environment} from '../../../environments/environment';
import {PointSystemRequest, PointSystemResponse} from '../models/point-system.models';

@Injectable({providedIn: 'root'})
export class PointSystemService {
  private readonly apiBase = `${environment.apiBaseUrl}`;

  constructor(private readonly http: HttpClient) {
  }

  create(seasonId: number, payload: PointSystemRequest): Observable<PointSystemResponse> {
    return this.http.post<PointSystemResponse>(`${this.apiBase}/seasons/${seasonId}/point-system`, payload);
  }

  getBySeason(seasonId: number): Observable<PointSystemResponse> {
    return this.http.get<PointSystemResponse>(`${this.apiBase}/seasons/${seasonId}/point-system`);
  }

  update(pointSystemId: number, payload: PointSystemRequest): Observable<PointSystemResponse> {
    return this.http.put<PointSystemResponse>(`${this.apiBase}/point-systems/${pointSystemId}`, payload);
  }
}
