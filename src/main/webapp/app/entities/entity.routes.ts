import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'conexaoSolidariaApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'eventos',
    data: { pageTitle: 'conexaoSolidariaApp.eventos.home.title' },
    loadChildren: () => import('./eventos/eventos.routes'),
  },
  {
    path: 'inscricao',
    data: { pageTitle: 'conexaoSolidariaApp.inscricao.home.title' },
    loadChildren: () => import('./inscricao/inscricao.routes'),
  },
  {
    path: 'avaliacao',
    data: { pageTitle: 'conexaoSolidariaApp.avaliacao.home.title' },
    loadChildren: () => import('./avaliacao/avaliacao.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
