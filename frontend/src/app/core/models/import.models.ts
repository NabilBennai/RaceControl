import {ResultStatus} from './result.models';

export type ImportStatus = 'PENDING' | 'FAILED' | 'CONFIRMED';

export interface ImportLinePreview {
  lineNumber: number;
  userEmail: string;
  position: number;
  totalTime: string | null;
  bestLap: string | null;
  polePosition: boolean;
  incidents: number;
  status: ResultStatus;
}

export interface ImportErrorResponse {
  lineNumber: number;
  message: string;
}

export interface ImportResponse {
  id: number;
  raceId: number;
  originalFileName: string;
  status: ImportStatus;
  preview: ImportLinePreview[];
  errors: ImportErrorResponse[];
}
