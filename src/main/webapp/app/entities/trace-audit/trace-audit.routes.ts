import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TraceAuditResolve from './route/trace-audit-routing-resolve.service';

const traceAuditRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/trace-audit.component').then(m => m.TraceAuditComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/trace-audit-detail.component').then(m => m.TraceAuditDetailComponent),
    resolve: {
      traceAudit: TraceAuditResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/trace-audit-update.component').then(m => m.TraceAuditUpdateComponent),
    resolve: {
      traceAudit: TraceAuditResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/trace-audit-update.component').then(m => m.TraceAuditUpdateComponent),
    resolve: {
      traceAudit: TraceAuditResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default traceAuditRoute;
