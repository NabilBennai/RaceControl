export type RegistrationStatus = 'CONFIRMED' | 'WAITLISTED' | 'CANCELLED' | 'REJECTED';

export interface RegistrationRequest {
  car: string;
  number: string;
  teamId: number | null;
}

export interface RegistrationStatusRequest {
  status: RegistrationStatus;
}

export interface RegistrationResponse {
  id: number;
  raceId: number;
  userId: number;
  username: string;
  email: string;
  car: string;
  number: string;
  teamId: number | null;
  teamName: string | null;
  status: RegistrationStatus;
}
