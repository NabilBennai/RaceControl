export type PointRuleType =
  'FINISH_POSITION'
  | 'POLE_POSITION'
  | 'FASTEST_LAP'
  | 'CLEAN_RACE'
  | 'PARTICIPATION';

export interface PointRuleRequest {
  type: PointRuleType;
  positionRank: number | null;
  points: number;
}

export interface PointSystemRequest {
  name: string;
  rules: PointRuleRequest[];
}

export interface PointRuleResponse {
  id: number;
  type: PointRuleType;
  positionRank: number | null;
  points: number;
}

export interface PointSystemResponse {
  id: number;
  seasonId: number;
  name: string;
  rules: PointRuleResponse[];
}
