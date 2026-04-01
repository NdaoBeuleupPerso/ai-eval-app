import dayjs from 'dayjs/esm';
import { IEvaluation } from 'app/entities/evaluation/evaluation.model';
import { IAppelOffre } from 'app/entities/appel-offre/appel-offre.model';
import { ICandidat } from 'app/entities/candidat/candidat.model';
import { StatutEvaluation } from 'app/entities/enumerations/statut-evaluation.model';
import { IDocumentJoint } from 'app/entities/document-joint/document-joint.model';
export interface ISoumission {
  id: number;
  dateSoumission?: dayjs.Dayjs | null;
  statut?: keyof typeof StatutEvaluation | null;
  evaluation?: Pick<IEvaluation, 'id'> | null;
  appelOffre?: Pick<IAppelOffre, 'id' | 'reference'> | null;
  candidat?: Pick<ICandidat, 'id' | 'nom'> | null;
  documents?: IDocumentJoint[] | null;
}

export type NewSoumission = Omit<ISoumission, 'id'> & { id: null };
