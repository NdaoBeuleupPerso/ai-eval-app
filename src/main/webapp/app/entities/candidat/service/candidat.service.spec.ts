import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ICandidat } from '../candidat.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../candidat.test-samples';

import { CandidatService } from './candidat.service';

const requireRestSample: ICandidat = {
  ...sampleWithRequiredData,
};

describe('Candidat Service', () => {
  let service: CandidatService;
  let httpMock: HttpTestingController;
  let expectedResult: ICandidat | ICandidat[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CandidatService);
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

    it('should create a Candidat', () => {
      const candidat = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(candidat).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Candidat', () => {
      const candidat = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(candidat).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Candidat', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Candidat', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Candidat', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCandidatToCollectionIfMissing', () => {
      it('should add a Candidat to an empty array', () => {
        const candidat: ICandidat = sampleWithRequiredData;
        expectedResult = service.addCandidatToCollectionIfMissing([], candidat);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(candidat);
      });

      it('should not add a Candidat to an array that contains it', () => {
        const candidat: ICandidat = sampleWithRequiredData;
        const candidatCollection: ICandidat[] = [
          {
            ...candidat,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCandidatToCollectionIfMissing(candidatCollection, candidat);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Candidat to an array that doesn't contain it", () => {
        const candidat: ICandidat = sampleWithRequiredData;
        const candidatCollection: ICandidat[] = [sampleWithPartialData];
        expectedResult = service.addCandidatToCollectionIfMissing(candidatCollection, candidat);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(candidat);
      });

      it('should add only unique Candidat to an array', () => {
        const candidatArray: ICandidat[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const candidatCollection: ICandidat[] = [sampleWithRequiredData];
        expectedResult = service.addCandidatToCollectionIfMissing(candidatCollection, ...candidatArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const candidat: ICandidat = sampleWithRequiredData;
        const candidat2: ICandidat = sampleWithPartialData;
        expectedResult = service.addCandidatToCollectionIfMissing([], candidat, candidat2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(candidat);
        expect(expectedResult).toContain(candidat2);
      });

      it('should accept null and undefined values', () => {
        const candidat: ICandidat = sampleWithRequiredData;
        expectedResult = service.addCandidatToCollectionIfMissing([], null, candidat, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(candidat);
      });

      it('should return initial array if no Candidat is added', () => {
        const candidatCollection: ICandidat[] = [sampleWithRequiredData];
        expectedResult = service.addCandidatToCollectionIfMissing(candidatCollection, undefined, null);
        expect(expectedResult).toEqual(candidatCollection);
      });
    });

    describe('compareCandidat', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCandidat(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 29649 };
        const entity2 = null;

        const compareResult1 = service.compareCandidat(entity1, entity2);
        const compareResult2 = service.compareCandidat(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 29649 };
        const entity2 = { id: 17830 };

        const compareResult1 = service.compareCandidat(entity1, entity2);
        const compareResult2 = service.compareCandidat(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 29649 };
        const entity2 = { id: 29649 };

        const compareResult1 = service.compareCandidat(entity1, entity2);
        const compareResult2 = service.compareCandidat(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
