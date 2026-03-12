import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { EvaluationService } from '../service/evaluation.service';
import { IEvaluation } from '../evaluation.model';
import { EvaluationFormService } from './evaluation-form.service';

import { EvaluationUpdateComponent } from './evaluation-update.component';

describe('Evaluation Management Update Component', () => {
  let comp: EvaluationUpdateComponent;
  let fixture: ComponentFixture<EvaluationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let evaluationFormService: EvaluationFormService;
  let evaluationService: EvaluationService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [EvaluationUpdateComponent],
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
      .overrideTemplate(EvaluationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EvaluationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    evaluationFormService = TestBed.inject(EvaluationFormService);
    evaluationService = TestBed.inject(EvaluationService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const evaluation: IEvaluation = { id: 6284 };
      const evaluateur: IUser = { id: '1344246c-16a7-46d1-bb61-2043f965c8d5' };
      evaluation.evaluateur = evaluateur;

      const userCollection: IUser[] = [{ id: '1344246c-16a7-46d1-bb61-2043f965c8d5' }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [evaluateur];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ evaluation });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const evaluation: IEvaluation = { id: 6284 };
      const evaluateur: IUser = { id: '1344246c-16a7-46d1-bb61-2043f965c8d5' };
      evaluation.evaluateur = evaluateur;

      activatedRoute.data = of({ evaluation });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(evaluateur);
      expect(comp.evaluation).toEqual(evaluation);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvaluation>>();
      const evaluation = { id: 12820 };
      jest.spyOn(evaluationFormService, 'getEvaluation').mockReturnValue(evaluation);
      jest.spyOn(evaluationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ evaluation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: evaluation }));
      saveSubject.complete();

      // THEN
      expect(evaluationFormService.getEvaluation).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(evaluationService.update).toHaveBeenCalledWith(expect.objectContaining(evaluation));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvaluation>>();
      const evaluation = { id: 12820 };
      jest.spyOn(evaluationFormService, 'getEvaluation').mockReturnValue({ id: null });
      jest.spyOn(evaluationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ evaluation: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: evaluation }));
      saveSubject.complete();

      // THEN
      expect(evaluationFormService.getEvaluation).toHaveBeenCalled();
      expect(evaluationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvaluation>>();
      const evaluation = { id: 12820 };
      jest.spyOn(evaluationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ evaluation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(evaluationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: '1344246c-16a7-46d1-bb61-2043f965c8d5' };
        const entity2 = { id: '1e61df13-b2d3-459d-875e-5607a4ccdbdb' };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
