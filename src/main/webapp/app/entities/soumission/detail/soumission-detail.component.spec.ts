import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { SoumissionDetailComponent } from './soumission-detail.component';

describe('Soumission Management Detail Component', () => {
  let comp: SoumissionDetailComponent;
  let fixture: ComponentFixture<SoumissionDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SoumissionDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./soumission-detail.component').then(m => m.SoumissionDetailComponent),
              resolve: { soumission: () => of({ id: 4418 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SoumissionDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SoumissionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load soumission on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SoumissionDetailComponent);

      // THEN
      expect(instance.soumission()).toEqual(expect.objectContaining({ id: 4418 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
