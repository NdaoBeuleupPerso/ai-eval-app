import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IDocumentJoint } from '../document-joint.model';
import { DocumentJointService } from '../service/document-joint.service';

@Component({
  templateUrl: './document-joint-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class DocumentJointDeleteDialogComponent {
  documentJoint?: IDocumentJoint;

  protected documentJointService = inject(DocumentJointService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.documentJointService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
