import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITraceAudit } from '../trace-audit.model';
import { TraceAuditService } from '../service/trace-audit.service';

const traceAuditResolve = (route: ActivatedRouteSnapshot): Observable<null | ITraceAudit> => {
  const id = route.params.id;
  if (id) {
    return inject(TraceAuditService)
      .find(id)
      .pipe(
        mergeMap((traceAudit: HttpResponse<ITraceAudit>) => {
          if (traceAudit.body) {
            return of(traceAudit.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default traceAuditResolve;
