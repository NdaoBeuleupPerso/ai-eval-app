import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAppelOffre } from '../appel-offre.model';
import { AppelOffreService } from '../service/appel-offre.service';

const appelOffreResolve = (route: ActivatedRouteSnapshot): Observable<null | IAppelOffre> => {
  const id = route.params.id;
  if (id) {
    return inject(AppelOffreService)
      .find(id)
      .pipe(
        mergeMap((appelOffre: HttpResponse<IAppelOffre>) => {
          if (appelOffre.body) {
            return of(appelOffre.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default appelOffreResolve;
