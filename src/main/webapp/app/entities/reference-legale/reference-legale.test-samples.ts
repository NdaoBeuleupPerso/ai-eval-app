import { IReferenceLegale, NewReferenceLegale } from './reference-legale.model';

export const sampleWithRequiredData: IReferenceLegale = {
  id: 27349,
  titre: 'débile de façon à ce que parfois',
  contenu: '../fake-data/blob/hipster.txt',
};

export const sampleWithPartialData: IReferenceLegale = {
  id: 23653,
  titre: 'alors que vivace concernant',
  contenu: '../fake-data/blob/hipster.txt',
};

export const sampleWithFullData: IReferenceLegale = {
  id: 26344,
  titre: 'porte-parole alentour',
  contenu: '../fake-data/blob/hipster.txt',
  qdrantUuid: 'tandis que touriste',
  source: 'briller',
};

export const sampleWithNewData: NewReferenceLegale = {
  titre: 'tard',
  contenu: '../fake-data/blob/hipster.txt',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
