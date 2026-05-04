import { IAppelOffre } from 'app/entities/appel-offre/appel-offre.model';
import { StatutCritere } from 'app/entities/enumerations/statut-critere.model';
import { TypeCritere } from 'app/entities/enumerations/type-critere.model';
export interface ICritere {
  id: number;
  nom?: string | null;
  ponderation?: number | null;
  categorie?: keyof typeof TypeCritere | null;
  description?: string | null;
  statut?: keyof typeof StatutCritere | null;
  appelOffre?: Pick<IAppelOffre, 'id' | 'reference'> | null;
}

export type NewCritere = Omit<ICritere, 'id'> & { id: null };
