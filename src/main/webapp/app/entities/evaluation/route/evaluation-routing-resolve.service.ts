import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEvaluation } from '../evaluation.model';
import { EvaluationService } from '../service/evaluation.service';

const evaluationResolve = (route: ActivatedRouteSnapshot): Observable<null | IEvaluation> => {
  const id = route.params.id;
  if (id) {
    return inject(EvaluationService)
      .find(id)
      .pipe(
        mergeMap((evaluation: HttpResponse<IEvaluation>) => {
          if (evaluation.body) {
            return of(evaluation.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default evaluationResolve;
