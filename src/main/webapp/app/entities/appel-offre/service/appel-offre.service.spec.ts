import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IAppelOffre } from '../appel-offre.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../appel-offre.test-samples';

import { AppelOffreService, RestAppelOffre } from './appel-offre.service';

const requireRestSample: RestAppelOffre = {
  ...sampleWithRequiredData,
  dateCloture: sampleWithRequiredData.dateCloture?.toJSON(),
};

describe('AppelOffre Service', () => {
  let service: AppelOffreService;
  let httpMock: HttpTestingController;
  let expectedResult: IAppelOffre | IAppelOffre[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AppelOffreService);
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

    it('should create a AppelOffre', () => {
      const appelOffre = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(appelOffre).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AppelOffre', () => {
      const appelOffre = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(appelOffre).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a AppelOffre', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AppelOffre', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a AppelOffre', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addAppelOffreToCollectionIfMissing', () => {
      it('should add a AppelOffre to an empty array', () => {
        const appelOffre: IAppelOffre = sampleWithRequiredData;
        expectedResult = service.addAppelOffreToCollectionIfMissing([], appelOffre);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(appelOffre);
      });

      it('should not add a AppelOffre to an array that contains it', () => {
        const appelOffre: IAppelOffre = sampleWithRequiredData;
        const appelOffreCollection: IAppelOffre[] = [
          {
            ...appelOffre,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAppelOffreToCollectionIfMissing(appelOffreCollection, appelOffre);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AppelOffre to an array that doesn't contain it", () => {
        const appelOffre: IAppelOffre = sampleWithRequiredData;
        const appelOffreCollection: IAppelOffre[] = [sampleWithPartialData];
        expectedResult = service.addAppelOffreToCollectionIfMissing(appelOffreCollection, appelOffre);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(appelOffre);
      });

      it('should add only unique AppelOffre to an array', () => {
        const appelOffreArray: IAppelOffre[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const appelOffreCollection: IAppelOffre[] = [sampleWithRequiredData];
        expectedResult = service.addAppelOffreToCollectionIfMissing(appelOffreCollection, ...appelOffreArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const appelOffre: IAppelOffre = sampleWithRequiredData;
        const appelOffre2: IAppelOffre = sampleWithPartialData;
        expectedResult = service.addAppelOffreToCollectionIfMissing([], appelOffre, appelOffre2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(appelOffre);
        expect(expectedResult).toContain(appelOffre2);
      });

      it('should accept null and undefined values', () => {
        const appelOffre: IAppelOffre = sampleWithRequiredData;
        expectedResult = service.addAppelOffreToCollectionIfMissing([], null, appelOffre, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(appelOffre);
      });

      it('should return initial array if no AppelOffre is added', () => {
        const appelOffreCollection: IAppelOffre[] = [sampleWithRequiredData];
        expectedResult = service.addAppelOffreToCollectionIfMissing(appelOffreCollection, undefined, null);
        expect(expectedResult).toEqual(appelOffreCollection);
      });
    });

    describe('compareAppelOffre', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAppelOffre(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 31506 };
        const entity2 = null;

        const compareResult1 = service.compareAppelOffre(entity1, entity2);
        const compareResult2 = service.compareAppelOffre(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 31506 };
        const entity2 = { id: 4012 };

        const compareResult1 = service.compareAppelOffre(entity1, entity2);
        const compareResult2 = service.compareAppelOffre(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 31506 };
        const entity2 = { id: 31506 };

        const compareResult1 = service.compareAppelOffre(entity1, entity2);
        const compareResult2 = service.compareAppelOffre(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
