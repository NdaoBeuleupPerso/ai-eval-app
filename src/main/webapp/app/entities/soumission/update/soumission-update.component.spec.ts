import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import dayjs from 'dayjs/esm';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { IAppelOffre } from 'app/entities/appel-offre/appel-offre.model';
import { AppelOffreService } from 'app/entities/appel-offre/service/appel-offre.service';
import { ICandidat } from 'app/entities/candidat/candidat.model';
import { CandidatService } from 'app/entities/candidat/service/candidat.service';
import { StatutEvaluation } from 'app/entities/enumerations/statut-evaluation.model';
import SharedModule from 'app/shared/shared.module';
import { SoumissionService } from '../service/soumission.service';
import { ISoumission, ISoumissionFile } from '../soumission.model';
import { SoumissionFormGroup, SoumissionFormService } from './soumission-form.service';

@Component({
  standalone: true,
  selector: 'jhi-soumission-update',
  templateUrl: './soumission-update.component.html',
  styleUrls: ['./soumission-update.component.scss'],
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SoumissionUpdateComponent implements OnInit {
  isSaving = false;
  statutEvaluationValues = Object.keys(StatutEvaluation);
  fichiersSelectionnes: ISoumissionFile[] = [];

  appelOffresSharedCollection: IAppelOffre[] = [];
  candidatsSharedCollection: ICandidat[] = [];

  protected soumissionService = inject(SoumissionService);
  protected soumissionFormService = inject(SoumissionFormService);
  protected appelOffreService = inject(AppelOffreService);
  protected candidatService = inject(CandidatService);
  protected activatedRoute = inject(ActivatedRoute);

  editForm: SoumissionFormGroup = this.soumissionFormService.createSoumissionFormGroup();

  compareAppelOffre = (o1: IAppelOffre | null, o2: IAppelOffre | null): boolean => this.appelOffreService.compareAppelOffre(o1, o2);
  compareCandidat = (o1: ICandidat | null, o2: ICandidat | null): boolean => this.candidatService.compareCandidat(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ soumission }) => {
      if (soumission) {
        this.updateForm(soumission);
      } else {
        // Valeurs par défaut pour une nouvelle soumission
        this.editForm.patchValue({
          dateSoumission: dayjs().format('YYYY-MM-DDTHH:mm'),
          statut: 'EN_ATTENTE',
        });
      }
      this.loadRelationshipsOptions();
    });
  }

  onFilesSelected(event: any): void {
    const files: FileList = event.target.files;
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      const reader = new FileReader();
      reader.onload = (e: any) => {
        const base64Content = e.target.result.split(',')[1];
        this.fichiersSelectionnes.push({
          file: base64Content,
          fileContentType: file.type,
          nom: file.name,
        });
      };
      reader.readAsDataURL(file);
    }
  }

  removeFile(index: number): void {
    this.fichiersSelectionnes.splice(index, 1);
  }

  save(): void {
    this.isSaving = true;
    const soumission = this.soumissionFormService.getSoumission(this.editForm);

    // On attache les fichiers accumulés au DTO
    soumission.fichiersNouveaux = this.fichiersSelectionnes;

    if (soumission.id !== null) {
      this.subscribeToSaveResponse(this.soumissionService.update(soumission));
    } else {
      this.subscribeToSaveResponse(this.soumissionService.create(soumission));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISoumission>>): void {
    result.pipe(finalize(() => (this.isSaving = false))).subscribe({
      next: () => this.previousState(),
      error: () => alert("Erreur lors de l'envoi de la soumission."),
    });
  }

  previousState(): void {
    window.history.back();
  }

  protected updateForm(soumission: ISoumission): void {
    this.soumissionFormService.resetForm(this.editForm, soumission);
    this.appelOffresSharedCollection = this.appelOffreService.addAppelOffreToCollectionIfMissing(
      this.appelOffresSharedCollection,
      soumission.appelOffre,
    );
    this.candidatsSharedCollection = this.candidatService.addCandidatToCollectionIfMissing(
      this.candidatsSharedCollection,
      soumission.candidat,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.appelOffreService.query().subscribe(res => (this.appelOffresSharedCollection = res.body ?? []));
    this.candidatService.query().subscribe(res => (this.candidatsSharedCollection = res.body ?? []));
  }
}
