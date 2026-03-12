import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISoumission, NewSoumission } from '../soumission.model';

export type PartialUpdateSoumission = Partial<ISoumission> & Pick<ISoumission, 'id'>;

type RestOf<T extends ISoumission | NewSoumission> = Omit<T, 'dateSoumission'> & {
  dateSoumission?: string | null;
};

export type RestSoumission = RestOf<ISoumission>;

export type NewRestSoumission = RestOf<NewSoumission>;

export type PartialUpdateRestSoumission = RestOf<PartialUpdateSoumission>;

export type EntityResponseType = HttpResponse<ISoumission>;
export type EntityArrayResponseType = HttpResponse<ISoumission[]>;

@Injectable({ providedIn: 'root' })
export class SoumissionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/soumissions');

  create(soumission: NewSoumission): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(soumission);
    return this.http
      .post<RestSoumission>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(soumission: ISoumission): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(soumission);
    return this.http
      .put<RestSoumission>(`${this.resourceUrl}/${this.getSoumissionIdentifier(soumission)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(soumission: PartialUpdateSoumission): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(soumission);
    return this.http
      .patch<RestSoumission>(`${this.resourceUrl}/${this.getSoumissionIdentifier(soumission)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSoumission>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSoumission[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSoumissionIdentifier(soumission: Pick<ISoumission, 'id'>): number {
    return soumission.id;
  }

  compareSoumission(o1: Pick<ISoumission, 'id'> | null, o2: Pick<ISoumission, 'id'> | null): boolean {
    return o1 && o2 ? this.getSoumissionIdentifier(o1) === this.getSoumissionIdentifier(o2) : o1 === o2;
  }

  addSoumissionToCollectionIfMissing<Type extends Pick<ISoumission, 'id'>>(
    soumissionCollection: Type[],
    ...soumissionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const soumissions: Type[] = soumissionsToCheck.filter(isPresent);
    if (soumissions.length > 0) {
      const soumissionCollectionIdentifiers = soumissionCollection.map(soumissionItem => this.getSoumissionIdentifier(soumissionItem));
      const soumissionsToAdd = soumissions.filter(soumissionItem => {
        const soumissionIdentifier = this.getSoumissionIdentifier(soumissionItem);
        if (soumissionCollectionIdentifiers.includes(soumissionIdentifier)) {
          return false;
        }
        soumissionCollectionIdentifiers.push(soumissionIdentifier);
        return true;
      });
      return [...soumissionsToAdd, ...soumissionCollection];
    }
    return soumissionCollection;
  }

  protected convertDateFromClient<T extends ISoumission | NewSoumission | PartialUpdateSoumission>(soumission: T): RestOf<T> {
    return {
      ...soumission,
      dateSoumission: soumission.dateSoumission?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restSoumission: RestSoumission): ISoumission {
    return {
      ...restSoumission,
      dateSoumission: restSoumission.dateSoumission ? dayjs(restSoumission.dateSoumission) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSoumission>): HttpResponse<ISoumission> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSoumission[]>): HttpResponse<ISoumission[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
