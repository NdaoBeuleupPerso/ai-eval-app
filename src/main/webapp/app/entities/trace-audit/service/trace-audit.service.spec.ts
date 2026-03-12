import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ITraceAudit } from '../trace-audit.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../trace-audit.test-samples';

import { RestTraceAudit, TraceAuditService } from './trace-audit.service';

const requireRestSample: RestTraceAudit = {
  ...sampleWithRequiredData,
  horodatage: sampleWithRequiredData.horodatage?.toJSON(),
};

describe('TraceAudit Service', () => {
  let service: TraceAuditService;
  let httpMock: HttpTestingController;
  let expectedResult: ITraceAudit | ITraceAudit[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TraceAuditService);
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

    it('should create a TraceAudit', () => {
      const traceAudit = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(traceAudit).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TraceAudit', () => {
      const traceAudit = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(traceAudit).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TraceAudit', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TraceAudit', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TraceAudit', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTraceAuditToCollectionIfMissing', () => {
      it('should add a TraceAudit to an empty array', () => {
        const traceAudit: ITraceAudit = sampleWithRequiredData;
        expectedResult = service.addTraceAuditToCollectionIfMissing([], traceAudit);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(traceAudit);
      });

      it('should not add a TraceAudit to an array that contains it', () => {
        const traceAudit: ITraceAudit = sampleWithRequiredData;
        const traceAuditCollection: ITraceAudit[] = [
          {
            ...traceAudit,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTraceAuditToCollectionIfMissing(traceAuditCollection, traceAudit);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TraceAudit to an array that doesn't contain it", () => {
        const traceAudit: ITraceAudit = sampleWithRequiredData;
        const traceAuditCollection: ITraceAudit[] = [sampleWithPartialData];
        expectedResult = service.addTraceAuditToCollectionIfMissing(traceAuditCollection, traceAudit);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(traceAudit);
      });

      it('should add only unique TraceAudit to an array', () => {
        const traceAuditArray: ITraceAudit[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const traceAuditCollection: ITraceAudit[] = [sampleWithRequiredData];
        expectedResult = service.addTraceAuditToCollectionIfMissing(traceAuditCollection, ...traceAuditArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const traceAudit: ITraceAudit = sampleWithRequiredData;
        const traceAudit2: ITraceAudit = sampleWithPartialData;
        expectedResult = service.addTraceAuditToCollectionIfMissing([], traceAudit, traceAudit2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(traceAudit);
        expect(expectedResult).toContain(traceAudit2);
      });

      it('should accept null and undefined values', () => {
        const traceAudit: ITraceAudit = sampleWithRequiredData;
        expectedResult = service.addTraceAuditToCollectionIfMissing([], null, traceAudit, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(traceAudit);
      });

      it('should return initial array if no TraceAudit is added', () => {
        const traceAuditCollection: ITraceAudit[] = [sampleWithRequiredData];
        expectedResult = service.addTraceAuditToCollectionIfMissing(traceAuditCollection, undefined, null);
        expect(expectedResult).toEqual(traceAuditCollection);
      });
    });

    describe('compareTraceAudit', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTraceAudit(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 23769 };
        const entity2 = null;

        const compareResult1 = service.compareTraceAudit(entity1, entity2);
        const compareResult2 = service.compareTraceAudit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 23769 };
        const entity2 = { id: 8667 };

        const compareResult1 = service.compareTraceAudit(entity1, entity2);
        const compareResult2 = service.compareTraceAudit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 23769 };
        const entity2 = { id: 23769 };

        const compareResult1 = service.compareTraceAudit(entity1, entity2);
        const compareResult2 = service.compareTraceAudit(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
