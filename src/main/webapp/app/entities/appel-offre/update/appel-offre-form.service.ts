import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import dayjs from 'dayjs/esm';
import { IAppelOffre, NewAppelOffre } from '../appel-offre.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAppelOffre for edit and NewAppelOffreFormGroupInput for create.
 */
type AppelOffreFormGroupInput = IAppelOffre | PartialWithRequiredKeyOf<NewAppelOffre>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAppelOffre | NewAppelOffre> = Omit<T, 'dateCloture'> & {
  dateCloture?: string | null;
};

type AppelOffreFormRawValue = FormValueOf<IAppelOffre>;

type NewAppelOffreFormRawValue = FormValueOf<NewAppelOffre>;

type AppelOffreFormDefaults = Pick<NewAppelOffre, 'id' | 'dateCloture'>;

type AppelOffreFormGroupContent = {
  id: FormControl<AppelOffreFormRawValue['id'] | NewAppelOffre['id']>;
  reference: FormControl<AppelOffreFormRawValue['reference']>;
  titre: FormControl<AppelOffreFormRawValue['titre']>;
  nomFichier: FormControl<AppelOffreFormRawValue['nomFichier']>;
  description: FormControl<AppelOffreFormRawValue['description']>;
  descriptionContentType: FormControl<AppelOffreFormRawValue['descriptionContentType']>;
  dateCloture: FormControl<AppelOffreFormRawValue['dateCloture']>;
  statut: FormControl<AppelOffreFormRawValue['statut']>;
};

export type AppelOffreFormGroup = FormGroup<AppelOffreFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AppelOffreFormService {
  createAppelOffreFormGroup(appelOffre: AppelOffreFormGroupInput = { id: null }): AppelOffreFormGroup {
    const appelOffreRawValue = this.convertAppelOffreToAppelOffreRawValue({
      ...this.getFormDefaults(),
      ...appelOffre,
    });
    return new FormGroup<AppelOffreFormGroupContent>({
      id: new FormControl(
        { value: appelOffreRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(appelOffreRawValue.reference, {
        validators: [Validators.required],
      }),
      titre: new FormControl(appelOffreRawValue.titre, {
        validators: [Validators.required],
      }),
      nomFichier: new FormControl(appelOffreRawValue.nomFichier),
      description: new FormControl(appelOffreRawValue.description),
      descriptionContentType: new FormControl(appelOffreRawValue.descriptionContentType),
      dateCloture: new FormControl(appelOffreRawValue.dateCloture),
      statut: new FormControl(appelOffreRawValue.statut),
    });
  }

  getAppelOffre(form: AppelOffreFormGroup): IAppelOffre | NewAppelOffre {
    return this.convertAppelOffreRawValueToAppelOffre(form.getRawValue() as AppelOffreFormRawValue | NewAppelOffreFormRawValue);
  }

  resetForm(form: AppelOffreFormGroup, appelOffre: AppelOffreFormGroupInput): void {
    const appelOffreRawValue = this.convertAppelOffreToAppelOffreRawValue({ ...this.getFormDefaults(), ...appelOffre });
    form.reset(
      {
        ...appelOffreRawValue,
        id: { value: appelOffreRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AppelOffreFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateCloture: currentTime,
      //nomFichier: null,
    };
  }

  private convertAppelOffreRawValueToAppelOffre(
    rawAppelOffre: AppelOffreFormRawValue | NewAppelOffreFormRawValue,
  ): IAppelOffre | NewAppelOffre {
    return {
      ...rawAppelOffre,
      dateCloture: dayjs(rawAppelOffre.dateCloture, DATE_TIME_FORMAT),
    };
  }

  private convertAppelOffreToAppelOffreRawValue(
    appelOffre: IAppelOffre | (Partial<NewAppelOffre> & AppelOffreFormDefaults),
  ): AppelOffreFormRawValue | PartialWithRequiredKeyOf<NewAppelOffreFormRawValue> {
    return {
      ...appelOffre,
      dateCloture: appelOffre.dateCloture ? appelOffre.dateCloture.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
