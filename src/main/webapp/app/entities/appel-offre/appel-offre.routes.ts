import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import AppelOffreResolve from './route/appel-offre-routing-resolve.service';

const appelOffreRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/appel-offre.component').then(m => m.AppelOffreComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/appel-offre-detail.component').then(m => m.AppelOffreDetailComponent),
    resolve: {
      appelOffre: AppelOffreResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/appel-offre-update.component').then(m => m.AppelOffreUpdateComponent),
    resolve: {
      appelOffre: AppelOffreResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/appel-offre-update.component').then(m => m.AppelOffreUpdateComponent),
    resolve: {
      appelOffre: AppelOffreResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default appelOffreRoute;
