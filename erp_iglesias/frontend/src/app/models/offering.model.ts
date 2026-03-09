export interface Offering {
  id: number;
  personId: number;
  personName: string;
  concept: string;
  amount: string;
  status: string;
  paymentId?: number;
  paymentStatus?: string;
}

export interface OfferingPayload {
  personId: number;
  amount: number;
  concept: string;
}