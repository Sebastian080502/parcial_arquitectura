import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Church } from '../models/church.model';

@Injectable({ providedIn: 'root' })
export class ChurchService {
  private apiUrl = `${environment.apiUrl}/church`;

  constructor(private http: HttpClient) {}

  getChurch(): Observable<Church> {
    return this.http.get<Church>(this.apiUrl);
  }

  createChurch(name: string, address: string): Observable<Church> {
    return this.http.post<Church>(this.apiUrl, { name, address });
  }
}