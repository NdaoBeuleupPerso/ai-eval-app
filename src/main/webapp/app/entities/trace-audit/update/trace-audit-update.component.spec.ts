import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IEvaluation } from 'app/entities/evaluation/evaluation.model';
import { EvaluationService } from 'app/entities/evaluation/service/evaluation.service';
import { TraceAuditService } from '../service/trace-audit.service';
import { ITraceAudit } from '../trace-audit.model';
import { TraceAuditFormService } from './trace-audit-form.service';

import { TraceAuditUpdateComponent } from './trace-audit-update.component';

describe('TraceAudit Management Update Component', () => {
  let comp: TraceAuditUpdateComponent;
  let fixture: ComponentFixture<TraceAuditUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let traceAuditFormService: TraceAuditFormService;
  let traceAuditService: TraceAuditService;
  let evaluationService: EvaluationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TraceAuditUpdateComponent],
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
      .overrideTemplate(TraceAuditUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TraceAuditUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    traceAuditFormService = TestBed.inject(TraceAuditFormService);
    traceAuditService = TestBed.inject(TraceAuditService);
    evaluationService = TestBed.inject(EvaluationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Evaluation query and add missing value', () => {
      const traceAudit: ITraceAudit = { id: 8667 };
      const evaluation: IEvaluation = { id: 12820 };
      traceAudit.evaluation = evaluation;

      const evaluationCollection: IEvaluation[] = [{ id: 12820 }];
      jest.spyOn(evaluationService, 'query').mockReturnValue(of(new HttpResponse({ body: evaluationCollection })));
      const additionalEvaluations = [evaluation];
      const expectedCollection: IEvaluation[] = [...additionalEvaluations, ...evaluationCollection];
      jest.spyOn(evaluationService, 'addEvaluationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ traceAudit });
      comp.ngOnInit();

      expect(evaluationService.query).toHaveBeenCalled();
      expect(evaluationService.addEvaluationToCollectionIfMissing).toHaveBeenCalledWith(
        evaluationCollection,
        ...additionalEvaluations.map(expect.objectContaining),
      );
      expect(comp.evaluationsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const traceAudit: ITraceAudit = { id: 8667 };
      const evaluation: IEvaluation = { id: 12820 };
      traceAudit.evaluation = evaluation;

      activatedRoute.data = of({ traceAudit });
      comp.ngOnInit();

      expect(comp.evaluationsSharedCollection).toContainEqual(evaluation);
      expect(comp.traceAudit).toEqual(traceAudit);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITraceAudit>>();
      const traceAudit = { id: 23769 };
      jest.spyOn(traceAuditFormService, 'getTraceAudit').mockReturnValue(traceAudit);
      jest.spyOn(traceAuditService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ traceAudit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: traceAudit }));
      saveSubject.complete();

      // THEN
      expect(traceAuditFormService.getTraceAudit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(traceAuditService.update).toHaveBeenCalledWith(expect.objectContaining(traceAudit));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITraceAudit>>();
      const traceAudit = { id: 23769 };
      jest.spyOn(traceAuditFormService, 'getTraceAudit').mockReturnValue({ id: null });
      jest.spyOn(traceAuditService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ traceAudit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: traceAudit }));
      saveSubject.complete();

      // THEN
      expect(traceAuditFormService.getTraceAudit).toHaveBeenCalled();
      expect(traceAuditService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITraceAudit>>();
      const traceAudit = { id: 23769 };
      jest.spyOn(traceAuditService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ traceAudit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(traceAuditService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareEvaluation', () => {
      it('should forward to evaluationService', () => {
        const entity = { id: 12820 };
        const entity2 = { id: 6284 };
        jest.spyOn(evaluationService, 'compareEvaluation');
        comp.compareEvaluation(entity, entity2);
        expect(evaluationService.compareEvaluation).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
