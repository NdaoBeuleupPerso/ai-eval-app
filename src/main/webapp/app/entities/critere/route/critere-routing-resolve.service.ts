import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICritere } from '../critere.model';
import { CritereService } from '../service/critere.service';

const critereResolve = (route: ActivatedRouteSnapshot): Observable<null | ICritere> => {
  const id = route.params.id;
  if (id) {
    return inject(CritereService)
      .find(id)
      .pipe(
        mergeMap((critere: HttpResponse<ICritere>) => {
          if (critere.body) {
            return of(critere.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default critereResolve;
