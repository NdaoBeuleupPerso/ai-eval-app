import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEvaluation, NewEvaluation } from '../evaluation.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEvaluation for edit and NewEvaluationFormGroupInput for create.
 */
type EvaluationFormGroupInput = IEvaluation | PartialWithRequiredKeyOf<NewEvaluation>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEvaluation | NewEvaluation> = Omit<T, 'dateEvaluation'> & {
  dateEvaluation?: string | null;
};

type EvaluationFormRawValue = FormValueOf<IEvaluation>;

type NewEvaluationFormRawValue = FormValueOf<NewEvaluation>;

type EvaluationFormDefaults = Pick<NewEvaluation, 'id' | 'dateEvaluation' | 'estValidee'>;

type EvaluationFormGroupContent = {
  id: FormControl<EvaluationFormRawValue['id'] | NewEvaluation['id']>;
  scoreGlobal: FormControl<EvaluationFormRawValue['scoreGlobal']>;
  scoreAdmin: FormControl<EvaluationFormRawValue['scoreAdmin']>;
  scoreTech: FormControl<EvaluationFormRawValue['scoreTech']>;
  scoreFin: FormControl<EvaluationFormRawValue['scoreFin']>;
  rapportAnalyse: FormControl<EvaluationFormRawValue['rapportAnalyse']>;
  documentPv: FormControl<EvaluationFormRawValue['documentPv']>;
  documentPvContentType: FormControl<EvaluationFormRawValue['documentPvContentType']>;
  dateEvaluation: FormControl<EvaluationFormRawValue['dateEvaluation']>;
  estValidee: FormControl<EvaluationFormRawValue['estValidee']>;
  commentaireEvaluateur: FormControl<EvaluationFormRawValue['commentaireEvaluateur']>;
  evaluateur: FormControl<EvaluationFormRawValue['evaluateur']>;
  soumission: FormControl<EvaluationFormRawValue['soumission']>; // <--- AJOUTÉ
};

export type EvaluationFormGroup = FormGroup<EvaluationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EvaluationFormService {
  createEvaluationFormGroup(evaluation: EvaluationFormGroupInput = { id: null }): EvaluationFormGroup {
    const evaluationRawValue = this.convertEvaluationToEvaluationRawValue({
      ...this.getFormDefaults(),
      ...evaluation,
    });
    return new FormGroup<EvaluationFormGroupContent>({
      id: new FormControl(
        { value: evaluationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      scoreGlobal: new FormControl(evaluationRawValue.scoreGlobal),
      scoreAdmin: new FormControl(evaluationRawValue.scoreAdmin),
      scoreTech: new FormControl(evaluationRawValue.scoreTech),
      scoreFin: new FormControl(evaluationRawValue.scoreFin),
      rapportAnalyse: new FormControl(evaluationRawValue.rapportAnalyse),
      documentPv: new FormControl(evaluationRawValue.documentPv),
      documentPvContentType: new FormControl(evaluationRawValue.documentPvContentType),
      dateEvaluation: new FormControl(evaluationRawValue.dateEvaluation),
      estValidee: new FormControl(evaluationRawValue.estValidee),
      commentaireEvaluateur: new FormControl(evaluationRawValue.commentaireEvaluateur),
      evaluateur: new FormControl(evaluationRawValue.evaluateur),
      soumission: new FormControl(evaluationRawValue.soumission), // <--- AJOUTÉ
    });
  }

  getEvaluation(form: EvaluationFormGroup): IEvaluation | NewEvaluation {
    return this.convertEvaluationRawValueToEvaluation(form.getRawValue() as EvaluationFormRawValue | NewEvaluationFormRawValue);
  }

  resetForm(form: EvaluationFormGroup, evaluation: EvaluationFormGroupInput): void {
    const evaluationRawValue = this.convertEvaluationToEvaluationRawValue({ ...this.getFormDefaults(), ...evaluation });
    form.reset(
      {
        ...evaluationRawValue,
        id: { value: evaluationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): EvaluationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateEvaluation: currentTime,
      estValidee: false,
    };
  }

  private convertEvaluationRawValueToEvaluation(
    rawEvaluation: EvaluationFormRawValue | NewEvaluationFormRawValue,
  ): IEvaluation | NewEvaluation {
    return {
      ...rawEvaluation,
      dateEvaluation: dayjs(rawEvaluation.dateEvaluation, DATE_TIME_FORMAT),
    };
  }

  private convertEvaluationToEvaluationRawValue(
    evaluation: IEvaluation | (Partial<NewEvaluation> & EvaluationFormDefaults),
  ): EvaluationFormRawValue | PartialWithRequiredKeyOf<NewEvaluationFormRawValue> {
    return {
      ...evaluation,
      dateEvaluation: evaluation.dateEvaluation ? evaluation.dateEvaluation.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
