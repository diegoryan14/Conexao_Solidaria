import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IInscricao } from '../inscricao.model';
import { InscricaoService } from '../service/inscricao.service';

const inscricaoResolve = (route: ActivatedRouteSnapshot): Observable<null | IInscricao> => {
  const id = route.params['id'];
  if (id) {
    return inject(InscricaoService)
      .find(id)
      .pipe(
        mergeMap((inscricao: HttpResponse<IInscricao>) => {
          if (inscricao.body) {
            return of(inscricao.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default inscricaoResolve;
