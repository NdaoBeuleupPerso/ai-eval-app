import dayjs from 'dayjs/esm';
import { StatutAppel } from 'app/entities/enumerations/statut-appel.model';

export interface IAppelOffre {
  id: number;
  reference?: string | null;
  titre?: string | null;
  description?: string | null;
  descriptionContentType?: string | null;
  dateCloture?: dayjs.Dayjs | null;
  statut?: keyof typeof StatutAppel | null;
}

export type NewAppelOffre = Omit<IAppelOffre, 'id'> & { id: null };
