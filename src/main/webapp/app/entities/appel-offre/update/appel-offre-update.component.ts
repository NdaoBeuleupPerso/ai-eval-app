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
import { StatutAppel } from 'app/entities/enumerations/statut-appel.model';
import { AppelOffreService } from '../service/appel-offre.service';
import { IAppelOffre } from '../appel-offre.model';
import { AppelOffreFormGroup, AppelOffreFormService } from './appel-offre-form.service';

@Component({
  selector: 'jhi-appel-offre-update',
  templateUrl: './appel-offre-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AppelOffreUpdateComponent implements OnInit {
  isSaving = false;
  appelOffre: IAppelOffre | null = null;
  statutAppelValues = Object.keys(StatutAppel);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected appelOffreService = inject(AppelOffreService);
  protected appelOffreFormService = inject(AppelOffreFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AppelOffreFormGroup = this.appelOffreFormService.createAppelOffreFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appelOffre }) => {
      this.appelOffre = appelOffre;
      if (appelOffre) {
        this.updateForm(appelOffre);
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
    const appelOffre = this.appelOffreFormService.getAppelOffre(this.editForm);
    if (appelOffre.id !== null) {
      this.subscribeToSaveResponse(this.appelOffreService.update(appelOffre));
    } else {
      this.subscribeToSaveResponse(this.appelOffreService.create(appelOffre));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppelOffre>>): void {
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

  protected updateForm(appelOffre: IAppelOffre): void {
    this.appelOffre = appelOffre;
    this.appelOffreFormService.resetForm(this.editForm, appelOffre);
  }
}
