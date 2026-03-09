import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { NgIf } from '@angular/common';
import { ChurchService, Church } from '../services/church.service';

@Component({
  selector: 'app-church',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
    MatDividerModule,
    MatIconModule,
    NgIf
  ],
  template: ` ... `, // Tu template actual sin cambios
  styles: [` ... `] // Tus estilos actuales sin cambios
})
export class ChurchComponent implements OnInit {
  church: Church | null = null;
  form = this.fb.group({
    name: ['', Validators.required],
    address: ['']
  });

  constructor(
    private churchService: ChurchService,
    private fb: FormBuilder,
    private snack: MatSnackBar
  ) {}

  ngOnInit() {
    this.churchService.get().subscribe({
      next: (res) => (this.church = res),
      error: () => (this.church = null)
    });
  }

  submit() {
    if (this.form.invalid) return;
    const { name, address } = this.form.getRawValue();
    this.churchService.create({ name: name!, address: address || '' }).subscribe({
      next: (res) => {
        this.church = res;
        this.snack.open('Iglesia registrada', 'Cerrar', { duration: 3000 });
      },
      error: (err) => {
        const message = err?.error?.message || 'No se pudo registrar la iglesia';
        this.snack.open(message, 'Cerrar', { duration: 3000 });
      }
    });
  }
}

