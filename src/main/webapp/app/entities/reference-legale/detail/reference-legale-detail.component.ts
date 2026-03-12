import { Component, inject, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DataUtils } from 'app/core/util/data-util.service';
import { IReferenceLegale } from '../reference-legale.model';

@Component({
  selector: 'jhi-reference-legale-detail',
  templateUrl: './reference-legale-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class ReferenceLegaleDetailComponent {
  referenceLegale = input<IReferenceLegale | null>(null);

  protected dataUtils = inject(DataUtils);

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
