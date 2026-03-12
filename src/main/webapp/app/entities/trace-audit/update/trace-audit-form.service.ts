import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITraceAudit, NewTraceAudit } from '../trace-audit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITraceAudit for edit and NewTraceAuditFormGroupInput for create.
 */
type TraceAuditFormGroupInput = ITraceAudit | PartialWithRequiredKeyOf<NewTraceAudit>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITraceAudit | NewTraceAudit> = Omit<T, 'horodatage'> & {
  horodatage?: string | null;
};

type TraceAuditFormRawValue = FormValueOf<ITraceAudit>;

type NewTraceAuditFormRawValue = FormValueOf<NewTraceAudit>;

type TraceAuditFormDefaults = Pick<NewTraceAudit, 'id' | 'horodatage'>;

type TraceAuditFormGroupContent = {
  id: FormControl<TraceAuditFormRawValue['id'] | NewTraceAudit['id']>;
  action: FormControl<TraceAuditFormRawValue['action']>;
  horodatage: FormControl<TraceAuditFormRawValue['horodatage']>;
  details: FormControl<TraceAuditFormRawValue['details']>;
  identifiantUtilisateur: FormControl<TraceAuditFormRawValue['identifiantUtilisateur']>;
  promptUtilise: FormControl<TraceAuditFormRawValue['promptUtilise']>;
  evaluation: FormControl<TraceAuditFormRawValue['evaluation']>;
};

export type TraceAuditFormGroup = FormGroup<TraceAuditFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TraceAuditFormService {
  createTraceAuditFormGroup(traceAudit: TraceAuditFormGroupInput = { id: null }): TraceAuditFormGroup {
    const traceAuditRawValue = this.convertTraceAuditToTraceAuditRawValue({
      ...this.getFormDefaults(),
      ...traceAudit,
    });
    return new FormGroup<TraceAuditFormGroupContent>({
      id: new FormControl(
        { value: traceAuditRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      action: new FormControl(traceAuditRawValue.action, {
        validators: [Validators.required],
      }),
      horodatage: new FormControl(traceAuditRawValue.horodatage, {
        validators: [Validators.required],
      }),
      details: new FormControl(traceAuditRawValue.details),
      identifiantUtilisateur: new FormControl(traceAuditRawValue.identifiantUtilisateur),
      promptUtilise: new FormControl(traceAuditRawValue.promptUtilise),
      evaluation: new FormControl(traceAuditRawValue.evaluation),
    });
  }

  getTraceAudit(form: TraceAuditFormGroup): ITraceAudit | NewTraceAudit {
    return this.convertTraceAuditRawValueToTraceAudit(form.getRawValue() as TraceAuditFormRawValue | NewTraceAuditFormRawValue);
  }

  resetForm(form: TraceAuditFormGroup, traceAudit: TraceAuditFormGroupInput): void {
    const traceAuditRawValue = this.convertTraceAuditToTraceAuditRawValue({ ...this.getFormDefaults(), ...traceAudit });
    form.reset(
      {
        ...traceAuditRawValue,
        id: { value: traceAuditRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TraceAuditFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      horodatage: currentTime,
    };
  }

  private convertTraceAuditRawValueToTraceAudit(
    rawTraceAudit: TraceAuditFormRawValue | NewTraceAuditFormRawValue,
  ): ITraceAudit | NewTraceAudit {
    return {
      ...rawTraceAudit,
      horodatage: dayjs(rawTraceAudit.horodatage, DATE_TIME_FORMAT),
    };
  }

  private convertTraceAuditToTraceAuditRawValue(
    traceAudit: ITraceAudit | (Partial<NewTraceAudit> & TraceAuditFormDefaults),
  ): TraceAuditFormRawValue | PartialWithRequiredKeyOf<NewTraceAuditFormRawValue> {
    return {
      ...traceAudit,
      horodatage: traceAudit.horodatage ? traceAudit.horodatage.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
