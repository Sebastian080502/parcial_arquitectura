import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Offering, OfferingPayload } from '../models/offering.model';

@Injectable({ providedIn: 'root' })
export class OfferingService {
  private apiUrl = `${environment.apiUrl}/offerings`;

  constructor(private http: HttpClient) {}

  listOfferings(): Observable<Offering[]> {
    return this.http.get<Offering[]>(this.apiUrl);
  }

  createOffering(payload: OfferingPayload): Observable<Offering> {
    return this.http.post<Offering>(this.apiUrl, payload);
  }
}