import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IReferenceLegale, NewReferenceLegale } from '../reference-legale.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReferenceLegale for edit and NewReferenceLegaleFormGroupInput for create.
 */
type ReferenceLegaleFormGroupInput = IReferenceLegale | PartialWithRequiredKeyOf<NewReferenceLegale>;

type ReferenceLegaleFormDefaults = Pick<NewReferenceLegale, 'id'>;

type ReferenceLegaleFormGroupContent = {
  id: FormControl<IReferenceLegale['id'] | NewReferenceLegale['id']>;
  titre: FormControl<IReferenceLegale['titre']>;
  contenu: FormControl<IReferenceLegale['contenu']>;
  typeSource: FormControl<IReferenceLegale['typeSource']>;
  version: FormControl<IReferenceLegale['version']>;
  qdrantUuid: FormControl<IReferenceLegale['qdrantUuid']>;
  source: FormControl<IReferenceLegale['source']>;
  document: FormControl<IReferenceLegale['document']>;
  documentContentType: FormControl<IReferenceLegale['documentContentType']>;
};

export type ReferenceLegaleFormGroup = FormGroup<ReferenceLegaleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReferenceLegaleFormService {
  createReferenceLegaleFormGroup(referenceLegale: ReferenceLegaleFormGroupInput = { id: null }): ReferenceLegaleFormGroup {
    const referenceLegaleRawValue = {
      ...this.getFormDefaults(),
      ...referenceLegale,
    };
    return new FormGroup<ReferenceLegaleFormGroupContent>({
      id: new FormControl(
        { value: referenceLegaleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titre: new FormControl(referenceLegaleRawValue.titre, {
        validators: [Validators.required],
      }),
      contenu: new FormControl(referenceLegaleRawValue.contenu, {
        validators: [Validators.required],
      }),
      typeSource: new FormControl(referenceLegaleRawValue.typeSource, {
        validators: [Validators.required],
      }),
      version: new FormControl(referenceLegaleRawValue.version),
      qdrantUuid: new FormControl(referenceLegaleRawValue.qdrantUuid),
      source: new FormControl(referenceLegaleRawValue.source),

      document: new FormControl(referenceLegaleRawValue.document),
      documentContentType: new FormControl(referenceLegaleRawValue.documentContentType),
    });
  }

  getReferenceLegale(form: ReferenceLegaleFormGroup): IReferenceLegale | NewReferenceLegale {
    return form.getRawValue() as IReferenceLegale | NewReferenceLegale;
  }

  resetForm(form: ReferenceLegaleFormGroup, referenceLegale: ReferenceLegaleFormGroupInput): void {
    const referenceLegaleRawValue = { ...this.getFormDefaults(), ...referenceLegale };
    form.reset(
      {
        ...referenceLegaleRawValue,
        id: { value: referenceLegaleRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ReferenceLegaleFormDefaults {
    return {
      id: null,
    };
  }
}
