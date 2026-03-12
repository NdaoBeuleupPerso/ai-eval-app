import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITraceAudit, NewTraceAudit } from '../trace-audit.model';

export type PartialUpdateTraceAudit = Partial<ITraceAudit> & Pick<ITraceAudit, 'id'>;

type RestOf<T extends ITraceAudit | NewTraceAudit> = Omit<T, 'horodatage'> & {
  horodatage?: string | null;
};

export type RestTraceAudit = RestOf<ITraceAudit>;

export type NewRestTraceAudit = RestOf<NewTraceAudit>;

export type PartialUpdateRestTraceAudit = RestOf<PartialUpdateTraceAudit>;

export type EntityResponseType = HttpResponse<ITraceAudit>;
export type EntityArrayResponseType = HttpResponse<ITraceAudit[]>;

@Injectable({ providedIn: 'root' })
export class TraceAuditService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/trace-audits');

  create(traceAudit: NewTraceAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(traceAudit);
    return this.http
      .post<RestTraceAudit>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(traceAudit: ITraceAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(traceAudit);
    return this.http
      .put<RestTraceAudit>(`${this.resourceUrl}/${this.getTraceAuditIdentifier(traceAudit)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(traceAudit: PartialUpdateTraceAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(traceAudit);
    return this.http
      .patch<RestTraceAudit>(`${this.resourceUrl}/${this.getTraceAuditIdentifier(traceAudit)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTraceAudit>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTraceAudit[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTraceAuditIdentifier(traceAudit: Pick<ITraceAudit, 'id'>): number {
    return traceAudit.id;
  }

  compareTraceAudit(o1: Pick<ITraceAudit, 'id'> | null, o2: Pick<ITraceAudit, 'id'> | null): boolean {
    return o1 && o2 ? this.getTraceAuditIdentifier(o1) === this.getTraceAuditIdentifier(o2) : o1 === o2;
  }

  addTraceAuditToCollectionIfMissing<Type extends Pick<ITraceAudit, 'id'>>(
    traceAuditCollection: Type[],
    ...traceAuditsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const traceAudits: Type[] = traceAuditsToCheck.filter(isPresent);
    if (traceAudits.length > 0) {
      const traceAuditCollectionIdentifiers = traceAuditCollection.map(traceAuditItem => this.getTraceAuditIdentifier(traceAuditItem));
      const traceAuditsToAdd = traceAudits.filter(traceAuditItem => {
        const traceAuditIdentifier = this.getTraceAuditIdentifier(traceAuditItem);
        if (traceAuditCollectionIdentifiers.includes(traceAuditIdentifier)) {
          return false;
        }
        traceAuditCollectionIdentifiers.push(traceAuditIdentifier);
        return true;
      });
      return [...traceAuditsToAdd, ...traceAuditCollection];
    }
    return traceAuditCollection;
  }

  protected convertDateFromClient<T extends ITraceAudit | NewTraceAudit | PartialUpdateTraceAudit>(traceAudit: T): RestOf<T> {
    return {
      ...traceAudit,
      horodatage: traceAudit.horodatage?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTraceAudit: RestTraceAudit): ITraceAudit {
    return {
      ...restTraceAudit,
      horodatage: restTraceAudit.horodatage ? dayjs(restTraceAudit.horodatage) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTraceAudit>): HttpResponse<ITraceAudit> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTraceAudit[]>): HttpResponse<ITraceAudit[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
