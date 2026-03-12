import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ICritere } from '../critere.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../critere.test-samples';

import { CritereService } from './critere.service';

const requireRestSample: ICritere = {
  ...sampleWithRequiredData,
};

describe('Critere Service', () => {
  let service: CritereService;
  let httpMock: HttpTestingController;
  let expectedResult: ICritere | ICritere[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CritereService);
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

    it('should create a Critere', () => {
      const critere = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(critere).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Critere', () => {
      const critere = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(critere).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Critere', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Critere', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Critere', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCritereToCollectionIfMissing', () => {
      it('should add a Critere to an empty array', () => {
        const critere: ICritere = sampleWithRequiredData;
        expectedResult = service.addCritereToCollectionIfMissing([], critere);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(critere);
      });

      it('should not add a Critere to an array that contains it', () => {
        const critere: ICritere = sampleWithRequiredData;
        const critereCollection: ICritere[] = [
          {
            ...critere,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCritereToCollectionIfMissing(critereCollection, critere);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Critere to an array that doesn't contain it", () => {
        const critere: ICritere = sampleWithRequiredData;
        const critereCollection: ICritere[] = [sampleWithPartialData];
        expectedResult = service.addCritereToCollectionIfMissing(critereCollection, critere);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(critere);
      });

      it('should add only unique Critere to an array', () => {
        const critereArray: ICritere[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const critereCollection: ICritere[] = [sampleWithRequiredData];
        expectedResult = service.addCritereToCollectionIfMissing(critereCollection, ...critereArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const critere: ICritere = sampleWithRequiredData;
        const critere2: ICritere = sampleWithPartialData;
        expectedResult = service.addCritereToCollectionIfMissing([], critere, critere2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(critere);
        expect(expectedResult).toContain(critere2);
      });

      it('should accept null and undefined values', () => {
        const critere: ICritere = sampleWithRequiredData;
        expectedResult = service.addCritereToCollectionIfMissing([], null, critere, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(critere);
      });

      it('should return initial array if no Critere is added', () => {
        const critereCollection: ICritere[] = [sampleWithRequiredData];
        expectedResult = service.addCritereToCollectionIfMissing(critereCollection, undefined, null);
        expect(expectedResult).toEqual(critereCollection);
      });
    });

    describe('compareCritere', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCritere(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 18878 };
        const entity2 = null;

        const compareResult1 = service.compareCritere(entity1, entity2);
        const compareResult2 = service.compareCritere(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 18878 };
        const entity2 = { id: 21343 };

        const compareResult1 = service.compareCritere(entity1, entity2);
        const compareResult2 = service.compareCritere(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 18878 };
        const entity2 = { id: 18878 };

        const compareResult1 = service.compareCritere(entity1, entity2);
        const compareResult2 = service.compareCritere(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
