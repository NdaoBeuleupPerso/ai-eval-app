import { Component, inject, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DataUtils } from 'app/core/util/data-util.service';
import { IDocumentJoint } from '../document-joint.model';

@Component({
  selector: 'jhi-document-joint-detail',
  templateUrl: './document-joint-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class DocumentJointDetailComponent {
  documentJoint = input<IDocumentJoint | null>(null);

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
