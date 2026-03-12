import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISoumission, NewSoumission } from '../soumission.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISoumission for edit and NewSoumissionFormGroupInput for create.
 */
type SoumissionFormGroupInput = ISoumission | PartialWithRequiredKeyOf<NewSoumission>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISoumission | NewSoumission> = Omit<T, 'dateSoumission'> & {
  dateSoumission?: string | null;
};

type SoumissionFormRawValue = FormValueOf<ISoumission>;

type NewSoumissionFormRawValue = FormValueOf<NewSoumission>;

type SoumissionFormDefaults = Pick<NewSoumission, 'id' | 'dateSoumission'>;

type SoumissionFormGroupContent = {
  id: FormControl<SoumissionFormRawValue['id'] | NewSoumission['id']>;
  dateSoumission: FormControl<SoumissionFormRawValue['dateSoumission']>;
  statut: FormControl<SoumissionFormRawValue['statut']>;
  evaluation: FormControl<SoumissionFormRawValue['evaluation']>;
  appelOffre: FormControl<SoumissionFormRawValue['appelOffre']>;
  candidat: FormControl<SoumissionFormRawValue['candidat']>;
};

export type SoumissionFormGroup = FormGroup<SoumissionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SoumissionFormService {
  createSoumissionFormGroup(soumission: SoumissionFormGroupInput = { id: null }): SoumissionFormGroup {
    const soumissionRawValue = this.convertSoumissionToSoumissionRawValue({
      ...this.getFormDefaults(),
      ...soumission,
    });
    return new FormGroup<SoumissionFormGroupContent>({
      id: new FormControl(
        { value: soumissionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      dateSoumission: new FormControl(soumissionRawValue.dateSoumission),
      statut: new FormControl(soumissionRawValue.statut),
      evaluation: new FormControl(soumissionRawValue.evaluation),
      appelOffre: new FormControl(soumissionRawValue.appelOffre),
      candidat: new FormControl(soumissionRawValue.candidat),
    });
  }

  getSoumission(form: SoumissionFormGroup): ISoumission | NewSoumission {
    return this.convertSoumissionRawValueToSoumission(form.getRawValue() as SoumissionFormRawValue | NewSoumissionFormRawValue);
  }

  resetForm(form: SoumissionFormGroup, soumission: SoumissionFormGroupInput): void {
    const soumissionRawValue = this.convertSoumissionToSoumissionRawValue({ ...this.getFormDefaults(), ...soumission });
    form.reset(
      {
        ...soumissionRawValue,
        id: { value: soumissionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SoumissionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateSoumission: currentTime,
    };
  }

  private convertSoumissionRawValueToSoumission(
    rawSoumission: SoumissionFormRawValue | NewSoumissionFormRawValue,
  ): ISoumission | NewSoumission {
    return {
      ...rawSoumission,
      dateSoumission: dayjs(rawSoumission.dateSoumission, DATE_TIME_FORMAT),
    };
  }

  private convertSoumissionToSoumissionRawValue(
    soumission: ISoumission | (Partial<NewSoumission> & SoumissionFormDefaults),
  ): SoumissionFormRawValue | PartialWithRequiredKeyOf<NewSoumissionFormRawValue> {
    return {
      ...soumission,
      dateSoumission: soumission.dateSoumission ? soumission.dateSoumission.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
