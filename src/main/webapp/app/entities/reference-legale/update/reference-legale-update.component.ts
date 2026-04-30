import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import SharedModule from 'app/shared/shared.module';

import { Validators } from '@angular/forms';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { TypeSource } from 'app/entities/enumerations/type-source.model';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { IReferenceLegale } from '../reference-legale.model';
import { ReferenceLegaleService } from '../service/reference-legale.service';
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

    this.validerChampsMutuels(); // Ajoutez cet appel dans ngOnInit pour initialiser la validation

    this.editForm.valueChanges.subscribe(() => {
      this.validerChampsMutuels();
    });
  }
  private validerChampsMutuels(): void {
    const contenuControl = this.editForm.get('contenu');
    const documentControl = this.editForm.get('document');

    // On vérifie si on a soit du texte, soit un fichier
    const aDuContenu = !!contenuControl?.value && contenuControl.value.trim().length > 0;
    const aUnDocument = !!documentControl?.value;

    if (aUnDocument) {
      // Si PDF présent : contenu devient facultatif et on efface ses erreurs
      contenuControl?.setValidators(null);
      contenuControl?.setErrors(null);
    } else if (!aDuContenu) {
      // Si ni l'un ni l'autre : on remet l'erreur required sur contenu
      contenuControl?.setValidators([Validators.required]);
      contenuControl?.setErrors({ required: true });
    }

    // On notifie Angular du changement visuel sans créer de boucle infinie
    contenuControl?.updateValueAndValidity({ emitEvent: false, onlySelf: true });
  }
  // 3. Modifiez setFileData pour déclencher la validation dès l'upload
  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      next: () => {
        // --- RÉCUPÉRATION DU NOM DU FICHIER ---
        const input = event.target as HTMLInputElement;
        if (input.files && input.files.length > 0) {
          const file = input.files[0];
          // On met à jour le champ nomFichier dans le formulaire
          this.editForm.patchValue({
            nomFichier: file.name,
          });
        }
        this.validerChampsMutuels();
      },
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('iaevalApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  /* setFileData(event: Event, field: string, isImage: boolean): void {
     this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
       error: (err: FileLoadError) =>
         this.eventManager.broadcast(new EventWithContent<AlertError>('iaevalApp.error', { ...err, key: `error.file.${err.key}` })),
     });
   }*/

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const referenceLegale = this.referenceLegaleFormService.getReferenceLegale(this.editForm);
    console.log('Données envoyées au serveur :', referenceLegale);
    if (referenceLegale.document) {
      console.log('Document présent, taille Base64 :', referenceLegale.document.length);
    } else {
      console.warn('ATTENTION : Le champ document est VIDE dans le formulaire !');
    }
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
