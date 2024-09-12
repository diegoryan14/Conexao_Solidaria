import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { InscricaoComponent } from './list/inscricao.component';
import { InscricaoDetailComponent } from './detail/inscricao-detail.component';
import { InscricaoUpdateComponent } from './update/inscricao-update.component';
import InscricaoResolve from './route/inscricao-routing-resolve.service';

const inscricaoRoute: Routes = [
  {
    path: '',
    component: InscricaoComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: InscricaoDetailComponent,
    resolve: {
      inscricao: InscricaoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: InscricaoUpdateComponent,
    resolve: {
      inscricao: InscricaoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: InscricaoUpdateComponent,
    resolve: {
      inscricao: InscricaoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default inscricaoRoute;
