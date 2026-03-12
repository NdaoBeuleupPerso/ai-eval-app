import dayjs from 'dayjs/esm';

import { ITraceAudit, NewTraceAudit } from './trace-audit.model';

export const sampleWithRequiredData: ITraceAudit = {
  id: 30065,
  action: 'ouch tant que prier',
  horodatage: dayjs('2026-03-12T11:22'),
};

export const sampleWithPartialData: ITraceAudit = {
  id: 8397,
  action: 'smack',
  horodatage: dayjs('2026-03-12T03:36'),
  identifiantUtilisateur: 'hystérique',
};

export const sampleWithFullData: ITraceAudit = {
  id: 26367,
  action: 'totalement membre du personnel',
  horodatage: dayjs('2026-03-12T10:23'),
  details: '../fake-data/blob/hipster.txt',
  identifiantUtilisateur: 'dans la mesure où entièrement avex',
  promptUtilise: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewTraceAudit = {
  action: 'commis de cuisine perplexe',
  horodatage: dayjs('2026-03-12T00:35'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
