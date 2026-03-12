import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICandidat, NewCandidat } from '../candidat.model';

export type PartialUpdateCandidat = Partial<ICandidat> & Pick<ICandidat, 'id'>;

export type EntityResponseType = HttpResponse<ICandidat>;
export type EntityArrayResponseType = HttpResponse<ICandidat[]>;

@Injectable({ providedIn: 'root' })
export class CandidatService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/candidats');

  create(candidat: NewCandidat): Observable<EntityResponseType> {
    return this.http.post<ICandidat>(this.resourceUrl, candidat, { observe: 'response' });
  }

  update(candidat: ICandidat): Observable<EntityResponseType> {
    return this.http.put<ICandidat>(`${this.resourceUrl}/${this.getCandidatIdentifier(candidat)}`, candidat, { observe: 'response' });
  }

  partialUpdate(candidat: PartialUpdateCandidat): Observable<EntityResponseType> {
    return this.http.patch<ICandidat>(`${this.resourceUrl}/${this.getCandidatIdentifier(candidat)}`, candidat, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICandidat>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICandidat[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCandidatIdentifier(candidat: Pick<ICandidat, 'id'>): number {
    return candidat.id;
  }

  compareCandidat(o1: Pick<ICandidat, 'id'> | null, o2: Pick<ICandidat, 'id'> | null): boolean {
    return o1 && o2 ? this.getCandidatIdentifier(o1) === this.getCandidatIdentifier(o2) : o1 === o2;
  }

  addCandidatToCollectionIfMissing<Type extends Pick<ICandidat, 'id'>>(
    candidatCollection: Type[],
    ...candidatsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const candidats: Type[] = candidatsToCheck.filter(isPresent);
    if (candidats.length > 0) {
      const candidatCollectionIdentifiers = candidatCollection.map(candidatItem => this.getCandidatIdentifier(candidatItem));
      const candidatsToAdd = candidats.filter(candidatItem => {
        const candidatIdentifier = this.getCandidatIdentifier(candidatItem);
        if (candidatCollectionIdentifiers.includes(candidatIdentifier)) {
          return false;
        }
        candidatCollectionIdentifiers.push(candidatIdentifier);
        return true;
      });
      return [...candidatsToAdd, ...candidatCollection];
    }
    return candidatCollection;
  }
}
