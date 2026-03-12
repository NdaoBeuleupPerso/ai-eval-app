import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IEvaluation {
  id: number;
  scoreGlobal?: number | null;
  scoreAdmin?: number | null;
  scoreTech?: number | null;
  scoreFin?: number | null;
  rapportAnalyse?: string | null;
  documentPv?: string | null;
  documentPvContentType?: string | null;
  dateEvaluation?: dayjs.Dayjs | null;
  estValidee?: boolean | null;
  commentaireEvaluateur?: string | null;
  evaluateur?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewEvaluation = Omit<IEvaluation, 'id'> & { id: null };
