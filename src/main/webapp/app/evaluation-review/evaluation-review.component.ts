import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { IEvaluation } from '../entities/evaluation/evaluation.model';
import { EvaluationService } from '../entities/evaluation/service/evaluation.service';
import { AlertService } from 'app/core/util/alert.service';
import SharedModule from 'app/shared/shared.module';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { QuillModule } from 'ngx-quill';
import { MarkdownModule } from 'ngx-markdown';
@Component({
  standalone: true,
  selector: 'jhi-evaluation-review',
  templateUrl: './evaluation-review.component.html',
  styleUrls: ['./evaluation-review.component.scss'],
  imports: [SharedModule, FormsModule, CommonModule, MarkdownModule, QuillModule],
})
export class EvaluationReviewComponent implements OnInit {
  appelOffreId?: number;
  evaluations: IEvaluation[] = [];
  selectedEval?: IEvaluation;
  isLoading = false;
  isSaving = false;
  showPreview = false;

  constructor(
    protected evaluationService: EvaluationService,
    protected activatedRoute: ActivatedRoute,
    protected alertService: AlertService,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      this.appelOffreId = params['id'];
      if (this.appelOffreId) {
        this.loadAll();
      }
    });
  }

  // 1. CHARGEMENT ET NETTOYAGE GLOBAL
  loadAll(): void {
    this.isLoading = true;
    if (this.appelOffreId) {
      this.evaluationService.queryByAppelOffre(this.appelOffreId).subscribe({
        // Dans loadAll, modifiez la fin du next :
        next: (res: HttpResponse<IEvaluation[]>) => {
          this.evaluations = res.body ?? [];
          this.isLoading = false;

          // Sélection auto du premier candidat en allant chercher ses données complètes
          if (this.evaluations.length > 0 && !this.selectedEval) {
            this.selectEvaluation(this.evaluations[0]);
          }
        },
        error: () => (this.isLoading = false),
      });
    }
  }

  // 2. FONCTION DE NETTOYAGE UNIQUE ET ROBUSTE (Celle que tu as fournie)
  private processAIContent(text: string | null | undefined): string {
    if (!text) return '';
    let cleaned = text;

    // A. Supprimer les blocs de code Markdown si l'IA en a mis
    cleaned = cleaned.replace(/```html/gi, '').replace(/```/gi, '');

    // B. IMPORTANT : Pour Quill (HTML), on cherche le premier tag HTML <h
    // Si vous cherchez '#', ça ne marchera pas sur du HTML !
    const firstTag = cleaned.indexOf('<h');
    if (firstTag !== -1) {
      cleaned = cleaned.substring(firstTag);
    }

    // C. Supprimer le bloc [METADATA]
    const metadataTag = cleaned.indexOf('[METADATA]');
    if (metadataTag !== -1) {
      cleaned = cleaned.substring(0, metadataTag);
    }

    return cleaned.trim();
  }
  // private processAIContent(text: string | null | undefined): string {
  //   if (!text) return '';
  //   let cleaned = text;
  //
  //   // A. Supprimer les balises de code Markdown (backticks)
  //   cleaned = cleaned.replace(/```markdown/gi, '').replace(/```/gi, '');
  //
  //   // B. Supprimer l'intro inutile (on garde tout à partir du premier titre #)
  //   const firstTitle = cleaned.indexOf('#');
  //   if (firstTitle !== -1) {
  //     cleaned = cleaned.substring(firstTitle);
  //   }
  //
  //   // C. Supprimer le bloc technique [METADATA] s'il est présent
  //   const metadataTag = cleaned.indexOf('[METADATA]');
  //   if (metadataTag !== -1) {
  //     cleaned = cleaned.substring(0, metadataTag);
  //   }
  //
  //   return cleaned.trim();
  // }

  // 3. ACTIONS DE L'INTERFACE
  // selectEvaluation(evaluation: IEvaluation): void {
  //   this.selectedEval = evaluation;
  //   this.showPreview = false;
  // }
  selectEvaluation(evaluation: IEvaluation): void {
    this.isLoading = true;
    this.evaluationService.find(evaluation.id).subscribe({
      next: res => {
        // CORRECTION ICI : on utilise res.body (le contenu complet venant de la DB)
        if (res.body) {
          this.selectedEval = {
            ...res.body,
            rapportAnalyse: this.processAIContent(res.body.rapportAnalyse),
          };
        }
        this.showPreview = false;
        this.isLoading = false;
      },
      error: () => (this.isLoading = false),
    });
  }

  togglePreview(): void {
    this.showPreview = !this.showPreview;
  }

  saveCurrentCorrection(): void {
    if (this.selectedEval) {
      this.isSaving = true;
      this.subscribeToSaveResponse(this.evaluationService.update(this.selectedEval));
    }
  }

  validateCurrent(): void {
    if (this.selectedEval) {
      this.selectedEval.estValidee = true;
      this.isSaving = true;
      this.subscribeToSaveResponse(this.evaluationService.update(this.selectedEval));
    }
  }

  allEvaluationsValidated(): boolean {
    return this.evaluations.length > 0 && this.evaluations.every(e => e.estValidee);
  }

  generateGlobalPV(): void {
    if (this.appelOffreId) {
      this.evaluationService.generateFinalPV(this.appelOffreId).subscribe({
        next: () => this.alertService.addAlert({ type: 'success', message: 'PV Global généré !' }),
      });
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEvaluation>>): void {
    result.pipe(finalize(() => (this.isSaving = false))).subscribe({
      next: () => this.alertService.addAlert({ type: 'success', message: 'Enregistré' }),
      error: () => this.alertService.addAlert({ type: 'danger', message: 'Erreur' }),
    });
  }
}
