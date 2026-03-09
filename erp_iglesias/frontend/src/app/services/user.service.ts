import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface User {
  id: number;
  email: string;
  role: string;
}

export interface CreateUserPayload {
  email: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class UsersService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  createClient(payload: CreateUserPayload): Observable<User> {
    return this.http.post<User>(this.apiUrl, payload);
  }
}