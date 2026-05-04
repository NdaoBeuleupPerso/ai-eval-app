import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import SharedModule from 'app/shared/shared.module';

import { IAppelOffre } from 'app/entities/appel-offre/appel-offre.model';
import { AppelOffreService } from 'app/entities/appel-offre/service/appel-offre.service';
import { StatutCritere } from 'app/entities/enumerations/statut-critere.model'; // AJOUTER CET IMPORT
import { TypeCritere } from 'app/entities/enumerations/type-critere.model';
import { ICritere } from '../critere.model';
import { CritereService } from '../service/critere.service';
import { CritereFormGroup, CritereFormService } from './critere-form.service';

@Component({
  selector: 'jhi-critere-update',
  templateUrl: './critere-update.component.html',
  standalone: true,
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CritereUpdateComponent implements OnInit {
  isSaving = false;
  critere: ICritere | null = null;

  // LISTES POUR LES MENUS DÉROULANTS
  typeCritereValues = Object.keys(TypeCritere);
  statutCritereValues = Object.keys(StatutCritere); // AJOUTER CETTE LIGNE (Règle l'erreur TS2339)

  appelOffresSharedCollection: IAppelOffre[] = [];

  protected critereService = inject(CritereService);
  protected critereFormService = inject(CritereFormService);
  protected appelOffreService = inject(AppelOffreService);
  protected activatedRoute = inject(ActivatedRoute);

  editForm: CritereFormGroup = this.critereFormService.createCritereFormGroup();

  compareAppelOffre = (o1: IAppelOffre | null, o2: IAppelOffre | null): boolean => this.appelOffreService.compareAppelOffre(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ critere }) => {
      this.critere = critere;
      // SI C'EST UNE CRÉATION, ON CHERCHE L'ID DANS L'URL
      if (!critere?.id) {
        const aoId = this.activatedRoute.snapshot.queryParamMap.get('appelOffreId');
        if (aoId) {
          // On simule un objet partiel pour le formulaire
          this.updateForm({ ...critere, appelOffre: { id: Number(aoId) } });
        }
      } else if (critere) {
        this.updateForm(critere);
      }
      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const critere = this.critereFormService.getCritere(this.editForm);
    if (critere.id !== null) {
      this.subscribeToSaveResponse(this.critereService.update(critere));
    } else {
      this.subscribeToSaveResponse(this.critereService.create(critere));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICritere>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {}

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(critere: ICritere): void {
    this.critere = critere;
    this.critereFormService.resetForm(this.editForm, critere);

    this.appelOffresSharedCollection = this.appelOffreService.addAppelOffreToCollectionIfMissing<IAppelOffre>(
      this.appelOffresSharedCollection,
      critere.appelOffre,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.appelOffreService
      .query()
      .pipe(map((res: HttpResponse<IAppelOffre[]>) => res.body ?? []))
      .pipe(
        map((appelOffres: IAppelOffre[]) =>
          this.appelOffreService.addAppelOffreToCollectionIfMissing<IAppelOffre>(appelOffres, this.critere?.appelOffre),
        ),
      )
      .subscribe((appelOffres: IAppelOffre[]) => (this.appelOffresSharedCollection = appelOffres));
  }
}
