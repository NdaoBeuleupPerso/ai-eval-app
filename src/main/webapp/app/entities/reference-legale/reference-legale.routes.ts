import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import ReferenceLegaleResolve from './route/reference-legale-routing-resolve.service';

const referenceLegaleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/reference-legale.component').then(m => m.ReferenceLegaleComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/reference-legale-detail.component').then(m => m.ReferenceLegaleDetailComponent),
    resolve: {
      referenceLegale: ReferenceLegaleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/reference-legale-update.component').then(m => m.ReferenceLegaleUpdateComponent),
    resolve: {
      referenceLegale: ReferenceLegaleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/reference-legale-update.component').then(m => m.ReferenceLegaleUpdateComponent),
    resolve: {
      referenceLegale: ReferenceLegaleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default referenceLegaleRoute;
