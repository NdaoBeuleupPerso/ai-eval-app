import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../reference-legale.test-samples';

import { ReferenceLegaleFormService } from './reference-legale-form.service';

describe('ReferenceLegale Form Service', () => {
  let service: ReferenceLegaleFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReferenceLegaleFormService);
  });

  describe('Service methods', () => {
    describe('createReferenceLegaleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createReferenceLegaleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titre: expect.any(Object),
            contenu: expect.any(Object),
            typeSource: expect.any(Object),
            version: expect.any(Object),
            qdrantUuid: expect.any(Object),
          }),
        );
      });

      it('passing IReferenceLegale should create a new form with FormGroup', () => {
        const formGroup = service.createReferenceLegaleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titre: expect.any(Object),
            contenu: expect.any(Object),
            typeSource: expect.any(Object),
            version: expect.any(Object),
            qdrantUuid: expect.any(Object),
          }),
        );
      });
    });

    describe('getReferenceLegale', () => {
      it('should return NewReferenceLegale for default ReferenceLegale initial value', () => {
        const formGroup = service.createReferenceLegaleFormGroup(sampleWithNewData);

        const referenceLegale = service.getReferenceLegale(formGroup) as any;

        expect(referenceLegale).toMatchObject(sampleWithNewData);
      });

      it('should return NewReferenceLegale for empty ReferenceLegale initial value', () => {
        const formGroup = service.createReferenceLegaleFormGroup();

        const referenceLegale = service.getReferenceLegale(formGroup) as any;

        expect(referenceLegale).toMatchObject({});
      });

      it('should return IReferenceLegale', () => {
        const formGroup = service.createReferenceLegaleFormGroup(sampleWithRequiredData);

        const referenceLegale = service.getReferenceLegale(formGroup) as any;

        expect(referenceLegale).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IReferenceLegale should not enable id FormControl', () => {
        const formGroup = service.createReferenceLegaleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewReferenceLegale should disable id FormControl', () => {
        const formGroup = service.createReferenceLegaleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
