import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { CritereService } from 'app/entities/critere/service/critere.service';
import { StatutAppel } from 'app/entities/enumerations/statut-appel.model';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { IaDialogService } from 'app/shared/ia-dialog/ia-dialog.service';
import SharedModule from 'app/shared/shared.module';
import { IAppelOffre } from '../appel-offre.model';
import { AppelOffreService } from '../service/appel-offre.service';
import { AppelOffreFormGroup, AppelOffreFormService } from './appel-offre-form.service';
@Component({
  standalone: true,
  selector: 'jhi-appel-offre-update',
  templateUrl: './appel-offre-update.component.html',
  styleUrls: ['./appel-offre-update.component.scss'],
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
  protected iaDialogService = inject(IaDialogService);
  protected critereService = inject(CritereService);
  protected router = inject(Router);

  editForm: AppelOffreFormGroup = this.appelOffreFormService.createAppelOffreFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appelOffre }) => {
      this.appelOffre = appelOffre;
      if (appelOffre) {
        this.updateForm(appelOffre);
      }
    });
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      next: () => {
        // Récupération du nom du fichier pour l'IA
        const input = event.target as HTMLInputElement;
        if (input.files && input.files.length > 0) {
          this.editForm.patchValue({ nomFichier: input.files[0].name });
        }
      },
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
      this.subscribeToSaveResponse(this.appelOffreService.update(appelOffre), false);
    } else {
      this.subscribeToSaveResponse(this.appelOffreService.create(appelOffre), true);
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppelOffre>>, isNew: boolean): void {
    result.pipe(finalize(() => (this.isSaving = false))).subscribe({
      next: res => this.onSaveSuccess(res.body, isNew),
      error: () => {},
    });
  }

  protected onSaveSuccess(appelOffre: IAppelOffre | null, isNew: boolean): void {
    if (isNew && appelOffre?.id) {
      this.iaDialogService
        .confirmIA(
          'Félicitations !',
          `L'Appel d'Offre <strong>${appelOffre.reference}</strong> a été créé. <br>Voulez-vous que l'IA génère la grille de critères ?`,
        )
        .then(result => {
          if (result === 'confirm') {
            this.isSaving = true;
            this.critereService.suggestForAppelOffre(appelOffre.id).subscribe({
              next: () => this.router.navigate(['/critere'], { queryParams: { 'appelOffreId.equals': appelOffre.id } }),
              error: () => this.router.navigate(['/appel-offre', appelOffre.id, 'view']),
            });
          } else {
            this.router.navigate(['/appel-offre', appelOffre.id, 'view']);
          }
        })
        .catch(() => {
          this.router.navigate(['/appel-offre', appelOffre.id, 'view']);
        });
    } else {
      this.previousState();
    }
  }

  protected updateForm(appelOffre: IAppelOffre): void {
    this.appelOffre = appelOffre;
    this.appelOffreFormService.resetForm(this.editForm, appelOffre);
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }
  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }
}
