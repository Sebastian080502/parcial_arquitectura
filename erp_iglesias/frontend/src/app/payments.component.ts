import { Component, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { PaymentsService, Payment } from '../services/payments.service';

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatSnackBarModule
  ],
  template: ` ... `,
  styles: [` ... `]
})
export class PaymentsComponent implements OnInit {
  payments: Payment[] = [];
  columns = ['id', 'type', 'amount', 'status', 'attempts', 'actions'];

  constructor(
    private paymentsService: PaymentsService,
    private snack: MatSnackBar
  ) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.paymentsService.list().subscribe({
      next: (res) => (this.payments = res),
      error: (err) => {
        const message = err?.error?.message || 'No se pudo cargar pagos';
        this.snack.open(message, 'Cerrar', { duration: 3000 });
      }
    });
  }

  confirm(payment: Payment) {
    this.paymentsService.confirm(payment.id).subscribe({
      next: () => {
        this.snack.open('Pago confirmado', 'Cerrar', { duration: 3000 });
        this.load();
      },
      error: (err) => {
        const message = err?.error?.message || 'No se pudo confirmar';
        this.snack.open(message, 'Cerrar', { duration: 3000 });
      }
    });
  }

  fail(payment: Payment) {
    this.paymentsService.fail(payment.id).subscribe({
      next: () => {
        this.snack.open('Pago fallido', 'Cerrar', { duration: 3000 });
        this.load();
      },
      error: (err) => {
        const message = err?.error?.message || 'No se pudo fallar';
        this.snack.open(message, 'Cerrar', { duration: 3000 });
      }
    });
  }

  retry(payment: Payment) {
    this.paymentsService.retry(payment.id).subscribe({
      next: () => {
        this.snack.open('Pago reiniciado', 'Cerrar', { duration: 3000 });
        this.load();
      },
      error: (err) => {
        const message = err?.error?.message || 'No se pudo reintentar';
        this.snack.open(message, 'Cerrar', { duration: 3000 });
      }
    });
  }
}

