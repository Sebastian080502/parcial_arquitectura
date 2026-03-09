import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { UsersService } from '../services/users.service'; // Asegúrate de tener este servicio

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
    MatDividerModule
  ],
  template: ` ... `,
  styles: [` ... `]
})
export class UsersComponent {
  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  constructor(
    private usersService: UsersService,
    private fb: FormBuilder,
    private snack: MatSnackBar
  ) {}

  submit() {
    if (this.form.invalid) return;
    const { email, password } = this.form.getRawValue();
    this.usersService.createClient({ email: email!, password: password! }).subscribe({
      next: () => {
        this.snack.open('Usuario creado', 'Cerrar', { duration: 3000 });
        this.form.reset();
      },
      error: (err) => {
        const message = err?.error?.message || 'No se pudo crear el usuario';
        this.snack.open(message, 'Cerrar', { duration: 3000 });
      }
    });
  }
}