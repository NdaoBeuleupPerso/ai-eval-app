import { Routes } from '@angular/router';
import { SoumissionnaireDashboardComponent } from './soumissionnaire-dashboard.component';
import { Authority } from 'app/config/authority.constants';

const routes: Routes = [
  {
    path: '',
    component: SoumissionnaireDashboardComponent,
    data: {
      pageTitle: 'Espace Soumissionnaire',
      authorities: [Authority.SOUMISSIONNAIRE],
    },
  },
];

export default routes;
