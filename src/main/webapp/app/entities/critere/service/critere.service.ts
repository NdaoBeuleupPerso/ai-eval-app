import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICritere, NewCritere } from '../critere.model';

export type PartialUpdateCritere = Partial<ICritere> & Pick<ICritere, 'id'>;

export type EntityResponseType = HttpResponse<ICritere>;
export type EntityArrayResponseType = HttpResponse<ICritere[]>;

@Injectable({ providedIn: 'root' })
export class CritereService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/criteres');

  create(critere: NewCritere): Observable<EntityResponseType> {
    return this.http.post<ICritere>(this.resourceUrl, critere, { observe: 'response' });
  }

  update(critere: ICritere): Observable<EntityResponseType> {
    return this.http.put<ICritere>(`${this.resourceUrl}/${this.getCritereIdentifier(critere)}`, critere, { observe: 'response' });
  }

  partialUpdate(critere: PartialUpdateCritere): Observable<EntityResponseType> {
    return this.http.patch<ICritere>(`${this.resourceUrl}/${this.getCritereIdentifier(critere)}`, critere, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICritere>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICritere[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCritereIdentifier(critere: Pick<ICritere, 'id'>): number {
    return critere.id;
  }

  compareCritere(o1: Pick<ICritere, 'id'> | null, o2: Pick<ICritere, 'id'> | null): boolean {
    return o1 && o2 ? this.getCritereIdentifier(o1) === this.getCritereIdentifier(o2) : o1 === o2;
  }

  addCritereToCollectionIfMissing<Type extends Pick<ICritere, 'id'>>(
    critereCollection: Type[],
    ...criteresToCheck: (Type | null | undefined)[]
  ): Type[] {
    const criteres: Type[] = criteresToCheck.filter(isPresent);
    if (criteres.length > 0) {
      const critereCollectionIdentifiers = critereCollection.map(critereItem => this.getCritereIdentifier(critereItem));
      const criteresToAdd = criteres.filter(critereItem => {
        const critereIdentifier = this.getCritereIdentifier(critereItem);
        if (critereCollectionIdentifiers.includes(critereIdentifier)) {
          return false;
        }
        critereCollectionIdentifiers.push(critereIdentifier);
        return true;
      });
      return [...criteresToAdd, ...critereCollection];
    }
    return critereCollection;
  }
}
