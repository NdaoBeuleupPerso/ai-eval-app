import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IEvaluation } from 'app/entities/evaluation/evaluation.model';
import { EvaluationService } from 'app/entities/evaluation/service/evaluation.service';
import { TraceAuditService } from '../service/trace-audit.service';
import { ITraceAudit } from '../trace-audit.model';
import { TraceAuditFormGroup, TraceAuditFormService } from './trace-audit-form.service';

@Component({
  selector: 'jhi-trace-audit-update',
  templateUrl: './trace-audit-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TraceAuditUpdateComponent implements OnInit {
  isSaving = false;
  traceAudit: ITraceAudit | null = null;

  evaluationsSharedCollection: IEvaluation[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected traceAuditService = inject(TraceAuditService);
  protected traceAuditFormService = inject(TraceAuditFormService);
  protected evaluationService = inject(EvaluationService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TraceAuditFormGroup = this.traceAuditFormService.createTraceAuditFormGroup();

  compareEvaluation = (o1: IEvaluation | null, o2: IEvaluation | null): boolean => this.evaluationService.compareEvaluation(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ traceAudit }) => {
      this.traceAudit = traceAudit;
      if (traceAudit) {
        this.updateForm(traceAudit);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('iaevalApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const traceAudit = this.traceAuditFormService.getTraceAudit(this.editForm);
    if (traceAudit.id !== null) {
      this.subscribeToSaveResponse(this.traceAuditService.update(traceAudit));
    } else {
      this.subscribeToSaveResponse(this.traceAuditService.create(traceAudit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITraceAudit>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(traceAudit: ITraceAudit): void {
    this.traceAudit = traceAudit;
    this.traceAuditFormService.resetForm(this.editForm, traceAudit);

    this.evaluationsSharedCollection = this.evaluationService.addEvaluationToCollectionIfMissing<IEvaluation>(
      this.evaluationsSharedCollection,
      traceAudit.evaluation,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.evaluationService
      .query()
      .pipe(map((res: HttpResponse<IEvaluation[]>) => res.body ?? []))
      .pipe(
        map((evaluations: IEvaluation[]) =>
          this.evaluationService.addEvaluationToCollectionIfMissing<IEvaluation>(evaluations, this.traceAudit?.evaluation),
        ),
      )
      .subscribe((evaluations: IEvaluation[]) => (this.evaluationsSharedCollection = evaluations));
  }
}
