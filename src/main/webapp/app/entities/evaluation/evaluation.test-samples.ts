import dayjs from 'dayjs/esm';

import { IEvaluation, NewEvaluation } from './evaluation.model';

export const sampleWithRequiredData: IEvaluation = {
  id: 29871,
};

export const sampleWithPartialData: IEvaluation = {
  id: 27213,
  scoreAdmin: 9771.86,
};

export const sampleWithFullData: IEvaluation = {
  id: 32076,
  scoreGlobal: 30688.48,
  scoreAdmin: 15333.96,
  scoreTech: 18442.81,
  scoreFin: 26128.63,
  rapportAnalyse: '../fake-data/blob/hipster.txt',
  documentPv: '../fake-data/blob/hipster.png',
  documentPvContentType: 'unknown',
  dateEvaluation: dayjs('2026-03-12T15:48'),
  estValidee: true,
  commentaireEvaluateur: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewEvaluation = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
