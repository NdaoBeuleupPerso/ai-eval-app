import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAppelOffre, NewAppelOffre } from '../appel-offre.model';

export type PartialUpdateAppelOffre = Partial<IAppelOffre> & Pick<IAppelOffre, 'id'>;

type RestOf<T extends IAppelOffre | NewAppelOffre> = Omit<T, 'dateCloture'> & {
  dateCloture?: string | null;
};

export type RestAppelOffre = RestOf<IAppelOffre>;
export type NewRestAppelOffre = RestOf<NewAppelOffre>;
export type PartialUpdateRestAppelOffre = RestOf<PartialUpdateAppelOffre>;

export type EntityResponseType = HttpResponse<IAppelOffre>;
export type EntityArrayResponseType = HttpResponse<IAppelOffre[]>;

@Injectable({ providedIn: 'root' })
export class AppelOffreService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/appel-offres');

  create(appelOffre: NewAppelOffre): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(appelOffre);
    return this.http
      .post<RestAppelOffre>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(appelOffre: IAppelOffre): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(appelOffre);
    return this.http
      .put<RestAppelOffre>(`${this.resourceUrl}/${this.getAppelOffreIdentifier(appelOffre)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  evaluerTout(id: number): Observable<HttpResponse<{}>> {
    return this.http.post(`${this.resourceUrl}/${id}/evaluer-tout`, {}, { observe: 'response' });
  }

  partialUpdate(appelOffre: PartialUpdateAppelOffre): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(appelOffre);
    return this.http
      .patch<RestAppelOffre>(`${this.resourceUrl}/${this.getAppelOffreIdentifier(appelOffre)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestAppelOffre>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAppelOffre[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAppelOffreIdentifier(appelOffre: Pick<IAppelOffre, 'id'>): number {
    return appelOffre.id;
  }

  compareAppelOffre(o1: Pick<IAppelOffre, 'id'> | null, o2: Pick<IAppelOffre, 'id'> | null): boolean {
    return o1 && o2 ? this.getAppelOffreIdentifier(o1) === this.getAppelOffreIdentifier(o2) : o1 === o2;
  }

  addAppelOffreToCollectionIfMissing<Type extends Pick<IAppelOffre, 'id'>>(
    appelOffreCollection: Type[],
    ...appelOffresToCheck: (Type | null | undefined)[]
  ): Type[] {
    const appelOffres: Type[] = appelOffresToCheck.filter(isPresent);
    if (appelOffres.length > 0) {
      const appelOffreCollectionIdentifiers = appelOffreCollection.map(item => this.getAppelOffreIdentifier(item));
      const appelOffresToAdd = appelOffres.filter(item => {
        const identifier = this.getAppelOffreIdentifier(item);
        if (appelOffreCollectionIdentifiers.includes(identifier)) {
          return false;
        }
        appelOffreCollectionIdentifiers.push(identifier);
        return true;
      });
      return [...appelOffresToAdd, ...appelOffreCollection];
    }
    return appelOffreCollection;
  }

  protected convertDateFromClient<T extends IAppelOffre | NewAppelOffre | PartialUpdateAppelOffre>(appelOffre: T): RestOf<T> {
    return {
      ...appelOffre,
      dateCloture: appelOffre.dateCloture?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restAppelOffre: RestAppelOffre): IAppelOffre {
    return {
      ...restAppelOffre,
      dateCloture: restAppelOffre.dateCloture ? dayjs(restAppelOffre.dateCloture) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestAppelOffre>): HttpResponse<IAppelOffre> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestAppelOffre[]>): HttpResponse<IAppelOffre[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
