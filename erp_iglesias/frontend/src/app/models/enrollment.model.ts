export interface Enrollment {
  id: number;
  personId: number;
  personName: string;
  courseId: number;
  courseName: string;
  status: string;
  paymentId?: number;
  paymentStatus?: string;
}

export interface EnrollmentPayload {
  personId: number;
  courseId: number;
}