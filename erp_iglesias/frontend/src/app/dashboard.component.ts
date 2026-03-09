import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DashboardService, DashboardData } from '../services/dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatSnackBarModule,
    MatIconModule
  ],
  template: ` ... `,
  styles: [` ... `]
})
export class DashboardComponent implements OnInit {
  data: DashboardData | null = null;

  constructor(
    private dashboardService: DashboardService,
    private snack: MatSnackBar
  ) {}

  ngOnInit() {
    this.dashboardService.get().subscribe({
      next: (res) => (this.data = res),
      error: (err) => {
        const message = err?.error?.message || 'No se pudo cargar el dashboard';
        this.snack.open(message, 'Cerrar', { duration: 3000 });
      }
    });
  }
}