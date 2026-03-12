import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../trace-audit.test-samples';

import { TraceAuditFormService } from './trace-audit-form.service';

describe('TraceAudit Form Service', () => {
  let service: TraceAuditFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TraceAuditFormService);
  });

  describe('Service methods', () => {
    describe('createTraceAuditFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTraceAuditFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            action: expect.any(Object),
            horodatage: expect.any(Object),
            details: expect.any(Object),
            identifiantUtilisateur: expect.any(Object),
            promptUtilise: expect.any(Object),
            evaluation: expect.any(Object),
          }),
        );
      });

      it('passing ITraceAudit should create a new form with FormGroup', () => {
        const formGroup = service.createTraceAuditFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            action: expect.any(Object),
            horodatage: expect.any(Object),
            details: expect.any(Object),
            identifiantUtilisateur: expect.any(Object),
            promptUtilise: expect.any(Object),
            evaluation: expect.any(Object),
          }),
        );
      });
    });

    describe('getTraceAudit', () => {
      it('should return NewTraceAudit for default TraceAudit initial value', () => {
        const formGroup = service.createTraceAuditFormGroup(sampleWithNewData);

        const traceAudit = service.getTraceAudit(formGroup) as any;

        expect(traceAudit).toMatchObject(sampleWithNewData);
      });

      it('should return NewTraceAudit for empty TraceAudit initial value', () => {
        const formGroup = service.createTraceAuditFormGroup();

        const traceAudit = service.getTraceAudit(formGroup) as any;

        expect(traceAudit).toMatchObject({});
      });

      it('should return ITraceAudit', () => {
        const formGroup = service.createTraceAuditFormGroup(sampleWithRequiredData);

        const traceAudit = service.getTraceAudit(formGroup) as any;

        expect(traceAudit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITraceAudit should not enable id FormControl', () => {
        const formGroup = service.createTraceAuditFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTraceAudit should disable id FormControl', () => {
        const formGroup = service.createTraceAuditFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
