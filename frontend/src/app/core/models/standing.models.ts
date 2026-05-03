export interface DriverStandingResponse {
  userId: number;
  username: string;
  email: string;
  points: number;
  wins: number;
  podiums: number;
  poles: number;
  fastestLaps: number;
}

export interface TeamStandingResponse {
  teamId: number;
  teamName: string;
  color: string;
  points: number;
  wins: number;
  podiums: number;
  poles: number;
  fastestLaps: number;
}

export interface StandingsResponse {
  seasonId: number;
  drivers: DriverStandingResponse[];
  teams: TeamStandingResponse[];
}
