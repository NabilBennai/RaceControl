export type SeasonStatus = 'DRAFT' | 'ACTIVE' | 'FINISHED' | 'ARCHIVED';

export interface SeasonRequest {
  name: string;
  startDate: string;
  endDate: string;
}

export interface SeasonResponse {
  id: number;
  leagueId: number;
  name: string;
  startDate: string;
  endDate: string;
  status: SeasonStatus;
}
