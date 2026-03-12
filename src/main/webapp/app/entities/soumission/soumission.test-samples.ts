import dayjs from 'dayjs/esm';

import { ISoumission, NewSoumission } from './soumission.model';

export const sampleWithRequiredData: ISoumission = {
  id: 25611,
};

export const sampleWithPartialData: ISoumission = {
  id: 10675,
};

export const sampleWithFullData: ISoumission = {
  id: 27901,
  dateSoumission: dayjs('2026-03-11T19:07'),
  statut: 'EN_COURS',
};

export const sampleWithNewData: NewSoumission = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
