import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Person, PersonPayload } from '../models/person.model';

@Injectable({ providedIn: 'root' })
export class PersonService {
  private apiUrl = `${environment.apiUrl}/people`;

  constructor(private http: HttpClient) {}

  listPeople(): Observable<Person[]> {
    return this.http.get<Person[]>(this.apiUrl);
  }

  createPerson(payload: PersonPayload): Observable<Person> {
    return this.http.post<Person>(this.apiUrl, payload);
  }
}