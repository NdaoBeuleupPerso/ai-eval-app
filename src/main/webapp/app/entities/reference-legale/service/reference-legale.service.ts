import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IReferenceLegale, NewReferenceLegale } from '../reference-legale.model';

export type PartialUpdateReferenceLegale = Partial<IReferenceLegale> & Pick<IReferenceLegale, 'id'>;

export type EntityResponseType = HttpResponse<IReferenceLegale>;
export type EntityArrayResponseType = HttpResponse<IReferenceLegale[]>;

@Injectable({ providedIn: 'root' })
export class ReferenceLegaleService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/reference-legales');

  create(referenceLegale: NewReferenceLegale): Observable<EntityResponseType> {
    return this.http.post<IReferenceLegale>(this.resourceUrl, referenceLegale, { observe: 'response' });
  }

  update(referenceLegale: IReferenceLegale): Observable<EntityResponseType> {
    return this.http.put<IReferenceLegale>(`${this.resourceUrl}/${this.getReferenceLegaleIdentifier(referenceLegale)}`, referenceLegale, {
      observe: 'response',
    });
  }

  partialUpdate(referenceLegale: PartialUpdateReferenceLegale): Observable<EntityResponseType> {
    return this.http.patch<IReferenceLegale>(`${this.resourceUrl}/${this.getReferenceLegaleIdentifier(referenceLegale)}`, referenceLegale, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IReferenceLegale>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IReferenceLegale[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getReferenceLegaleIdentifier(referenceLegale: Pick<IReferenceLegale, 'id'>): number {
    return referenceLegale.id;
  }

  compareReferenceLegale(o1: Pick<IReferenceLegale, 'id'> | null, o2: Pick<IReferenceLegale, 'id'> | null): boolean {
    return o1 && o2 ? this.getReferenceLegaleIdentifier(o1) === this.getReferenceLegaleIdentifier(o2) : o1 === o2;
  }

  addReferenceLegaleToCollectionIfMissing<Type extends Pick<IReferenceLegale, 'id'>>(
    referenceLegaleCollection: Type[],
    ...referenceLegalesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const referenceLegales: Type[] = referenceLegalesToCheck.filter(isPresent);
    if (referenceLegales.length > 0) {
      const referenceLegaleCollectionIdentifiers = referenceLegaleCollection.map(referenceLegaleItem =>
        this.getReferenceLegaleIdentifier(referenceLegaleItem),
      );
      const referenceLegalesToAdd = referenceLegales.filter(referenceLegaleItem => {
        const referenceLegaleIdentifier = this.getReferenceLegaleIdentifier(referenceLegaleItem);
        if (referenceLegaleCollectionIdentifiers.includes(referenceLegaleIdentifier)) {
          return false;
        }
        referenceLegaleCollectionIdentifiers.push(referenceLegaleIdentifier);
        return true;
      });
      return [...referenceLegalesToAdd, ...referenceLegaleCollection];
    }
    return referenceLegaleCollection;
  }
}
