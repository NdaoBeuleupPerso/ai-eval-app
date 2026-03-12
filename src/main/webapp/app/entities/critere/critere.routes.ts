import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CritereResolve from './route/critere-routing-resolve.service';

const critereRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/critere.component').then(m => m.CritereComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/critere-detail.component').then(m => m.CritereDetailComponent),
    resolve: {
      critere: CritereResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/critere-update.component').then(m => m.CritereUpdateComponent),
    resolve: {
      critere: CritereResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/critere-update.component').then(m => m.CritereUpdateComponent),
    resolve: {
      critere: CritereResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default critereRoute;
