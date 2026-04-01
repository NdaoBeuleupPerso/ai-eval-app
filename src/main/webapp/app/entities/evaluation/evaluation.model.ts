import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ISoumission } from 'app/entities/soumission/soumission.model'; // Importez la soumission

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
  soumission?: ISoumission | null; // <--- AJOUTEZ CECI
}

export type NewEvaluation = Omit<IEvaluation, 'id'> & { id: null };
