import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import EvaluationResolve from './route/evaluation-routing-resolve.service';

const evaluationRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/evaluation.component').then(m => m.EvaluationComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/evaluation-detail.component').then(m => m.EvaluationDetailComponent),
    resolve: {
      evaluation: EvaluationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/evaluation-update.component').then(m => m.EvaluationUpdateComponent),
    resolve: {
      evaluation: EvaluationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/evaluation-update.component').then(m => m.EvaluationUpdateComponent),
    resolve: {
      evaluation: EvaluationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default evaluationRoute;
