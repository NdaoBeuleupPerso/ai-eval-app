import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISoumission } from '../soumission.model';
import { SoumissionService } from '../service/soumission.service';

const soumissionResolve = (route: ActivatedRouteSnapshot): Observable<null | ISoumission> => {
  const id = route.params.id;
  if (id) {
    return inject(SoumissionService)
      .find(id)
      .pipe(
        mergeMap((soumission: HttpResponse<ISoumission>) => {
          if (soumission.body) {
            return of(soumission.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default soumissionResolve;
