import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICandidat } from '../candidat.model';
import { CandidatService } from '../service/candidat.service';

const candidatResolve = (route: ActivatedRouteSnapshot): Observable<null | ICandidat> => {
  const id = route.params.id;
  if (id) {
    return inject(CandidatService)
      .find(id)
      .pipe(
        mergeMap((candidat: HttpResponse<ICandidat>) => {
          if (candidat.body) {
            return of(candidat.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default candidatResolve;
