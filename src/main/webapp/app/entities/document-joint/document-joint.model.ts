import { ISoumission } from 'app/entities/soumission/soumission.model';
import { FormatDocument } from 'app/entities/enumerations/format-document.model';

export interface IDocumentJoint {
  id: number;
  nom?: string | null;
  format?: keyof typeof FormatDocument | null;
  url?: string | null;
  contenuOcr?: string | null;
  idExterne?: string | null;
  soumission?: Pick<ISoumission, 'id'> | null;
}

export type NewDocumentJoint = Omit<IDocumentJoint, 'id'> & { id: null };
