import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IReferenceLegale } from '../reference-legale.model';
import { ReferenceLegaleService } from '../service/reference-legale.service';

const referenceLegaleResolve = (route: ActivatedRouteSnapshot): Observable<null | IReferenceLegale> => {
  const id = route.params.id;
  if (id) {
    return inject(ReferenceLegaleService)
      .find(id)
      .pipe(
        mergeMap((referenceLegale: HttpResponse<IReferenceLegale>) => {
          if (referenceLegale.body) {
            return of(referenceLegale.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default referenceLegaleResolve;
