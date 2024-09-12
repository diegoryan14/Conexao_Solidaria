import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { EventosComponent } from './list/eventos.component';
import { EventosDetailComponent } from './detail/eventos-detail.component';
import { EventosUpdateComponent } from './update/eventos-update.component';
import EventosResolve from './route/eventos-routing-resolve.service';

const eventosRoute: Routes = [
  {
    path: '',
    component: EventosComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EventosDetailComponent,
    resolve: {
      eventos: EventosResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EventosUpdateComponent,
    resolve: {
      eventos: EventosResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EventosUpdateComponent,
    resolve: {
      eventos: EventosResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default eventosRoute;
