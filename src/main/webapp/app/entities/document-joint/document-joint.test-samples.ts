import { IDocumentJoint, NewDocumentJoint } from './document-joint.model';

export const sampleWithRequiredData: IDocumentJoint = {
  id: 2819,
  nom: 'concernant espiègle dorénavant',
  format: 'OFFRE_TECHNIQUE',
};

export const sampleWithPartialData: IDocumentJoint = {
  id: 5659,
  nom: 'surveiller',
  format: 'GARANTIE',
  contenuOcr: '../fake-data/blob/hipster.txt',
  idExterne: 'en faveur de',
};

export const sampleWithFullData: IDocumentJoint = {
  id: 25807,
  nom: 'touchant crac',
  format: 'OFFRE_TECHNIQUE',
  url: 'https://svelte-lectorat.fr',
  contenuOcr: '../fake-data/blob/hipster.txt',
  idExterne: "à l'encontre de",
};

export const sampleWithNewData: NewDocumentJoint = {
  nom: 'que diététiste',
  format: 'AUTRE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
