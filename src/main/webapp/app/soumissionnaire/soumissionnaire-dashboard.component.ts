import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AlertService } from 'app/core/util/alert.service';
import { EvaluationAiService, IAiEvaluationResponse, IAppelOffreEvaluation } from 'app/entities/evaluation/service/evaluation-ai.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

// 1. Assurez-vous d'importer ou de définir l'interface de réponse
// (Exemple de structure basée sur ce qu'une IA renvoie généralement)
@Component({
  selector: 'app-soumissionnaire-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './soumissionnaire-dashboard.component.html',
  styleUrls: ['./soumissionnaire-dashboard.component.scss'],
})

// ... (imports identiques)
export class SoumissionnaireDashboardComponent implements OnInit, OnDestroy {
  private readonly evaluationService = inject(EvaluationAiService);
  private readonly alertService = inject(AlertService);
  private readonly destroy$ = new Subject<void>();

  // État du composant
  appelsOffres: IAppelOffreEvaluation[] = [];
  selectedAppelOffre: IAppelOffreEvaluation | null = null;
  selectedFiles: File[] = [];

  loadingAppelsOffres = false;
  evaluatingInProgress = false;
  evaluationStatus = '';
  evaluationResult: IAiEvaluationResponse | null = null;

  currentStep: 'select-appel' | 'select-docs' | 'evaluating' | 'results' = 'select-appel';

  ngOnInit(): void {
    this.loadAppelsOffres();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadAppelsOffres(): void {
    this.loadingAppelsOffres = true;
    this.evaluationService
      .getAppelsOffres()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: appels => {
          this.appelsOffres = appels;
          this.loadingAppelsOffres = false;
        },
        error: () => (this.loadingAppelsOffres = false),
      });
  }

  selectAppelOffre(appel: IAppelOffreEvaluation): void {
    this.selectedAppelOffre = appel;
    this.selectedFiles = [];
    this.currentStep = 'select-docs';
  }

  /**
   * Gestion cumulative des fichiers
   */
  onFilesSelected(event: any): void {
    const files = event.target.files;
    if (files) {
      const newFiles = Array.from(files) as File[];

      // On AJOUTE les nouveaux fichiers à la liste existante
      // On utilise un petit filtre pour éviter les doublons (même nom et même taille)
      newFiles.forEach(file => {
        const exists = this.selectedFiles.some(f => f.name === file.name && f.size === file.size);
        if (!exists) {
          this.selectedFiles.push(file);
        }
      });
    }
    // IMPORTANT : On vide l'input pour pouvoir sélectionner à nouveau le même fichier si besoin
    event.target.value = '';
  }

  /**
   * Supprimer un fichier de la liste avant l'analyse
   */
  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
  }

  lancerEvaluation(): void {
    if (!this.selectedAppelOffre || this.selectedFiles.length === 0) {
      this.alertService.addAlert({ type: 'warning', message: 'Veuillez ajouter des documents.' });
      return;
    }

    this.evaluatingInProgress = true;
    this.currentStep = 'evaluating';
    this.evaluationStatus = 'Analyse stratégique de vos fichiers en cours...';

    this.evaluationService
      .simulerEvaluationAvecFichiers(this.selectedFiles, this.selectedAppelOffre.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: res => {
          this.evaluationResult = res;
          this.evaluatingInProgress = false;
          this.currentStep = 'results';
        },
        error: () => {
          this.evaluatingInProgress = false;
          this.currentStep = 'select-docs';
          this.alertService.addAlert({ type: 'danger', message: "Erreur lors de l'analyse." });
        },
      });
  }

  backToAppelSelection(): void {
    this.currentStep = 'select-appel';
    this.selectedAppelOffre = null;
    this.selectedFiles = [];
  }

  continueEvaluation(): void {
    this.currentStep = 'select-appel';
    this.selectedAppelOffre = null;
    this.selectedFiles = [];
    this.evaluationResult = null;
  }

  getStatutClass(statut?: string): string {
    return statut === 'OUVERT' ? 'badge bg-success' : 'badge bg-secondary';
  }
}
