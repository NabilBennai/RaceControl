export type SeasonStatus = 'DRAFT' | 'ACTIVE' | 'FINISHED' | 'ARCHIVED';
export type GamePlatform = 'F1' | 'ACC' | 'IRACING' | 'LMU' | 'RF2' | 'OTHER';

export interface SeasonRequest {
  name: string;
  startDate: string;
  endDate: string;
}

export interface SeasonResponse {
  id: number;
  leagueId: number;
  gamePlatform: GamePlatform;
  name: string;
  startDate: string;
  endDate: string;
  status: SeasonStatus;
}
