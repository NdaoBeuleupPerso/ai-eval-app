import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IEvaluation } from 'app/entities/evaluation/evaluation.model';
import { EvaluationService } from 'app/entities/evaluation/service/evaluation.service';
import { IAppelOffre } from 'app/entities/appel-offre/appel-offre.model';
import { AppelOffreService } from 'app/entities/appel-offre/service/appel-offre.service';
import { ICandidat } from 'app/entities/candidat/candidat.model';
import { CandidatService } from 'app/entities/candidat/service/candidat.service';
import { StatutEvaluation } from 'app/entities/enumerations/statut-evaluation.model';
import { SoumissionService } from '../service/soumission.service';
import { ISoumission } from '../soumission.model';
import { SoumissionFormGroup, SoumissionFormService } from './soumission-form.service';

@Component({
  selector: 'jhi-soumission-update',
  templateUrl: './soumission-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SoumissionUpdateComponent implements OnInit {
  isSaving = false;
  soumission: ISoumission | null = null;
  statutEvaluationValues = Object.keys(StatutEvaluation);

  evaluationsCollection: IEvaluation[] = [];
  appelOffresSharedCollection: IAppelOffre[] = [];
  candidatsSharedCollection: ICandidat[] = [];

  protected soumissionService = inject(SoumissionService);
  protected soumissionFormService = inject(SoumissionFormService);
  protected evaluationService = inject(EvaluationService);
  protected appelOffreService = inject(AppelOffreService);
  protected candidatService = inject(CandidatService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SoumissionFormGroup = this.soumissionFormService.createSoumissionFormGroup();

  compareEvaluation = (o1: IEvaluation | null, o2: IEvaluation | null): boolean => this.evaluationService.compareEvaluation(o1, o2);

  compareAppelOffre = (o1: IAppelOffre | null, o2: IAppelOffre | null): boolean => this.appelOffreService.compareAppelOffre(o1, o2);

  compareCandidat = (o1: ICandidat | null, o2: ICandidat | null): boolean => this.candidatService.compareCandidat(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ soumission }) => {
      this.soumission = soumission;
      if (soumission) {
        this.updateForm(soumission);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const soumission = this.soumissionFormService.getSoumission(this.editForm);
    if (soumission.id !== null) {
      this.subscribeToSaveResponse(this.soumissionService.update(soumission));
    } else {
      this.subscribeToSaveResponse(this.soumissionService.create(soumission));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISoumission>>): void {
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

  protected updateForm(soumission: ISoumission): void {
    this.soumission = soumission;
    this.soumissionFormService.resetForm(this.editForm, soumission);

    this.evaluationsCollection = this.evaluationService.addEvaluationToCollectionIfMissing<IEvaluation>(
      this.evaluationsCollection,
      soumission.evaluation,
    );
    this.appelOffresSharedCollection = this.appelOffreService.addAppelOffreToCollectionIfMissing<IAppelOffre>(
      this.appelOffresSharedCollection,
      soumission.appelOffre,
    );
    this.candidatsSharedCollection = this.candidatService.addCandidatToCollectionIfMissing<ICandidat>(
      this.candidatsSharedCollection,
      soumission.candidat,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.evaluationService
      .query({ filter: 'soumission-is-null' })
      .pipe(map((res: HttpResponse<IEvaluation[]>) => res.body ?? []))
      .pipe(
        map((evaluations: IEvaluation[]) =>
          this.evaluationService.addEvaluationToCollectionIfMissing<IEvaluation>(evaluations, this.soumission?.evaluation),
        ),
      )
      .subscribe((evaluations: IEvaluation[]) => (this.evaluationsCollection = evaluations));

    this.appelOffreService
      .query()
      .pipe(map((res: HttpResponse<IAppelOffre[]>) => res.body ?? []))
      .pipe(
        map((appelOffres: IAppelOffre[]) =>
          this.appelOffreService.addAppelOffreToCollectionIfMissing<IAppelOffre>(appelOffres, this.soumission?.appelOffre),
        ),
      )
      .subscribe((appelOffres: IAppelOffre[]) => (this.appelOffresSharedCollection = appelOffres));

    this.candidatService
      .query()
      .pipe(map((res: HttpResponse<ICandidat[]>) => res.body ?? []))
      .pipe(
        map((candidats: ICandidat[]) =>
          this.candidatService.addCandidatToCollectionIfMissing<ICandidat>(candidats, this.soumission?.candidat),
        ),
      )
      .subscribe((candidats: ICandidat[]) => (this.candidatsSharedCollection = candidats));
  }
}
