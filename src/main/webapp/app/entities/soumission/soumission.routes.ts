import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SoumissionResolve from './route/soumission-routing-resolve.service';

const soumissionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/soumission.component').then(m => m.SoumissionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/soumission-detail.component').then(m => m.SoumissionDetailComponent),
    resolve: {
      soumission: SoumissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/soumission-update.component').then(m => m.SoumissionUpdateComponent),
    resolve: {
      soumission: SoumissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/soumission-update.component').then(m => m.SoumissionUpdateComponent),
    resolve: {
      soumission: SoumissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default soumissionRoute;
