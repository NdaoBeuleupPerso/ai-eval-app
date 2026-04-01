import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { TypeSource } from 'app/entities/enumerations/type-source.model';
import { ReferenceLegaleService } from '../service/reference-legale.service';
import { IReferenceLegale } from '../reference-legale.model';
import { ReferenceLegaleFormGroup, ReferenceLegaleFormService } from './reference-legale-form.service';

@Component({
  selector: 'jhi-reference-legale-update',
  templateUrl: './reference-legale-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ReferenceLegaleUpdateComponent implements OnInit {
  isSaving = false;
  referenceLegale: IReferenceLegale | null = null;
  typeSourceValues = Object.keys(TypeSource);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected referenceLegaleService = inject(ReferenceLegaleService);
  protected referenceLegaleFormService = inject(ReferenceLegaleFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  protected editForm: ReferenceLegaleFormGroup = this.referenceLegaleFormService.createReferenceLegaleFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ referenceLegale }) => {
      this.referenceLegale = referenceLegale;
      if (referenceLegale) {
        this.updateForm(referenceLegale);
      }
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
    const referenceLegale = this.referenceLegaleFormService.getReferenceLegale(this.editForm);
    if (referenceLegale.id !== null) {
      this.subscribeToSaveResponse(this.referenceLegaleService.update(referenceLegale));
    } else {
      this.subscribeToSaveResponse(this.referenceLegaleService.create(referenceLegale));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReferenceLegale>>): void {
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

  protected updateForm(referenceLegale: IReferenceLegale): void {
    this.referenceLegale = referenceLegale;
    this.referenceLegaleFormService.resetForm(this.editForm, referenceLegale);
  }
}
