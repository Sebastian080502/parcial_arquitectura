export interface Course {
  id: number;
  name: string;
  description?: string;
  price: number;
  active: boolean;
}

export interface CoursePayload {
  name: string;
  description?: string;
  price: number;
}