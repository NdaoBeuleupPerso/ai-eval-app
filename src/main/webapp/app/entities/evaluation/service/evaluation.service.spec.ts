import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IEvaluation } from '../evaluation.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../evaluation.test-samples';

import { EvaluationService, RestEvaluation } from './evaluation.service';

const requireRestSample: RestEvaluation = {
  ...sampleWithRequiredData,
  dateEvaluation: sampleWithRequiredData.dateEvaluation?.toJSON(),
};

describe('Evaluation Service', () => {
  let service: EvaluationService;
  let httpMock: HttpTestingController;
  let expectedResult: IEvaluation | IEvaluation[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(EvaluationService);
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

    it('should create a Evaluation', () => {
      const evaluation = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(evaluation).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Evaluation', () => {
      const evaluation = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(evaluation).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Evaluation', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Evaluation', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Evaluation', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addEvaluationToCollectionIfMissing', () => {
      it('should add a Evaluation to an empty array', () => {
        const evaluation: IEvaluation = sampleWithRequiredData;
        expectedResult = service.addEvaluationToCollectionIfMissing([], evaluation);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(evaluation);
      });

      it('should not add a Evaluation to an array that contains it', () => {
        const evaluation: IEvaluation = sampleWithRequiredData;
        const evaluationCollection: IEvaluation[] = [
          {
            ...evaluation,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEvaluationToCollectionIfMissing(evaluationCollection, evaluation);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Evaluation to an array that doesn't contain it", () => {
        const evaluation: IEvaluation = sampleWithRequiredData;
        const evaluationCollection: IEvaluation[] = [sampleWithPartialData];
        expectedResult = service.addEvaluationToCollectionIfMissing(evaluationCollection, evaluation);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(evaluation);
      });

      it('should add only unique Evaluation to an array', () => {
        const evaluationArray: IEvaluation[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const evaluationCollection: IEvaluation[] = [sampleWithRequiredData];
        expectedResult = service.addEvaluationToCollectionIfMissing(evaluationCollection, ...evaluationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const evaluation: IEvaluation = sampleWithRequiredData;
        const evaluation2: IEvaluation = sampleWithPartialData;
        expectedResult = service.addEvaluationToCollectionIfMissing([], evaluation, evaluation2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(evaluation);
        expect(expectedResult).toContain(evaluation2);
      });

      it('should accept null and undefined values', () => {
        const evaluation: IEvaluation = sampleWithRequiredData;
        expectedResult = service.addEvaluationToCollectionIfMissing([], null, evaluation, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(evaluation);
      });

      it('should return initial array if no Evaluation is added', () => {
        const evaluationCollection: IEvaluation[] = [sampleWithRequiredData];
        expectedResult = service.addEvaluationToCollectionIfMissing(evaluationCollection, undefined, null);
        expect(expectedResult).toEqual(evaluationCollection);
      });
    });

    describe('compareEvaluation', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEvaluation(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 12820 };
        const entity2 = null;

        const compareResult1 = service.compareEvaluation(entity1, entity2);
        const compareResult2 = service.compareEvaluation(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 12820 };
        const entity2 = { id: 6284 };

        const compareResult1 = service.compareEvaluation(entity1, entity2);
        const compareResult2 = service.compareEvaluation(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 12820 };
        const entity2 = { id: 12820 };

        const compareResult1 = service.compareEvaluation(entity1, entity2);
        const compareResult2 = service.compareEvaluation(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
