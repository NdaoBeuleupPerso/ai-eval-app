import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IEvaluation } from '../evaluation.model';
import { EvaluationService } from '../service/evaluation.service';

@Component({
  templateUrl: './evaluation-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class EvaluationDeleteDialogComponent {
  evaluation?: IEvaluation;

  protected evaluationService = inject(EvaluationService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.evaluationService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
