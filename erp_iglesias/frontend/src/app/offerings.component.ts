import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { OfferingsService, Offering, OfferingPayload } from '../services/offerings.service';
import { PeopleService, Person } from '../services/people.service';

@Component({
  selector: 'app-offerings',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatButtonModule,
    MatTableModule,
    MatSnackBarModule
  ],
  template: ` ... `,
  styles: [` ... `]
})
export class OfferingsComponent implements OnInit {
  people: Person[] = [];
  offerings: Offering[] = [];
  columns = ['person', 'concept', 'amount', 'status', 'payment'];
  form = this.fb.group({
    personId: [null, Validators.required],
    amount: [0, [Validators.required, Validators.min(1)]],
    concept: ['', Validators.required]
  });

  constructor(
    private offeringsService: OfferingsService,
    private peopleService: PeopleService,
    private fb: FormBuilder,
    private snack: MatSnackBar
  ) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.peopleService.list().subscribe({ next: (res) => (this.people = res) });
    this.offeringsService.list().subscribe({
      next: (res) => (this.offerings = res),
      error: (err) => {
        const message = err?.error?.message || 'No se pudo cargar ofrendas';
        this.snack.open(message, 'Cerrar', { duration: 3000 });
      }
    });
  }

  submit() {
    if (this.form.invalid) return;
    const raw = this.form.getRawValue();
    const payload: OfferingPayload = {
      personId: raw.personId!,
      amount: raw.amount!,
      concept: raw.concept!
    };
    this.offeringsService.create(payload).subscribe({
      next: () => {
        this.snack.open('Ofrenda creada', 'Cerrar', { duration: 3000 });
        this.form.reset();
        this.load();
      },
      error: (err) => {
        const message = err?.error?.message || 'No se pudo crear ofrenda';
        this.snack.open(message, 'Cerrar', { duration: 3000 });
      }
    });
  }
}