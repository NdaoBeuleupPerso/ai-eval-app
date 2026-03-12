import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../appel-offre.test-samples';

import { AppelOffreFormService } from './appel-offre-form.service';

describe('AppelOffre Form Service', () => {
  let service: AppelOffreFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AppelOffreFormService);
  });

  describe('Service methods', () => {
    describe('createAppelOffreFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAppelOffreFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            titre: expect.any(Object),
            description: expect.any(Object),
            dateCloture: expect.any(Object),
            statut: expect.any(Object),
          }),
        );
      });

      it('passing IAppelOffre should create a new form with FormGroup', () => {
        const formGroup = service.createAppelOffreFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            titre: expect.any(Object),
            description: expect.any(Object),
            dateCloture: expect.any(Object),
            statut: expect.any(Object),
          }),
        );
      });
    });

    describe('getAppelOffre', () => {
      it('should return NewAppelOffre for default AppelOffre initial value', () => {
        const formGroup = service.createAppelOffreFormGroup(sampleWithNewData);

        const appelOffre = service.getAppelOffre(formGroup) as any;

        expect(appelOffre).toMatchObject(sampleWithNewData);
      });

      it('should return NewAppelOffre for empty AppelOffre initial value', () => {
        const formGroup = service.createAppelOffreFormGroup();

        const appelOffre = service.getAppelOffre(formGroup) as any;

        expect(appelOffre).toMatchObject({});
      });

      it('should return IAppelOffre', () => {
        const formGroup = service.createAppelOffreFormGroup(sampleWithRequiredData);

        const appelOffre = service.getAppelOffre(formGroup) as any;

        expect(appelOffre).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAppelOffre should not enable id FormControl', () => {
        const formGroup = service.createAppelOffreFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAppelOffre should disable id FormControl', () => {
        const formGroup = service.createAppelOffreFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
