import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../critere.test-samples';

import { CritereFormService } from './critere-form.service';

describe('Critere Form Service', () => {
  let service: CritereFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CritereFormService);
  });

  describe('Service methods', () => {
    describe('createCritereFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCritereFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            ponderation: expect.any(Object),
            categorie: expect.any(Object),
            description: expect.any(Object),
            appelOffre: expect.any(Object),
          }),
        );
      });

      it('passing ICritere should create a new form with FormGroup', () => {
        const formGroup = service.createCritereFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            ponderation: expect.any(Object),
            categorie: expect.any(Object),
            description: expect.any(Object),
            appelOffre: expect.any(Object),
          }),
        );
      });
    });

    describe('getCritere', () => {
      it('should return NewCritere for default Critere initial value', () => {
        const formGroup = service.createCritereFormGroup(sampleWithNewData);

        const critere = service.getCritere(formGroup) as any;

        expect(critere).toMatchObject(sampleWithNewData);
      });

      it('should return NewCritere for empty Critere initial value', () => {
        const formGroup = service.createCritereFormGroup();

        const critere = service.getCritere(formGroup) as any;

        expect(critere).toMatchObject({});
      });

      it('should return ICritere', () => {
        const formGroup = service.createCritereFormGroup(sampleWithRequiredData);

        const critere = service.getCritere(formGroup) as any;

        expect(critere).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICritere should not enable id FormControl', () => {
        const formGroup = service.createCritereFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCritere should disable id FormControl', () => {
        const formGroup = service.createCritereFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
