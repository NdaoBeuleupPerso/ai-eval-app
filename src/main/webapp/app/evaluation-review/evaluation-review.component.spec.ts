import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EvaluationReviewComponent } from './evaluation-review.component';

describe('EvaluationReviewComponent', () => {
  let component: EvaluationReviewComponent;
  let fixture: ComponentFixture<EvaluationReviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EvaluationReviewComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EvaluationReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
