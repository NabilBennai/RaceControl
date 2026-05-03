export type RaceStatus = 'SCHEDULED' | 'REGISTRATION_OPEN' | 'LIVE' | 'FINISHED' | 'CANCELLED';

export interface RaceRequest {
  track: string;
  raceDate: string;
  raceTime: string;
  format: string;
  laps: number | null;
  durationMinutes: number | null;
  weather: string;
  carCategory: string;
}

export interface RaceStatusRequest {
  status: RaceStatus;
}

export interface RaceResponse {
  id: number;
  seasonId: number;
  track: string;
  raceDate: string;
  raceTime: string;
  format: string;
  laps: number | null;
  durationMinutes: number | null;
  weather: string;
  carCategory: string;
  status: RaceStatus;
}
