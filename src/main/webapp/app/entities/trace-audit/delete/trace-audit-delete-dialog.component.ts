import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITraceAudit } from '../trace-audit.model';
import { TraceAuditService } from '../service/trace-audit.service';

@Component({
  templateUrl: './trace-audit-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TraceAuditDeleteDialogComponent {
  traceAudit?: ITraceAudit;

  protected traceAuditService = inject(TraceAuditService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.traceAuditService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
