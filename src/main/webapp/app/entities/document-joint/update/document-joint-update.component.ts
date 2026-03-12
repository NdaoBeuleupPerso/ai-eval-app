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
import { ISoumission } from 'app/entities/soumission/soumission.model';
import { SoumissionService } from 'app/entities/soumission/service/soumission.service';
import { FormatDocument } from 'app/entities/enumerations/format-document.model';
import { DocumentJointService } from '../service/document-joint.service';
import { IDocumentJoint } from '../document-joint.model';
import { DocumentJointFormGroup, DocumentJointFormService } from './document-joint-form.service';

@Component({
  selector: 'jhi-document-joint-update',
  templateUrl: './document-joint-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DocumentJointUpdateComponent implements OnInit {
  isSaving = false;
  documentJoint: IDocumentJoint | null = null;
  formatDocumentValues = Object.keys(FormatDocument);

  soumissionsSharedCollection: ISoumission[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected documentJointService = inject(DocumentJointService);
  protected documentJointFormService = inject(DocumentJointFormService);
  protected soumissionService = inject(SoumissionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DocumentJointFormGroup = this.documentJointFormService.createDocumentJointFormGroup();

  compareSoumission = (o1: ISoumission | null, o2: ISoumission | null): boolean => this.soumissionService.compareSoumission(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ documentJoint }) => {
      this.documentJoint = documentJoint;
      if (documentJoint) {
        this.updateForm(documentJoint);
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
    const documentJoint = this.documentJointFormService.getDocumentJoint(this.editForm);
    if (documentJoint.id !== null) {
      this.subscribeToSaveResponse(this.documentJointService.update(documentJoint));
    } else {
      this.subscribeToSaveResponse(this.documentJointService.create(documentJoint));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDocumentJoint>>): void {
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

  protected updateForm(documentJoint: IDocumentJoint): void {
    this.documentJoint = documentJoint;
    this.documentJointFormService.resetForm(this.editForm, documentJoint);

    this.soumissionsSharedCollection = this.soumissionService.addSoumissionToCollectionIfMissing<ISoumission>(
      this.soumissionsSharedCollection,
      documentJoint.soumission,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.soumissionService
      .query()
      .pipe(map((res: HttpResponse<ISoumission[]>) => res.body ?? []))
      .pipe(
        map((soumissions: ISoumission[]) =>
          this.soumissionService.addSoumissionToCollectionIfMissing<ISoumission>(soumissions, this.documentJoint?.soumission),
        ),
      )
      .subscribe((soumissions: ISoumission[]) => (this.soumissionsSharedCollection = soumissions));
  }
}
