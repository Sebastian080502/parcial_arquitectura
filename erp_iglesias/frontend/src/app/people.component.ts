import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { PeopleService, Person, PersonPayload } from '../services/people.service';

@Component({
  selector: 'app-people',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatTableModule,
    MatSnackBarModule
  ],
  template: ` ... `,
  styles: [` ... `]
})
export class PeopleComponent implements OnInit {
  people: Person[] = [];
  columns = ['name', 'document', 'contact'];
  form = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    document: [''],
    phone: [''],
    email: ['']
  });

  constructor(
    private peopleService: PeopleService,
    private fb: FormBuilder,
    private snack: MatSnackBar
  ) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.peopleService.list().subscribe({
      next: (res) => (this.people = res),
      error: (err) => {
        const message = err?.error?.message || 'No se pudo cargar personas';
        this.snack.open(message, 'Cerrar', { duration: 3000 });
      }
    });
  }

  submit() {
    if (this.form.invalid) return;
    const raw = this.form.getRawValue();
    const payload: PersonPayload = {
      firstName: raw.firstName!,
      lastName: raw.lastName!,
      document: raw.document || '',
      phone: raw.phone || '',
      email: raw.email || ''
    };
    this.peopleService.create(payload).subscribe({
      next: () => {
        this.snack.open('Persona registrada', 'Cerrar', { duration: 3000 });
        this.form.reset();
        this.load();
      },
      error: (err) => {
        const message = err?.error?.message || 'No se pudo registrar persona';
        this.snack.open(message, 'Cerrar', { duration: 3000 });
      }
    });
  }
}