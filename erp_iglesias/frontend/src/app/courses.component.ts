import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { CoursesService, Course, CoursePayload } from '../services/courses.service';

@Component({
  selector: 'app-courses',
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
  template: ` ... `, // Tu template actual (sin cambios)
  styles: [` ... `] // Tus estilos actuales
})
export class CoursesComponent implements OnInit {
  courses: Course[] = [];
  columns = ['name', 'price', 'active'];
  form = this.fb.group({
    name: ['', Validators.required],
    description: [''],
    price: [0, [Validators.required, Validators.min(0)]]
  });

  constructor(
    private coursesService: CoursesService,
    private fb: FormBuilder,
    private snack: MatSnackBar
  ) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.coursesService.list().subscribe({
      next: (res) => (this.courses = res),
      error: (err) => {
        const message = err?.error?.message || 'No se pudo cargar cursos';
        this.snack.open(message, 'Cerrar', { duration: 3000 });
      }
    });
  }

  submit() {
    if (this.form.invalid) return;
    const raw = this.form.getRawValue();
    const payload: CoursePayload = {
      name: raw.name!,
      description: raw.description || '',
      price: raw.price!
    };
    this.coursesService.create(payload).subscribe({
      next: () => {
        this.snack.open('Curso creado', 'Cerrar', { duration: 3000 });
        this.form.reset({ name: '', description: '', price: 0 });
        this.load();
      },
      error: (err) => {
        const message = err?.error?.message || 'No se pudo crear curso';
        this.snack.open(message, 'Cerrar', { duration: 3000 });
      }
    });
  }
}