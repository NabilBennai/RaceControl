export type LeagueVisibility = 'PUBLIC' | 'PRIVATE';
export type LeagueRole = 'OWNER' | 'ADMIN' | 'STEWARD' | 'DRIVER';
export type GamePlatform = 'F1' | 'ACC' | 'IRACING' | 'LMU' | 'RF2' | 'OTHER';

export interface LeagueRequest {
  name: string;
  description: string;
  gamePlatform: GamePlatform;
  visibility: LeagueVisibility;
}

export interface LeagueResponse {
  id: number;
  name: string;
  description: string;
  gamePlatform: GamePlatform;
  visibility: LeagueVisibility;
  myRole: LeagueRole | null;
  invitationCode: string | null;
}

export type MembershipStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'BANNED';

export interface JoinByCodeRequest {
  invitationCode: string;
}

export interface LeagueJoinResponse {
  leagueId: number;
  status: MembershipStatus;
  message: string;
}

export interface LeagueMemberResponse {
  id: number;
  userId: number;
  username: string;
  email: string;
  role: LeagueRole;
  status: MembershipStatus;
  requestedAt: string | null;
}
