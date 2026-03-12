import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import DocumentJointResolve from './route/document-joint-routing-resolve.service';

const documentJointRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/document-joint.component').then(m => m.DocumentJointComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/document-joint-detail.component').then(m => m.DocumentJointDetailComponent),
    resolve: {
      documentJoint: DocumentJointResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/document-joint-update.component').then(m => m.DocumentJointUpdateComponent),
    resolve: {
      documentJoint: DocumentJointResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/document-joint-update.component').then(m => m.DocumentJointUpdateComponent),
    resolve: {
      documentJoint: DocumentJointResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default documentJointRoute;
