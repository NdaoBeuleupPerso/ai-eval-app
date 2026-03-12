import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IEvaluation } from 'app/entities/evaluation/evaluation.model';
import { EvaluationService } from 'app/entities/evaluation/service/evaluation.service';
import { IAppelOffre } from 'app/entities/appel-offre/appel-offre.model';
import { AppelOffreService } from 'app/entities/appel-offre/service/appel-offre.service';
import { ICandidat } from 'app/entities/candidat/candidat.model';
import { CandidatService } from 'app/entities/candidat/service/candidat.service';
import { ISoumission } from '../soumission.model';
import { SoumissionService } from '../service/soumission.service';
import { SoumissionFormService } from './soumission-form.service';

import { SoumissionUpdateComponent } from './soumission-update.component';

describe('Soumission Management Update Component', () => {
  let comp: SoumissionUpdateComponent;
  let fixture: ComponentFixture<SoumissionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let soumissionFormService: SoumissionFormService;
  let soumissionService: SoumissionService;
  let evaluationService: EvaluationService;
  let appelOffreService: AppelOffreService;
  let candidatService: CandidatService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SoumissionUpdateComponent],
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
      .overrideTemplate(SoumissionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SoumissionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    soumissionFormService = TestBed.inject(SoumissionFormService);
    soumissionService = TestBed.inject(SoumissionService);
    evaluationService = TestBed.inject(EvaluationService);
    appelOffreService = TestBed.inject(AppelOffreService);
    candidatService = TestBed.inject(CandidatService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call evaluation query and add missing value', () => {
      const soumission: ISoumission = { id: 18967 };
      const evaluation: IEvaluation = { id: 12820 };
      soumission.evaluation = evaluation;

      const evaluationCollection: IEvaluation[] = [{ id: 12820 }];
      jest.spyOn(evaluationService, 'query').mockReturnValue(of(new HttpResponse({ body: evaluationCollection })));
      const expectedCollection: IEvaluation[] = [evaluation, ...evaluationCollection];
      jest.spyOn(evaluationService, 'addEvaluationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ soumission });
      comp.ngOnInit();

      expect(evaluationService.query).toHaveBeenCalled();
      expect(evaluationService.addEvaluationToCollectionIfMissing).toHaveBeenCalledWith(evaluationCollection, evaluation);
      expect(comp.evaluationsCollection).toEqual(expectedCollection);
    });

    it('should call AppelOffre query and add missing value', () => {
      const soumission: ISoumission = { id: 18967 };
      const appelOffre: IAppelOffre = { id: 31506 };
      soumission.appelOffre = appelOffre;

      const appelOffreCollection: IAppelOffre[] = [{ id: 31506 }];
      jest.spyOn(appelOffreService, 'query').mockReturnValue(of(new HttpResponse({ body: appelOffreCollection })));
      const additionalAppelOffres = [appelOffre];
      const expectedCollection: IAppelOffre[] = [...additionalAppelOffres, ...appelOffreCollection];
      jest.spyOn(appelOffreService, 'addAppelOffreToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ soumission });
      comp.ngOnInit();

      expect(appelOffreService.query).toHaveBeenCalled();
      expect(appelOffreService.addAppelOffreToCollectionIfMissing).toHaveBeenCalledWith(
        appelOffreCollection,
        ...additionalAppelOffres.map(expect.objectContaining),
      );
      expect(comp.appelOffresSharedCollection).toEqual(expectedCollection);
    });

    it('should call Candidat query and add missing value', () => {
      const soumission: ISoumission = { id: 18967 };
      const candidat: ICandidat = { id: 29649 };
      soumission.candidat = candidat;

      const candidatCollection: ICandidat[] = [{ id: 29649 }];
      jest.spyOn(candidatService, 'query').mockReturnValue(of(new HttpResponse({ body: candidatCollection })));
      const additionalCandidats = [candidat];
      const expectedCollection: ICandidat[] = [...additionalCandidats, ...candidatCollection];
      jest.spyOn(candidatService, 'addCandidatToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ soumission });
      comp.ngOnInit();

      expect(candidatService.query).toHaveBeenCalled();
      expect(candidatService.addCandidatToCollectionIfMissing).toHaveBeenCalledWith(
        candidatCollection,
        ...additionalCandidats.map(expect.objectContaining),
      );
      expect(comp.candidatsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const soumission: ISoumission = { id: 18967 };
      const evaluation: IEvaluation = { id: 12820 };
      soumission.evaluation = evaluation;
      const appelOffre: IAppelOffre = { id: 31506 };
      soumission.appelOffre = appelOffre;
      const candidat: ICandidat = { id: 29649 };
      soumission.candidat = candidat;

      activatedRoute.data = of({ soumission });
      comp.ngOnInit();

      expect(comp.evaluationsCollection).toContainEqual(evaluation);
      expect(comp.appelOffresSharedCollection).toContainEqual(appelOffre);
      expect(comp.candidatsSharedCollection).toContainEqual(candidat);
      expect(comp.soumission).toEqual(soumission);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISoumission>>();
      const soumission = { id: 4418 };
      jest.spyOn(soumissionFormService, 'getSoumission').mockReturnValue(soumission);
      jest.spyOn(soumissionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ soumission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: soumission }));
      saveSubject.complete();

      // THEN
      expect(soumissionFormService.getSoumission).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(soumissionService.update).toHaveBeenCalledWith(expect.objectContaining(soumission));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISoumission>>();
      const soumission = { id: 4418 };
      jest.spyOn(soumissionFormService, 'getSoumission').mockReturnValue({ id: null });
      jest.spyOn(soumissionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ soumission: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: soumission }));
      saveSubject.complete();

      // THEN
      expect(soumissionFormService.getSoumission).toHaveBeenCalled();
      expect(soumissionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISoumission>>();
      const soumission = { id: 4418 };
      jest.spyOn(soumissionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ soumission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(soumissionService.update).toHaveBeenCalled();
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

    describe('compareAppelOffre', () => {
      it('should forward to appelOffreService', () => {
        const entity = { id: 31506 };
        const entity2 = { id: 4012 };
        jest.spyOn(appelOffreService, 'compareAppelOffre');
        comp.compareAppelOffre(entity, entity2);
        expect(appelOffreService.compareAppelOffre).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCandidat', () => {
      it('should forward to candidatService', () => {
        const entity = { id: 29649 };
        const entity2 = { id: 17830 };
        jest.spyOn(candidatService, 'compareCandidat');
        comp.compareCandidat(entity, entity2);
        expect(candidatService.compareCandidat).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
