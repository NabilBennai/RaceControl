export interface TeamRequest {
  name: string;
  color: string;
  logoUrl: string | null;
}

export interface TeamMemberRequest {
  userId: number;
}

export interface TeamMemberResponse {
  id: number;
  userId: number;
  username: string;
  email: string;
}

export interface TeamResponse {
  id: number;
  seasonId: number;
  name: string;
  color: string;
  logoUrl: string | null;
  members: TeamMemberResponse[];
}
