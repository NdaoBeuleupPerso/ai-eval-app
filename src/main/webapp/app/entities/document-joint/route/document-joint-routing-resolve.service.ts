import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDocumentJoint } from '../document-joint.model';
import { DocumentJointService } from '../service/document-joint.service';

const documentJointResolve = (route: ActivatedRouteSnapshot): Observable<null | IDocumentJoint> => {
  const id = route.params.id;
  if (id) {
    return inject(DocumentJointService)
      .find(id)
      .pipe(
        mergeMap((documentJoint: HttpResponse<IDocumentJoint>) => {
          if (documentJoint.body) {
            return of(documentJoint.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default documentJointResolve;
