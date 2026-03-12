import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDocumentJoint, NewDocumentJoint } from '../document-joint.model';

export type PartialUpdateDocumentJoint = Partial<IDocumentJoint> & Pick<IDocumentJoint, 'id'>;

export type EntityResponseType = HttpResponse<IDocumentJoint>;
export type EntityArrayResponseType = HttpResponse<IDocumentJoint[]>;

@Injectable({ providedIn: 'root' })
export class DocumentJointService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/document-joints');

  create(documentJoint: NewDocumentJoint): Observable<EntityResponseType> {
    return this.http.post<IDocumentJoint>(this.resourceUrl, documentJoint, { observe: 'response' });
  }

  update(documentJoint: IDocumentJoint): Observable<EntityResponseType> {
    return this.http.put<IDocumentJoint>(`${this.resourceUrl}/${this.getDocumentJointIdentifier(documentJoint)}`, documentJoint, {
      observe: 'response',
    });
  }

  partialUpdate(documentJoint: PartialUpdateDocumentJoint): Observable<EntityResponseType> {
    return this.http.patch<IDocumentJoint>(`${this.resourceUrl}/${this.getDocumentJointIdentifier(documentJoint)}`, documentJoint, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDocumentJoint>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDocumentJoint[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDocumentJointIdentifier(documentJoint: Pick<IDocumentJoint, 'id'>): number {
    return documentJoint.id;
  }

  compareDocumentJoint(o1: Pick<IDocumentJoint, 'id'> | null, o2: Pick<IDocumentJoint, 'id'> | null): boolean {
    return o1 && o2 ? this.getDocumentJointIdentifier(o1) === this.getDocumentJointIdentifier(o2) : o1 === o2;
  }

  addDocumentJointToCollectionIfMissing<Type extends Pick<IDocumentJoint, 'id'>>(
    documentJointCollection: Type[],
    ...documentJointsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const documentJoints: Type[] = documentJointsToCheck.filter(isPresent);
    if (documentJoints.length > 0) {
      const documentJointCollectionIdentifiers = documentJointCollection.map(documentJointItem =>
        this.getDocumentJointIdentifier(documentJointItem),
      );
      const documentJointsToAdd = documentJoints.filter(documentJointItem => {
        const documentJointIdentifier = this.getDocumentJointIdentifier(documentJointItem);
        if (documentJointCollectionIdentifiers.includes(documentJointIdentifier)) {
          return false;
        }
        documentJointCollectionIdentifiers.push(documentJointIdentifier);
        return true;
      });
      return [...documentJointsToAdd, ...documentJointCollection];
    }
    return documentJointCollection;
  }
}
