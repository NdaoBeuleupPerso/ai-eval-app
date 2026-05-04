import { Component, Input, inject } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import SharedModule from 'app/shared/shared.module';

@Component({
  standalone: true,
  selector: 'jhi-ia-dialog',
  template: `
    <div class="modal-header" [ngClass]="'bg-' + type">
      <h4 class="modal-title text-white"><fa-icon [icon]="icon"></fa-icon> {{ title }}</h4>
    </div>
    <div class="modal-body text-center p-4">
      <div class="mb-3" *ngIf="showAnimation">
        <!-- Remplacement de 'pulse' par 'beat' pour la compatibilité et le look -->
        <fa-icon [icon]="mainIcon" size="4x" [class]="'text-' + type" [animation]="isProcessing ? 'beat' : undefined"> </fa-icon>
      </div>
      <h5 class="fw-bold">{{ subTitle }}</h5>
      <p class="text-muted" [innerHTML]="message"></p>
    </div>
    <div class="modal-footer justify-content-center border-0 mb-3">
      <button type="button" class="btn btn-outline-secondary px-4" (click)="modal.dismiss('cancel')">
        {{ cancelLabel }}
      </button>
      <button type="button" [class]="'btn btn-' + type + ' px-4'" (click)="modal.close('confirm')">
        <fa-icon *ngIf="!isProcessing" icon="bolt"></fa-icon>
        <fa-icon *ngIf="isProcessing" icon="sync" [animation]="'spin'"></fa-icon>
        {{ isProcessing ? processingLabel : confirmLabel }}
      </button>
    </div>
  `,
  imports: [SharedModule],
})
export class IaDialogComponent {
  modal = inject(NgbActiveModal);

  @Input() type: 'primary' | 'warning' | 'info' | 'success' = 'primary';
  @Input() icon: any = 'robot';
  @Input() mainIcon: any = 'magic';
  @Input() title = 'Assistant IA';
  @Input() subTitle = '';
  @Input() message = '';
  @Input() confirmLabel = 'Confirmer';
  @Input() cancelLabel = 'Annuler';
  @Input() showAnimation = true;

  @Input() isProcessing = false;
  @Input() processingLabel = 'Traitement...';
}
