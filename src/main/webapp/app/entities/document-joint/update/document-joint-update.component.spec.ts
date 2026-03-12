import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ISoumission } from 'app/entities/soumission/soumission.model';
import { SoumissionService } from 'app/entities/soumission/service/soumission.service';
import { DocumentJointService } from '../service/document-joint.service';
import { IDocumentJoint } from '../document-joint.model';
import { DocumentJointFormService } from './document-joint-form.service';

import { DocumentJointUpdateComponent } from './document-joint-update.component';

describe('DocumentJoint Management Update Component', () => {
  let comp: DocumentJointUpdateComponent;
  let fixture: ComponentFixture<DocumentJointUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let documentJointFormService: DocumentJointFormService;
  let documentJointService: DocumentJointService;
  let soumissionService: SoumissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DocumentJointUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(DocumentJointUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DocumentJointUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    documentJointFormService = TestBed.inject(DocumentJointFormService);
    documentJointService = TestBed.inject(DocumentJointService);
    soumissionService = TestBed.inject(SoumissionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Soumission query and add missing value', () => {
      const documentJoint: IDocumentJoint = { id: 15336 };
      const soumission: ISoumission = { id: 4418 };
      documentJoint.soumission = soumission;

      const soumissionCollection: ISoumission[] = [{ id: 4418 }];
      jest.spyOn(soumissionService, 'query').mockReturnValue(of(new HttpResponse({ body: soumissionCollection })));
      const additionalSoumissions = [soumission];
      const expectedCollection: ISoumission[] = [...additionalSoumissions, ...soumissionCollection];
      jest.spyOn(soumissionService, 'addSoumissionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ documentJoint });
      comp.ngOnInit();

      expect(soumissionService.query).toHaveBeenCalled();
      expect(soumissionService.addSoumissionToCollectionIfMissing).toHaveBeenCalledWith(
        soumissionCollection,
        ...additionalSoumissions.map(expect.objectContaining),
      );
      expect(comp.soumissionsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const documentJoint: IDocumentJoint = { id: 15336 };
      const soumission: ISoumission = { id: 4418 };
      documentJoint.soumission = soumission;

      activatedRoute.data = of({ documentJoint });
      comp.ngOnInit();

      expect(comp.soumissionsSharedCollection).toContainEqual(soumission);
      expect(comp.documentJoint).toEqual(documentJoint);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDocumentJoint>>();
      const documentJoint = { id: 8654 };
      jest.spyOn(documentJointFormService, 'getDocumentJoint').mockReturnValue(documentJoint);
      jest.spyOn(documentJointService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ documentJoint });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: documentJoint }));
      saveSubject.complete();

      // THEN
      expect(documentJointFormService.getDocumentJoint).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(documentJointService.update).toHaveBeenCalledWith(expect.objectContaining(documentJoint));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDocumentJoint>>();
      const documentJoint = { id: 8654 };
      jest.spyOn(documentJointFormService, 'getDocumentJoint').mockReturnValue({ id: null });
      jest.spyOn(documentJointService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ documentJoint: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: documentJoint }));
      saveSubject.complete();

      // THEN
      expect(documentJointFormService.getDocumentJoint).toHaveBeenCalled();
      expect(documentJointService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDocumentJoint>>();
      const documentJoint = { id: 8654 };
      jest.spyOn(documentJointService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ documentJoint });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(documentJointService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSoumission', () => {
      it('should forward to soumissionService', () => {
        const entity = { id: 4418 };
        const entity2 = { id: 18967 };
        jest.spyOn(soumissionService, 'compareSoumission');
        comp.compareSoumission(entity, entity2);
        expect(soumissionService.compareSoumission).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
