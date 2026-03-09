export interface Payment {
  id: number;
  type: string;
  status: string;
  amount: string;
  attempts: number;
  referenceId: number;
}