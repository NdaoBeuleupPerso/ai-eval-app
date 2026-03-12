import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ICandidat } from '../candidat.model';

@Component({
  selector: 'jhi-candidat-detail',
  templateUrl: './candidat-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class CandidatDetailComponent {
  candidat = input<ICandidat | null>(null);

  previousState(): void {
    window.history.back();
  }
}
