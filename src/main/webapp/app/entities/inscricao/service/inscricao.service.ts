import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IInscricao, NewInscricao } from '../inscricao.model';

export type PartialUpdateInscricao = Partial<IInscricao> & Pick<IInscricao, 'id'>;

type RestOf<T extends IInscricao | NewInscricao> = Omit<T, 'data'> & {
  data?: string | null;
};

export type RestInscricao = RestOf<IInscricao>;

export type NewRestInscricao = RestOf<NewInscricao>;

export type PartialUpdateRestInscricao = RestOf<PartialUpdateInscricao>;

export type EntityResponseType = HttpResponse<IInscricao>;
export type EntityArrayResponseType = HttpResponse<IInscricao[]>;

@Injectable({ providedIn: 'root' })
export class InscricaoService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/inscricaos');

  create(inscricao: NewInscricao): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(inscricao);
    return this.http
      .post<RestInscricao>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(inscricao: IInscricao): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(inscricao);
    return this.http
      .put<RestInscricao>(`${this.resourceUrl}/${this.getInscricaoIdentifier(inscricao)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(inscricao: PartialUpdateInscricao): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(inscricao);
    return this.http
      .patch<RestInscricao>(`${this.resourceUrl}/${this.getInscricaoIdentifier(inscricao)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestInscricao>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestInscricao[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getInscricaoIdentifier(inscricao: Pick<IInscricao, 'id'>): number {
    return inscricao.id;
  }

  compareInscricao(o1: Pick<IInscricao, 'id'> | null, o2: Pick<IInscricao, 'id'> | null): boolean {
    return o1 && o2 ? this.getInscricaoIdentifier(o1) === this.getInscricaoIdentifier(o2) : o1 === o2;
  }

  addInscricaoToCollectionIfMissing<Type extends Pick<IInscricao, 'id'>>(
    inscricaoCollection: Type[],
    ...inscricaosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const inscricaos: Type[] = inscricaosToCheck.filter(isPresent);
    if (inscricaos.length > 0) {
      const inscricaoCollectionIdentifiers = inscricaoCollection.map(inscricaoItem => this.getInscricaoIdentifier(inscricaoItem));
      const inscricaosToAdd = inscricaos.filter(inscricaoItem => {
        const inscricaoIdentifier = this.getInscricaoIdentifier(inscricaoItem);
        if (inscricaoCollectionIdentifiers.includes(inscricaoIdentifier)) {
          return false;
        }
        inscricaoCollectionIdentifiers.push(inscricaoIdentifier);
        return true;
      });
      return [...inscricaosToAdd, ...inscricaoCollection];
    }
    return inscricaoCollection;
  }

  protected convertDateFromClient<T extends IInscricao | NewInscricao | PartialUpdateInscricao>(inscricao: T): RestOf<T> {
    return {
      ...inscricao,
      data: inscricao.data?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restInscricao: RestInscricao): IInscricao {
    return {
      ...restInscricao,
      data: restInscricao.data ? dayjs(restInscricao.data) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestInscricao>): HttpResponse<IInscricao> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestInscricao[]>): HttpResponse<IInscricao[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
