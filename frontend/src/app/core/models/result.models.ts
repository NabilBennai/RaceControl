export type ResultStatus = 'CLASSIFIED' | 'DNF' | 'DNS' | 'DSQ';

export interface RaceResultLineRequest {
  userId: number;
  position: number;
  totalTime: string | null;
  bestLap: string | null;
  polePosition: boolean;
  incidents: number;
  status: ResultStatus;
}

export interface RaceResultRequest {
  lines: RaceResultLineRequest[];
}

export interface RaceResultLineResponse {
  id: number;
  userId: number;
  username: string;
  email: string;
  position: number;
  totalTime: string | null;
  bestLap: string | null;
  polePosition: boolean;
  incidents: number;
  status: ResultStatus;
}

export interface RaceResultResponse {
  id: number;
  raceId: number;
  lines: RaceResultLineResponse[];
}
