export interface IReferenceLegale {
  id: number;
  titre?: string | null;
  contenu?: string | null;
  qdrantUuid?: string | null;
  source?: string | null;
}

export type NewReferenceLegale = Omit<IReferenceLegale, 'id'> & { id: null };
