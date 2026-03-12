import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IReferenceLegale } from '../reference-legale.model';
import { ReferenceLegaleService } from '../service/reference-legale.service';

@Component({
  templateUrl: './reference-legale-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ReferenceLegaleDeleteDialogComponent {
  referenceLegale?: IReferenceLegale;

  protected referenceLegaleService = inject(ReferenceLegaleService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.referenceLegaleService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
