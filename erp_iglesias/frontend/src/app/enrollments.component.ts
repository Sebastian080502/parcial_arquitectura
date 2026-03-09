import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { EnrollmentsService, Enrollment, EnrollmentPayload } from '../services/enrollments.service';
import { PeopleService, Person } from '../services/people.service';
import { CoursesService, Course } from '../services/courses.service';

@Component({
  selector: 'app-enrollments',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule,
    MatTableModule,
    MatSnackBarModule
  ],
  template: ` ... `,
  styles: [` ... `]
})
export class EnrollmentsComponent implements OnInit {
  people: Person[] = [];
  courses: Course[] = [];
  enrollments: Enrollment[] = [];
  columns = ['person', 'course', 'status', 'payment'];
  form = this.fb.group({
    personId: [null, Validators.required],
    courseId: [null, Validators.required]
  });

  constructor(
    private enrollmentsService: EnrollmentsService,
    private peopleService: PeopleService,
    private coursesService: CoursesService,
    private fb: FormBuilder,
    private snack: MatSnackBar
  ) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.peopleService.list().subscribe({ next: (res) => (this.people = res) });
    this.coursesService.list().subscribe({ next: (res) => (this.courses = res) });
    this.enrollmentsService.list().subscribe({
      next: (res) => (this.enrollments = res),
      error: (err) => {
        const message = err?.error?.message || 'No se pudo cargar inscripciones';
        this.snack.open(message, 'Cerrar', { duration: 3000 });
      }
    });
  }

  submit() {
    if (this.form.invalid) return;
    const raw = this.form.getRawValue();
    const payload: EnrollmentPayload = {
      personId: raw.personId!,
      courseId: raw.courseId!
    };
    this.enrollmentsService.create(payload).subscribe({
      next: () => {
        this.snack.open('Inscripción creada', 'Cerrar', { duration: 3000 });
        this.form.reset();
        this.load();
      },
      error: (err) => {
        const message = err?.error?.message || 'No se pudo crear inscripción';
        this.snack.open(message, 'Cerrar', { duration: 3000 });
      }
    });
  }
}