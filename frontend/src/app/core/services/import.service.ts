import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {environment} from '../../../environments/environment';
import {ImportResponse} from '../models/import.models';

@Injectable({providedIn: 'root'})
export class ImportService {
  private readonly apiBase = `${environment.apiBaseUrl}`;

  constructor(private readonly http: HttpClient) {
  }

  upload(raceId: number, file: File): Observable<ImportResponse> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post<ImportResponse>(`${this.apiBase}/races/${raceId}/results/import`, form);
  }

  get(importId: number): Observable<ImportResponse> {
    return this.http.get<ImportResponse>(`${this.apiBase}/imports/${importId}`);
  }

  confirm(importId: number): Observable<ImportResponse> {
    return this.http.post<ImportResponse>(`${this.apiBase}/imports/${importId}/confirm`, {});
  }

  templateUrl(): string {
    return `${this.apiBase}/results/template.csv`;
  }
}
