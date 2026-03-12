import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { AppelOffreService } from '../service/appel-offre.service';
import { IAppelOffre } from '../appel-offre.model';
import { AppelOffreFormService } from './appel-offre-form.service';

import { AppelOffreUpdateComponent } from './appel-offre-update.component';

describe('AppelOffre Management Update Component', () => {
  let comp: AppelOffreUpdateComponent;
  let fixture: ComponentFixture<AppelOffreUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let appelOffreFormService: AppelOffreFormService;
  let appelOffreService: AppelOffreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppelOffreUpdateComponent],
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
      .overrideTemplate(AppelOffreUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AppelOffreUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    appelOffreFormService = TestBed.inject(AppelOffreFormService);
    appelOffreService = TestBed.inject(AppelOffreService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const appelOffre: IAppelOffre = { id: 4012 };

      activatedRoute.data = of({ appelOffre });
      comp.ngOnInit();

      expect(comp.appelOffre).toEqual(appelOffre);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAppelOffre>>();
      const appelOffre = { id: 31506 };
      jest.spyOn(appelOffreFormService, 'getAppelOffre').mockReturnValue(appelOffre);
      jest.spyOn(appelOffreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appelOffre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: appelOffre }));
      saveSubject.complete();

      // THEN
      expect(appelOffreFormService.getAppelOffre).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(appelOffreService.update).toHaveBeenCalledWith(expect.objectContaining(appelOffre));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAppelOffre>>();
      const appelOffre = { id: 31506 };
      jest.spyOn(appelOffreFormService, 'getAppelOffre').mockReturnValue({ id: null });
      jest.spyOn(appelOffreService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appelOffre: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: appelOffre }));
      saveSubject.complete();

      // THEN
      expect(appelOffreFormService.getAppelOffre).toHaveBeenCalled();
      expect(appelOffreService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAppelOffre>>();
      const appelOffre = { id: 31506 };
      jest.spyOn(appelOffreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appelOffre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(appelOffreService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
