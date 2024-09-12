import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { AvaliacaoComponent } from './list/avaliacao.component';
import { AvaliacaoDetailComponent } from './detail/avaliacao-detail.component';
import { AvaliacaoUpdateComponent } from './update/avaliacao-update.component';
import AvaliacaoResolve from './route/avaliacao-routing-resolve.service';

const avaliacaoRoute: Routes = [
  {
    path: '',
    component: AvaliacaoComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AvaliacaoDetailComponent,
    resolve: {
      avaliacao: AvaliacaoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AvaliacaoUpdateComponent,
    resolve: {
      avaliacao: AvaliacaoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AvaliacaoUpdateComponent,
    resolve: {
      avaliacao: AvaliacaoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default avaliacaoRoute;
