import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../document-joint.test-samples';

import { DocumentJointFormService } from './document-joint-form.service';

describe('DocumentJoint Form Service', () => {
  let service: DocumentJointFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DocumentJointFormService);
  });

  describe('Service methods', () => {
    describe('createDocumentJointFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDocumentJointFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            format: expect.any(Object),
            url: expect.any(Object),
            contenuOcr: expect.any(Object),
            idExterne: expect.any(Object),
            soumission: expect.any(Object),
          }),
        );
      });

      it('passing IDocumentJoint should create a new form with FormGroup', () => {
        const formGroup = service.createDocumentJointFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            format: expect.any(Object),
            url: expect.any(Object),
            contenuOcr: expect.any(Object),
            idExterne: expect.any(Object),
            soumission: expect.any(Object),
          }),
        );
      });
    });

    describe('getDocumentJoint', () => {
      it('should return NewDocumentJoint for default DocumentJoint initial value', () => {
        const formGroup = service.createDocumentJointFormGroup(sampleWithNewData);

        const documentJoint = service.getDocumentJoint(formGroup) as any;

        expect(documentJoint).toMatchObject(sampleWithNewData);
      });

      it('should return NewDocumentJoint for empty DocumentJoint initial value', () => {
        const formGroup = service.createDocumentJointFormGroup();

        const documentJoint = service.getDocumentJoint(formGroup) as any;

        expect(documentJoint).toMatchObject({});
      });

      it('should return IDocumentJoint', () => {
        const formGroup = service.createDocumentJointFormGroup(sampleWithRequiredData);

        const documentJoint = service.getDocumentJoint(formGroup) as any;

        expect(documentJoint).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDocumentJoint should not enable id FormControl', () => {
        const formGroup = service.createDocumentJointFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDocumentJoint should disable id FormControl', () => {
        const formGroup = service.createDocumentJointFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
