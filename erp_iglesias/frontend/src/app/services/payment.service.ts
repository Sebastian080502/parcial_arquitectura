import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Payment } from '../models/payment.model';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private apiUrl = `${environment.apiUrl}/payments`;

  constructor(private http: HttpClient) {}

  listPayments(status?: string): Observable<Payment[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<Payment[]>(this.apiUrl, { params });
  }

  confirmPayment(id: number): Observable<Payment> {
    return this.http.post<Payment>(`${this.apiUrl}/${id}/confirm`, {});
  }

  failPayment(id: number): Observable<Payment> {
    return this.http.post<Payment>(`${this.apiUrl}/${id}/fail`, {});
  }

  retryPayment(id: number): Observable<Payment> {
    return this.http.post<Payment>(`${this.apiUrl}/${id}/retry`, {});
  }
}