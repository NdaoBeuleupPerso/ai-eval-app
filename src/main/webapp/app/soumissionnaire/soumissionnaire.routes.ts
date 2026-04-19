import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'dashboard',
    loadComponent: () => import('./soumissionnaire-dashboard.component').then(m => m.SoumissionnaireDashboardComponent),
    data: {
      pageTitle: 'iaevalApp.evaluation.home.title',
    },
  },
];

// CETTE LIGNE EST OBLIGATOIRE :
export default routes;
