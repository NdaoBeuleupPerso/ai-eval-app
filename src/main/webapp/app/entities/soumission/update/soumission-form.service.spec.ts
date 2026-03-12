import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../soumission.test-samples';

import { SoumissionFormService } from './soumission-form.service';

describe('Soumission Form Service', () => {
  let service: SoumissionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SoumissionFormService);
  });

  describe('Service methods', () => {
    describe('createSoumissionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSoumissionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dateSoumission: expect.any(Object),
            statut: expect.any(Object),
            evaluation: expect.any(Object),
            appelOffre: expect.any(Object),
            candidat: expect.any(Object),
          }),
        );
      });

      it('passing ISoumission should create a new form with FormGroup', () => {
        const formGroup = service.createSoumissionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dateSoumission: expect.any(Object),
            statut: expect.any(Object),
            evaluation: expect.any(Object),
            appelOffre: expect.any(Object),
            candidat: expect.any(Object),
          }),
        );
      });
    });

    describe('getSoumission', () => {
      it('should return NewSoumission for default Soumission initial value', () => {
        const formGroup = service.createSoumissionFormGroup(sampleWithNewData);

        const soumission = service.getSoumission(formGroup) as any;

        expect(soumission).toMatchObject(sampleWithNewData);
      });

      it('should return NewSoumission for empty Soumission initial value', () => {
        const formGroup = service.createSoumissionFormGroup();

        const soumission = service.getSoumission(formGroup) as any;

        expect(soumission).toMatchObject({});
      });

      it('should return ISoumission', () => {
        const formGroup = service.createSoumissionFormGroup(sampleWithRequiredData);

        const soumission = service.getSoumission(formGroup) as any;

        expect(soumission).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISoumission should not enable id FormControl', () => {
        const formGroup = service.createSoumissionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSoumission should disable id FormControl', () => {
        const formGroup = service.createSoumissionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
