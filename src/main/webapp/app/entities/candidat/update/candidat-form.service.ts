import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICandidat, NewCandidat } from '../candidat.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICandidat for edit and NewCandidatFormGroupInput for create.
 */
type CandidatFormGroupInput = ICandidat | PartialWithRequiredKeyOf<NewCandidat>;

type CandidatFormDefaults = Pick<NewCandidat, 'id'>;

type CandidatFormGroupContent = {
  id: FormControl<ICandidat['id'] | NewCandidat['id']>;
  nom: FormControl<ICandidat['nom']>;
  siret: FormControl<ICandidat['siret']>;
  email: FormControl<ICandidat['email']>;
};

export type CandidatFormGroup = FormGroup<CandidatFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CandidatFormService {
  createCandidatFormGroup(candidat: CandidatFormGroupInput = { id: null }): CandidatFormGroup {
    const candidatRawValue = {
      ...this.getFormDefaults(),
      ...candidat,
    };
    return new FormGroup<CandidatFormGroupContent>({
      id: new FormControl(
        { value: candidatRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(candidatRawValue.nom, {
        validators: [Validators.required],
      }),
      siret: new FormControl(candidatRawValue.siret),
      email: new FormControl(candidatRawValue.email),
    });
  }

  getCandidat(form: CandidatFormGroup): ICandidat | NewCandidat {
    return form.getRawValue() as ICandidat | NewCandidat;
  }

  resetForm(form: CandidatFormGroup, candidat: CandidatFormGroupInput): void {
    const candidatRawValue = { ...this.getFormDefaults(), ...candidat };
    form.reset(
      {
        ...candidatRawValue,
        id: { value: candidatRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CandidatFormDefaults {
    return {
      id: null,
    };
  }
}
