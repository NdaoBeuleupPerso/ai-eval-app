import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ISoumission } from '../soumission.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../soumission.test-samples';

import { RestSoumission, SoumissionService } from './soumission.service';

const requireRestSample: RestSoumission = {
  ...sampleWithRequiredData,
  dateSoumission: sampleWithRequiredData.dateSoumission?.toJSON(),
};

describe('Soumission Service', () => {
  let service: SoumissionService;
  let httpMock: HttpTestingController;
  let expectedResult: ISoumission | ISoumission[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SoumissionService);
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

    it('should create a Soumission', () => {
      const soumission = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(soumission).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Soumission', () => {
      const soumission = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(soumission).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Soumission', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Soumission', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Soumission', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSoumissionToCollectionIfMissing', () => {
      it('should add a Soumission to an empty array', () => {
        const soumission: ISoumission = sampleWithRequiredData;
        expectedResult = service.addSoumissionToCollectionIfMissing([], soumission);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(soumission);
      });

      it('should not add a Soumission to an array that contains it', () => {
        const soumission: ISoumission = sampleWithRequiredData;
        const soumissionCollection: ISoumission[] = [
          {
            ...soumission,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSoumissionToCollectionIfMissing(soumissionCollection, soumission);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Soumission to an array that doesn't contain it", () => {
        const soumission: ISoumission = sampleWithRequiredData;
        const soumissionCollection: ISoumission[] = [sampleWithPartialData];
        expectedResult = service.addSoumissionToCollectionIfMissing(soumissionCollection, soumission);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(soumission);
      });

      it('should add only unique Soumission to an array', () => {
        const soumissionArray: ISoumission[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const soumissionCollection: ISoumission[] = [sampleWithRequiredData];
        expectedResult = service.addSoumissionToCollectionIfMissing(soumissionCollection, ...soumissionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const soumission: ISoumission = sampleWithRequiredData;
        const soumission2: ISoumission = sampleWithPartialData;
        expectedResult = service.addSoumissionToCollectionIfMissing([], soumission, soumission2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(soumission);
        expect(expectedResult).toContain(soumission2);
      });

      it('should accept null and undefined values', () => {
        const soumission: ISoumission = sampleWithRequiredData;
        expectedResult = service.addSoumissionToCollectionIfMissing([], null, soumission, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(soumission);
      });

      it('should return initial array if no Soumission is added', () => {
        const soumissionCollection: ISoumission[] = [sampleWithRequiredData];
        expectedResult = service.addSoumissionToCollectionIfMissing(soumissionCollection, undefined, null);
        expect(expectedResult).toEqual(soumissionCollection);
      });
    });

    describe('compareSoumission', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSoumission(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 4418 };
        const entity2 = null;

        const compareResult1 = service.compareSoumission(entity1, entity2);
        const compareResult2 = service.compareSoumission(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 4418 };
        const entity2 = { id: 18967 };

        const compareResult1 = service.compareSoumission(entity1, entity2);
        const compareResult2 = service.compareSoumission(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 4418 };
        const entity2 = { id: 4418 };

        const compareResult1 = service.compareSoumission(entity1, entity2);
        const compareResult2 = service.compareSoumission(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
