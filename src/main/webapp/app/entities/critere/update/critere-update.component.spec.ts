import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IAppelOffre } from 'app/entities/appel-offre/appel-offre.model';
import { AppelOffreService } from 'app/entities/appel-offre/service/appel-offre.service';
import { CritereService } from '../service/critere.service';
import { ICritere } from '../critere.model';
import { CritereFormService } from './critere-form.service';

import { CritereUpdateComponent } from './critere-update.component';

describe('Critere Management Update Component', () => {
  let comp: CritereUpdateComponent;
  let fixture: ComponentFixture<CritereUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let critereFormService: CritereFormService;
  let critereService: CritereService;
  let appelOffreService: AppelOffreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CritereUpdateComponent],
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
      .overrideTemplate(CritereUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CritereUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    critereFormService = TestBed.inject(CritereFormService);
    critereService = TestBed.inject(CritereService);
    appelOffreService = TestBed.inject(AppelOffreService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call AppelOffre query and add missing value', () => {
      const critere: ICritere = { id: 21343 };
      const appelOffre: IAppelOffre = { id: 31506 };
      critere.appelOffre = appelOffre;

      const appelOffreCollection: IAppelOffre[] = [{ id: 31506 }];
      jest.spyOn(appelOffreService, 'query').mockReturnValue(of(new HttpResponse({ body: appelOffreCollection })));
      const additionalAppelOffres = [appelOffre];
      const expectedCollection: IAppelOffre[] = [...additionalAppelOffres, ...appelOffreCollection];
      jest.spyOn(appelOffreService, 'addAppelOffreToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ critere });
      comp.ngOnInit();

      expect(appelOffreService.query).toHaveBeenCalled();
      expect(appelOffreService.addAppelOffreToCollectionIfMissing).toHaveBeenCalledWith(
        appelOffreCollection,
        ...additionalAppelOffres.map(expect.objectContaining),
      );
      expect(comp.appelOffresSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const critere: ICritere = { id: 21343 };
      const appelOffre: IAppelOffre = { id: 31506 };
      critere.appelOffre = appelOffre;

      activatedRoute.data = of({ critere });
      comp.ngOnInit();

      expect(comp.appelOffresSharedCollection).toContainEqual(appelOffre);
      expect(comp.critere).toEqual(critere);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICritere>>();
      const critere = { id: 18878 };
      jest.spyOn(critereFormService, 'getCritere').mockReturnValue(critere);
      jest.spyOn(critereService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ critere });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: critere }));
      saveSubject.complete();

      // THEN
      expect(critereFormService.getCritere).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(critereService.update).toHaveBeenCalledWith(expect.objectContaining(critere));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICritere>>();
      const critere = { id: 18878 };
      jest.spyOn(critereFormService, 'getCritere').mockReturnValue({ id: null });
      jest.spyOn(critereService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ critere: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: critere }));
      saveSubject.complete();

      // THEN
      expect(critereFormService.getCritere).toHaveBeenCalled();
      expect(critereService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICritere>>();
      const critere = { id: 18878 };
      jest.spyOn(critereService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ critere });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(critereService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareAppelOffre', () => {
      it('should forward to appelOffreService', () => {
        const entity = { id: 31506 };
        const entity2 = { id: 4012 };
        jest.spyOn(appelOffreService, 'compareAppelOffre');
        comp.compareAppelOffre(entity, entity2);
        expect(appelOffreService.compareAppelOffre).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
