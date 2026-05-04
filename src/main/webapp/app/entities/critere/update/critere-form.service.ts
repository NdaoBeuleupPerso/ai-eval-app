import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { StatutCritere } from 'app/entities/enumerations/statut-critere.model'; // Import ajouté
import { ICritere, NewCritere } from '../critere.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICritere for edit and NewCritereFormGroupInput for create.
 */
type CritereFormGroupInput = ICritere | PartialWithRequiredKeyOf<NewCritere>;

type CritereFormDefaults = Pick<NewCritere, 'id' | 'statut'>; // Ajout de statut dans les defaults

type CritereFormGroupContent = {
  id: FormControl<ICritere['id'] | NewCritere['id']>;
  nom: FormControl<ICritere['nom']>;
  ponderation: FormControl<ICritere['ponderation']>;
  categorie: FormControl<ICritere['categorie']>;
  description: FormControl<ICritere['description']>;
  statut: FormControl<ICritere['statut']>; // Champ ajouté
  appelOffre: FormControl<ICritere['appelOffre']>;
};

export type CritereFormGroup = FormGroup<CritereFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CritereFormService {
  createCritereFormGroup(critere: CritereFormGroupInput = { id: null }): CritereFormGroup {
    const critereRawValue = {
      ...this.getFormDefaults(),
      ...critere,
    };
    return new FormGroup<CritereFormGroupContent>({
      id: new FormControl(
        { value: critereRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(critereRawValue.nom, {
        validators: [Validators.required],
      }),
      ponderation: new FormControl(critereRawValue.ponderation, {
        validators: [Validators.required],
      }),
      categorie: new FormControl(critereRawValue.categorie, {
        validators: [Validators.required],
      }),
      description: new FormControl(critereRawValue.description),
      statut: new FormControl(critereRawValue.statut ?? StatutCritere.VALIDE, {
        validators: [Validators.required],
      }),
      appelOffre: new FormControl(critereRawValue.appelOffre),
    });
  }

  getCritere(form: CritereFormGroup): ICritere | NewCritere {
    return form.getRawValue() as ICritere | NewCritere;
  }

  resetForm(form: CritereFormGroup, critere: CritereFormGroupInput): void {
    const critereRawValue = { ...this.getFormDefaults(), ...critere };
    form.reset(
      {
        ...critereRawValue,
        id: { value: critereRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CritereFormDefaults {
    return {
      id: null,
      statut: StatutCritere.VALIDE, // Valeur par défaut pour les nouveaux critères
    };
  }
}
