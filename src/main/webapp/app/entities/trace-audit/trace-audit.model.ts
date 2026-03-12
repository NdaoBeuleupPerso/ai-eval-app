import dayjs from 'dayjs/esm';
import { IEvaluation } from 'app/entities/evaluation/evaluation.model';

export interface ITraceAudit {
  id: number;
  action?: string | null;
  horodatage?: dayjs.Dayjs | null;
  details?: string | null;
  identifiantUtilisateur?: string | null;
  promptUtilise?: string | null;
  evaluation?: Pick<IEvaluation, 'id'> | null;
}

export type NewTraceAudit = Omit<ITraceAudit, 'id'> & { id: null };
