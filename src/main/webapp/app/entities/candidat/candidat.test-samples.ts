import { ICandidat, NewCandidat } from './candidat.model';

export const sampleWithRequiredData: ICandidat = {
  id: 18880,
  nom: 'à demi de façon à ce que responsable',
};

export const sampleWithPartialData: ICandidat = {
  id: 5985,
  nom: 'communauté étudiante assez communauté étudiante',
  siret: 'hirsute alors que',
  email: 'Sarah24@gmail.com',
};

export const sampleWithFullData: ICandidat = {
  id: 11710,
  nom: 'dans la mesure où déjà de sorte que',
  siret: 'antagoniste direction',
  email: 'Isabelle.Aubert65@gmail.com',
};

export const sampleWithNewData: NewCandidat = {
  nom: 'ha ha',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
