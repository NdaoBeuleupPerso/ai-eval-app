import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'iaevalApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'appel-offre',
    data: { pageTitle: 'iaevalApp.appelOffre.home.title' },
    loadChildren: () => import('./appel-offre/appel-offre.routes'),
  },
  {
    path: 'critere',
    data: { pageTitle: 'iaevalApp.critere.home.title' },
    loadChildren: () => import('./critere/critere.routes'),
  },
  {
    path: 'candidat',
    data: { pageTitle: 'iaevalApp.candidat.home.title' },
    loadChildren: () => import('./candidat/candidat.routes'),
  },
  {
    path: 'soumission',
    data: { pageTitle: 'iaevalApp.soumission.home.title' },
    loadChildren: () => import('./soumission/soumission.routes'),
  },
  {
    path: 'document-joint',
    data: { pageTitle: 'iaevalApp.documentJoint.home.title' },
    loadChildren: () => import('./document-joint/document-joint.routes'),
  },
  {
    path: 'evaluation',
    data: { pageTitle: 'iaevalApp.evaluation.home.title' },
    loadChildren: () => import('./evaluation/evaluation.routes'),
  },
  {
    path: 'reference-legale',
    data: { pageTitle: 'iaevalApp.referenceLegale.home.title' },
    loadChildren: () => import('./reference-legale/reference-legale.routes'),
  },
  {
    path: 'trace-audit',
    data: { pageTitle: 'iaevalApp.traceAudit.home.title' },
    loadChildren: () => import('./trace-audit/trace-audit.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
