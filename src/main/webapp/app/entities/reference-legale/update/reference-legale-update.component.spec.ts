import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ReferenceLegaleService } from '../service/reference-legale.service';
import { IReferenceLegale } from '../reference-legale.model';
import { ReferenceLegaleFormService } from './reference-legale-form.service';

import { ReferenceLegaleUpdateComponent } from './reference-legale-update.component';

describe('ReferenceLegale Management Update Component', () => {
  let comp: ReferenceLegaleUpdateComponent;
  let fixture: ComponentFixture<ReferenceLegaleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let referenceLegaleFormService: ReferenceLegaleFormService;
  let referenceLegaleService: ReferenceLegaleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReferenceLegaleUpdateComponent],
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
      .overrideTemplate(ReferenceLegaleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReferenceLegaleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    referenceLegaleFormService = TestBed.inject(ReferenceLegaleFormService);
    referenceLegaleService = TestBed.inject(ReferenceLegaleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const referenceLegale: IReferenceLegale = { id: 2811 };

      activatedRoute.data = of({ referenceLegale });
      comp.ngOnInit();

      expect(comp.referenceLegale).toEqual(referenceLegale);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReferenceLegale>>();
      const referenceLegale = { id: 14030 };
      jest.spyOn(referenceLegaleFormService, 'getReferenceLegale').mockReturnValue(referenceLegale);
      jest.spyOn(referenceLegaleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ referenceLegale });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: referenceLegale }));
      saveSubject.complete();

      // THEN
      expect(referenceLegaleFormService.getReferenceLegale).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(referenceLegaleService.update).toHaveBeenCalledWith(expect.objectContaining(referenceLegale));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReferenceLegale>>();
      const referenceLegale = { id: 14030 };
      jest.spyOn(referenceLegaleFormService, 'getReferenceLegale').mockReturnValue({ id: null });
      jest.spyOn(referenceLegaleService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ referenceLegale: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: referenceLegale }));
      saveSubject.complete();

      // THEN
      expect(referenceLegaleFormService.getReferenceLegale).toHaveBeenCalled();
      expect(referenceLegaleService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReferenceLegale>>();
      const referenceLegale = { id: 14030 };
      jest.spyOn(referenceLegaleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ referenceLegale });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(referenceLegaleService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
