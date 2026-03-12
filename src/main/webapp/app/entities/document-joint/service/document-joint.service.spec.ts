import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IDocumentJoint } from '../document-joint.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../document-joint.test-samples';

import { DocumentJointService } from './document-joint.service';

const requireRestSample: IDocumentJoint = {
  ...sampleWithRequiredData,
};

describe('DocumentJoint Service', () => {
  let service: DocumentJointService;
  let httpMock: HttpTestingController;
  let expectedResult: IDocumentJoint | IDocumentJoint[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DocumentJointService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a DocumentJoint', () => {
      const documentJoint = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(documentJoint).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DocumentJoint', () => {
      const documentJoint = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(documentJoint).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DocumentJoint', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DocumentJoint', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DocumentJoint', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDocumentJointToCollectionIfMissing', () => {
      it('should add a DocumentJoint to an empty array', () => {
        const documentJoint: IDocumentJoint = sampleWithRequiredData;
        expectedResult = service.addDocumentJointToCollectionIfMissing([], documentJoint);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(documentJoint);
      });

      it('should not add a DocumentJoint to an array that contains it', () => {
        const documentJoint: IDocumentJoint = sampleWithRequiredData;
        const documentJointCollection: IDocumentJoint[] = [
          {
            ...documentJoint,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDocumentJointToCollectionIfMissing(documentJointCollection, documentJoint);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DocumentJoint to an array that doesn't contain it", () => {
        const documentJoint: IDocumentJoint = sampleWithRequiredData;
        const documentJointCollection: IDocumentJoint[] = [sampleWithPartialData];
        expectedResult = service.addDocumentJointToCollectionIfMissing(documentJointCollection, documentJoint);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(documentJoint);
      });

      it('should add only unique DocumentJoint to an array', () => {
        const documentJointArray: IDocumentJoint[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const documentJointCollection: IDocumentJoint[] = [sampleWithRequiredData];
        expectedResult = service.addDocumentJointToCollectionIfMissing(documentJointCollection, ...documentJointArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const documentJoint: IDocumentJoint = sampleWithRequiredData;
        const documentJoint2: IDocumentJoint = sampleWithPartialData;
        expectedResult = service.addDocumentJointToCollectionIfMissing([], documentJoint, documentJoint2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(documentJoint);
        expect(expectedResult).toContain(documentJoint2);
      });

      it('should accept null and undefined values', () => {
        const documentJoint: IDocumentJoint = sampleWithRequiredData;
        expectedResult = service.addDocumentJointToCollectionIfMissing([], null, documentJoint, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(documentJoint);
      });

      it('should return initial array if no DocumentJoint is added', () => {
        const documentJointCollection: IDocumentJoint[] = [sampleWithRequiredData];
        expectedResult = service.addDocumentJointToCollectionIfMissing(documentJointCollection, undefined, null);
        expect(expectedResult).toEqual(documentJointCollection);
      });
    });

    describe('compareDocumentJoint', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDocumentJoint(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 8654 };
        const entity2 = null;

        const compareResult1 = service.compareDocumentJoint(entity1, entity2);
        const compareResult2 = service.compareDocumentJoint(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 8654 };
        const entity2 = { id: 15336 };

        const compareResult1 = service.compareDocumentJoint(entity1, entity2);
        const compareResult2 = service.compareDocumentJoint(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 8654 };
        const entity2 = { id: 8654 };

        const compareResult1 = service.compareDocumentJoint(entity1, entity2);
        const compareResult2 = service.compareDocumentJoint(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
