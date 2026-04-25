import { TypeSource } from 'app/entities/enumerations/type-source.model';

export interface IReferenceLegale {
  id: number;
  titre?: string | null;
  contenu?: string | null;
  typeSource?: keyof typeof TypeSource | null;
  version?: string | null;
  qdrantUuid?: string | null;
  source?: string | null;
  document?: string | null;
  documentContentType?: string | null;
}

export type NewReferenceLegale = Omit<IReferenceLegale, 'id'> & { id: null };
