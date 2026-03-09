export interface Person {
  id: number;
  firstName: string;
  lastName: string;
  document?: string;
  phone?: string;
  email?: string;
}

export interface PersonPayload {
  firstName: string;
  lastName: string;
  document?: string;
  phone?: string;
  email?: string;
}
