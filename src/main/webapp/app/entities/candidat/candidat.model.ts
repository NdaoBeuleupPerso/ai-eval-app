export interface ICandidat {
  id: number;
  nom?: string | null;
  siret?: string | null;
  email?: string | null;
}

export type NewCandidat = Omit<ICandidat, 'id'> & { id: null };
