import { UserRouteAccessService } from '../../core/auth/user-route-access.service';
import { Routes } from '@angular/router';
import { ASC } from '../../config/navigation.constants';
import evaluationResolve from './route/evaluation-routing-resolve.service';

const evaluationRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/evaluation.component').then(m => m.EvaluationComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  // --- NOUVELLE ROUTE POUR LA RÉVISION À DEUX COLONNES ---
  {
    path: 'appel-offre/:id/review',
    loadComponent: () => import('../../evaluation-review/evaluation-review.component').then(m => m.EvaluationReviewComponent),
    canActivate: [UserRouteAccessService],
  },
  // -------------------------------------------------------
  {
    path: ':id/view',
    loadComponent: () => import('./detail/evaluation-detail.component').then(m => m.EvaluationDetailComponent),
    resolve: {
      evaluation: evaluationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/evaluation-update.component').then(m => m.EvaluationUpdateComponent),
    resolve: {
      evaluation: evaluationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/evaluation-update.component').then(m => m.EvaluationUpdateComponent),
    resolve: {
      evaluation: evaluationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default evaluationRoute;
