import { IReferenceLegale, NewReferenceLegale } from './reference-legale.model';

export const sampleWithRequiredData: IReferenceLegale = {
  id: 27349,
  titre: 'débile de façon à ce que parfois',
  contenu: '../fake-data/blob/hipster.txt',
  typeSource: 'GUIDE_INTERNE',
};

export const sampleWithPartialData: IReferenceLegale = {
  id: 23653,
  titre: 'alors que vivace concernant',
  contenu: '../fake-data/blob/hipster.txt',
  typeSource: 'PV_HISTORIQUE',
};

export const sampleWithFullData: IReferenceLegale = {
  id: 26344,
  titre: 'porte-parole alentour',
  contenu: '../fake-data/blob/hipster.txt',
  typeSource: 'PV_HISTORIQUE',
  version: 'vouh hormis briller',
  qdrantUuid: 'chut',
};

export const sampleWithNewData: NewReferenceLegale = {
  titre: 'tard',
  contenu: '../fake-data/blob/hipster.txt',
  typeSource: 'CODE_MARCHES',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
