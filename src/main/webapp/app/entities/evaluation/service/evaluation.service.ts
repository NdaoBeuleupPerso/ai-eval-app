import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEvaluation, NewEvaluation } from '../evaluation.model';

export type PartialUpdateEvaluation = Partial<IEvaluation> & Pick<IEvaluation, 'id'>;

type RestOf<T extends IEvaluation | NewEvaluation> = Omit<T, 'dateEvaluation'> & {
  dateEvaluation?: string | null;
};

export type RestEvaluation = RestOf<IEvaluation>;

export type NewRestEvaluation = RestOf<NewEvaluation>;

export type PartialUpdateRestEvaluation = RestOf<PartialUpdateEvaluation>;

export type EntityResponseType = HttpResponse<IEvaluation>;
export type EntityArrayResponseType = HttpResponse<IEvaluation[]>;

@Injectable({ providedIn: 'root' })
export class EvaluationService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/evaluations');

  create(evaluation: NewEvaluation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(evaluation);
    return this.http
      .post<RestEvaluation>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(evaluation: IEvaluation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(evaluation);
    return this.http
      .put<RestEvaluation>(`${this.resourceUrl}/${this.getEvaluationIdentifier(evaluation)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(evaluation: PartialUpdateEvaluation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(evaluation);
    return this.http
      .patch<RestEvaluation>(`${this.resourceUrl}/${this.getEvaluationIdentifier(evaluation)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestEvaluation>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEvaluation[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getEvaluationIdentifier(evaluation: Pick<IEvaluation, 'id'>): number {
    return evaluation.id;
  }

  compareEvaluation(o1: Pick<IEvaluation, 'id'> | null, o2: Pick<IEvaluation, 'id'> | null): boolean {
    return o1 && o2 ? this.getEvaluationIdentifier(o1) === this.getEvaluationIdentifier(o2) : o1 === o2;
  }

  addEvaluationToCollectionIfMissing<Type extends Pick<IEvaluation, 'id'>>(
    evaluationCollection: Type[],
    ...evaluationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const evaluations: Type[] = evaluationsToCheck.filter(isPresent);
    if (evaluations.length > 0) {
      const evaluationCollectionIdentifiers = evaluationCollection.map(evaluationItem => this.getEvaluationIdentifier(evaluationItem));
      const evaluationsToAdd = evaluations.filter(evaluationItem => {
        const evaluationIdentifier = this.getEvaluationIdentifier(evaluationItem);
        if (evaluationCollectionIdentifiers.includes(evaluationIdentifier)) {
          return false;
        }
        evaluationCollectionIdentifiers.push(evaluationIdentifier);
        return true;
      });
      return [...evaluationsToAdd, ...evaluationCollection];
    }
    return evaluationCollection;
  }

  protected convertDateFromClient<T extends IEvaluation | NewEvaluation | PartialUpdateEvaluation>(evaluation: T): RestOf<T> {
    return {
      ...evaluation,
      dateEvaluation: evaluation.dateEvaluation?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restEvaluation: RestEvaluation): IEvaluation {
    return {
      ...restEvaluation,
      dateEvaluation: restEvaluation.dateEvaluation ? dayjs(restEvaluation.dateEvaluation) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestEvaluation>): HttpResponse<IEvaluation> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestEvaluation[]>): HttpResponse<IEvaluation[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
