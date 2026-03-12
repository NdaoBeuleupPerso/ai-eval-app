import { ICritere, NewCritere } from './critere.model';

export const sampleWithRequiredData: ICritere = {
  id: 13806,
  nom: 'rudement circulaire',
  ponderation: 12859.32,
  categorie: 'TECHNIQUE',
};

export const sampleWithPartialData: ICritere = {
  id: 18532,
  nom: "à l'égard de membre titulaire volontiers",
  ponderation: 2353.37,
  categorie: 'FINANCIER',
  description: 'si bien que',
};

export const sampleWithFullData: ICritere = {
  id: 9738,
  nom: 'à partir de comme',
  ponderation: 10453.77,
  categorie: 'TECHNIQUE',
  description: 'cependant clac',
};

export const sampleWithNewData: NewCritere = {
  nom: 'au dépens de',
  ponderation: 11640.81,
  categorie: 'ADMINISTRATIF',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
