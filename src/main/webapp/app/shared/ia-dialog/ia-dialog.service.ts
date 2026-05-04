import { Injectable, inject } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { IaDialogComponent } from './ia-dialog.component';

@Injectable({ providedIn: 'root' })
export class IaDialogService {
  private modalService = inject(NgbModal);

  confirmIA(subTitle: string, message: string): Promise<any> {
    const modalRef = this.modalService.open(IaDialogComponent, { centered: true, backdrop: 'static' });
    const instance = modalRef.componentInstance as IaDialogComponent;

    instance.type = 'warning';
    instance.mainIcon = 'robot';
    instance.title = 'Intelligence Artificielle';
    instance.subTitle = subTitle;
    instance.message = message;
    instance.confirmLabel = "Lancer l'analyse";
    instance.cancelLabel = 'Plus tard';

    return modalRef.result;
  }
}
