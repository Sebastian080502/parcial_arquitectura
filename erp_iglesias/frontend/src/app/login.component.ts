import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '../services/auth.service'; // Ya existe

@Component({
  selector: 'app-login',
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
export class LoginComponent {
  loading = false;
  form = this.fb.group({
    email: ['admin@parroquia.com', [Validators.required, Validators.email]],
    password: ['Admin123!', [Validators.required]]
  });

  constructor(
    private fb: FormBuilder,
    private authService: AuthService, // Nota: usamos AuthService, no ApiService
    private router: Router,
    private snack: MatSnackBar
  ) {}

  submit() {
    if (this.form.invalid || this.loading) return;
    this.loading = true;
    const { email, password } = this.form.getRawValue();
    // Aquí necesitas un método de login en AuthService. Si no existe, créalo.
    // Por ahora asumimos que AuthService tiene un método login.
    this.authService.login(email!, password!).subscribe({
      next: (res) => {
        this.authService.setAuth(res); // Guarda el estado (token, email, role)
        this.router.navigateByUrl('/dashboard');
      },
      error: (err) => {
        const message = err?.error?.message || 'Error al iniciar sesión';
        this.snack.open(message, 'Cerrar', { duration: 3000 });
        this.loading = false;
      }
    });
  }
}