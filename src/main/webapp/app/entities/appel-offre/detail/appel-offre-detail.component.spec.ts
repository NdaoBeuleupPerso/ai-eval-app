import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { AppelOffreDetailComponent } from './appel-offre-detail.component';

describe('AppelOffre Management Detail Component', () => {
  let comp: AppelOffreDetailComponent;
  let fixture: ComponentFixture<AppelOffreDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppelOffreDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: AppelOffreDetailComponent,
              resolve: { appelOffre: () => of({ id: 31506, reference: 'REF123', description: 'Ma description texte' }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(AppelOffreDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AppelOffreDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load appelOffre on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AppelOffreDetailComponent);

      // Correction : Accès direct à la propriété (pas comme une fonction)
      // et vérification qu'elle n'est pas nulle
      expect(instance.appelOffre).not.toBeNull();
      expect(instance.appelOffre?.id).toEqual(31506);
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });

  // Le test sur byteSize a été SUPPRIMÉ car la méthode n'existe plus dans le composant

  describe('openFile', () => {
    it('should call openFile from DataUtils', () => {
      // On mock le dataUtils injecté dans le composant
      const dataUtils = (comp as any).dataUtils;
      jest.spyOn(dataUtils, 'openFile');

      const fakeContentType = 'fake content type';
      const fakeBase64 = 'fake base64';

      // WHEN
      comp.openFile(fakeBase64, fakeContentType);

      // THEN
      expect(dataUtils.openFile).toHaveBeenCalledWith(fakeBase64, fakeContentType);
    });
  });
});
