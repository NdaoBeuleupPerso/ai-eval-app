import { IAppelOffre } from 'app/entities/appel-offre/appel-offre.model';
import { ICandidat } from 'app/entities/candidat/candidat.model';
import { IDocumentJoint } from 'app/entities/document-joint/document-joint.model';
import { StatutEvaluation } from 'app/entities/enumerations/statut-evaluation.model';
import { IEvaluation } from 'app/entities/evaluation/evaluation.model';
import dayjs from 'dayjs/esm';

// Interface pour capturer les fichiers avant l'envoi
export interface ISoumissionFile {
  file: string; // Base64
  fileContentType: string;
  nom: string;
}

export interface ISoumission {
  id: number;
  dateSoumission?: dayjs.Dayjs | null;
  statut?: keyof typeof StatutEvaluation | null;
  evaluation?: Pick<IEvaluation, 'id'> | null;
  appelOffre?: Pick<IAppelOffre, 'id' | 'reference' | 'titre'> | null;
  candidat?: Pick<ICandidat, 'id' | 'nom'> | null;
  documents?: IDocumentJoint[] | null;

  // Champ de transport pour les nouveaux fichiers (Transient côté Java)
  fichiersNouveaux?: ISoumissionFile[] | null;
}

export type NewSoumission = Omit<ISoumission, 'id'> & { id: null };
