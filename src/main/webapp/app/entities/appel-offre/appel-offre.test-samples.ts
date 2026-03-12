import dayjs from 'dayjs/esm';

import { IAppelOffre, NewAppelOffre } from './appel-offre.model';

export const sampleWithRequiredData: IAppelOffre = {
  id: 32633,
  reference: 'via rire',
  titre: 'à travers bien que bzzz',
};

export const sampleWithPartialData: IAppelOffre = {
  id: 27195,
  reference: 'snif',
  titre: 'parce que fêter revoir',
};

export const sampleWithFullData: IAppelOffre = {
  id: 7486,
  reference: 'miaou glouglou tant',
  titre: 'tant ding introduire',
  description: '../fake-data/blob/hipster.png',
  descriptionContentType: 'unknown',
  dateCloture: dayjs('2026-03-12T12:08'),
  statut: 'CLOTURE',
};

export const sampleWithNewData: NewAppelOffre = {
  reference: 'aussitôt que pousser vétuste',
  titre: 'lâche',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
