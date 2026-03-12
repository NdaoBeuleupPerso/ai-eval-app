import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ICritere } from '../critere.model';

@Component({
  selector: 'jhi-critere-detail',
  templateUrl: './critere-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class CritereDetailComponent {
  critere = input<ICritere | null>(null);

  previousState(): void {
    window.history.back();
  }
}
