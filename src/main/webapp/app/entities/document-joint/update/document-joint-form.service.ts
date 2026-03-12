import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IDocumentJoint, NewDocumentJoint } from '../document-joint.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDocumentJoint for edit and NewDocumentJointFormGroupInput for create.
 */
type DocumentJointFormGroupInput = IDocumentJoint | PartialWithRequiredKeyOf<NewDocumentJoint>;

type DocumentJointFormDefaults = Pick<NewDocumentJoint, 'id'>;

type DocumentJointFormGroupContent = {
  id: FormControl<IDocumentJoint['id'] | NewDocumentJoint['id']>;
  nom: FormControl<IDocumentJoint['nom']>;
  format: FormControl<IDocumentJoint['format']>;
  url: FormControl<IDocumentJoint['url']>;
  contenuOcr: FormControl<IDocumentJoint['contenuOcr']>;
  idExterne: FormControl<IDocumentJoint['idExterne']>;
  soumission: FormControl<IDocumentJoint['soumission']>;
};

export type DocumentJointFormGroup = FormGroup<DocumentJointFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DocumentJointFormService {
  createDocumentJointFormGroup(documentJoint: DocumentJointFormGroupInput = { id: null }): DocumentJointFormGroup {
    const documentJointRawValue = {
      ...this.getFormDefaults(),
      ...documentJoint,
    };
    return new FormGroup<DocumentJointFormGroupContent>({
      id: new FormControl(
        { value: documentJointRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(documentJointRawValue.nom, {
        validators: [Validators.required],
      }),
      format: new FormControl(documentJointRawValue.format, {
        validators: [Validators.required],
      }),
      url: new FormControl(documentJointRawValue.url),
      contenuOcr: new FormControl(documentJointRawValue.contenuOcr),
      idExterne: new FormControl(documentJointRawValue.idExterne),
      soumission: new FormControl(documentJointRawValue.soumission),
    });
  }

  getDocumentJoint(form: DocumentJointFormGroup): IDocumentJoint | NewDocumentJoint {
    return form.getRawValue() as IDocumentJoint | NewDocumentJoint;
  }

  resetForm(form: DocumentJointFormGroup, documentJoint: DocumentJointFormGroupInput): void {
    const documentJointRawValue = { ...this.getFormDefaults(), ...documentJoint };
    form.reset(
      {
        ...documentJointRawValue,
        id: { value: documentJointRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DocumentJointFormDefaults {
    return {
      id: null,
    };
  }
}
