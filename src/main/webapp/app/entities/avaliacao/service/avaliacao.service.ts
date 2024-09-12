import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAvaliacao, NewAvaliacao } from '../avaliacao.model';

export type PartialUpdateAvaliacao = Partial<IAvaliacao> & Pick<IAvaliacao, 'id'>;

export type EntityResponseType = HttpResponse<IAvaliacao>;
export type EntityArrayResponseType = HttpResponse<IAvaliacao[]>;

@Injectable({ providedIn: 'root' })
export class AvaliacaoService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/avaliacaos');

  create(avaliacao: NewAvaliacao): Observable<EntityResponseType> {
    return this.http.post<IAvaliacao>(this.resourceUrl, avaliacao, { observe: 'response' });
  }

  update(avaliacao: IAvaliacao): Observable<EntityResponseType> {
    return this.http.put<IAvaliacao>(`${this.resourceUrl}/${this.getAvaliacaoIdentifier(avaliacao)}`, avaliacao, { observe: 'response' });
  }

  partialUpdate(avaliacao: PartialUpdateAvaliacao): Observable<EntityResponseType> {
    return this.http.patch<IAvaliacao>(`${this.resourceUrl}/${this.getAvaliacaoIdentifier(avaliacao)}`, avaliacao, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAvaliacao>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAvaliacao[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAvaliacaoIdentifier(avaliacao: Pick<IAvaliacao, 'id'>): number {
    return avaliacao.id;
  }

  compareAvaliacao(o1: Pick<IAvaliacao, 'id'> | null, o2: Pick<IAvaliacao, 'id'> | null): boolean {
    return o1 && o2 ? this.getAvaliacaoIdentifier(o1) === this.getAvaliacaoIdentifier(o2) : o1 === o2;
  }

  addAvaliacaoToCollectionIfMissing<Type extends Pick<IAvaliacao, 'id'>>(
    avaliacaoCollection: Type[],
    ...avaliacaosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const avaliacaos: Type[] = avaliacaosToCheck.filter(isPresent);
    if (avaliacaos.length > 0) {
      const avaliacaoCollectionIdentifiers = avaliacaoCollection.map(avaliacaoItem => this.getAvaliacaoIdentifier(avaliacaoItem));
      const avaliacaosToAdd = avaliacaos.filter(avaliacaoItem => {
        const avaliacaoIdentifier = this.getAvaliacaoIdentifier(avaliacaoItem);
        if (avaliacaoCollectionIdentifiers.includes(avaliacaoIdentifier)) {
          return false;
        }
        avaliacaoCollectionIdentifiers.push(avaliacaoIdentifier);
        return true;
      });
      return [...avaliacaosToAdd, ...avaliacaoCollection];
    }
    return avaliacaoCollection;
  }
}
