import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Enrollment, EnrollmentPayload } from '../models/enrollment.model';

@Injectable({ providedIn: 'root' })
export class EnrollmentService {
  private apiUrl = `${environment.apiUrl}/enrollments`;

  constructor(private http: HttpClient) {}

  listEnrollments(): Observable<Enrollment[]> {
    return this.http.get<Enrollment[]>(this.apiUrl);
  }

  createEnrollment(payload: EnrollmentPayload): Observable<Enrollment> {
    return this.http.post<Enrollment>(this.apiUrl, payload);
  }
}