import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { CritereDetailComponent } from './critere-detail.component';

describe('Critere Management Detail Component', () => {
  let comp: CritereDetailComponent;
  let fixture: ComponentFixture<CritereDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CritereDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./critere-detail.component').then(m => m.CritereDetailComponent),
              resolve: { critere: () => of({ id: 18878 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CritereDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CritereDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load critere on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CritereDetailComponent);

      // THEN
      expect(instance.critere()).toEqual(expect.objectContaining({ id: 18878 }));
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
