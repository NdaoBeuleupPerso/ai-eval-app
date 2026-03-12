import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CandidatResolve from './route/candidat-routing-resolve.service';

const candidatRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/candidat.component').then(m => m.CandidatComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/candidat-detail.component').then(m => m.CandidatDetailComponent),
    resolve: {
      candidat: CandidatResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/candidat-update.component').then(m => m.CandidatUpdateComponent),
    resolve: {
      candidat: CandidatResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/candidat-update.component').then(m => m.CandidatUpdateComponent),
    resolve: {
      candidat: CandidatResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default candidatRoute;
