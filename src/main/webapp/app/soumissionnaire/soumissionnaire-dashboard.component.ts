import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { EvaluationAiService, IAppelOffreEvaluation, ISoumisisonnaireDocs } from 'app/entities/evaluation/service/evaluation-ai.service';
import { AlertService } from 'app/core/util/alert.service';

@Component({
  selector: 'app-soumissionnaire-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './soumissionnaire-dashboard.component.html',
  styleUrls: ['./soumissionnaire-dashboard.component.scss'],
})
export class SoumissionnaireDashboardComponent implements OnInit, OnDestroy {
  private readonly evaluationService = inject(EvaluationAiService);
  private readonly alertService = inject(AlertService);
  private readonly destroy$ = new Subject<void>();

  // État du composant
  appelsOffres: IAppelOffreEvaluation[] = [];
  selectedAppelOffre: IAppelOffreEvaluation | null = null;
  soumissionDocuments: ISoumisisonnaireDocs | null = null;
  selectedDocuments: number[] = [];

  loadingAppelsOffres = false;
  loadingDocuments = false;
  evaluatingInProgress = false;
  evaluationStatus = '';

  currentStep: 'select-appel' | 'select-docs' | 'evaluating' | 'results' = 'select-appel';

  ngOnInit(): void {
    this.loadAppelsOffres();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Charger la liste des appels d'offres disponibles
   */
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
        error: err => {
          console.error("Erreur lors du chargement des appels d'offres:", err);
          this.alertService.addAlert({
            type: 'danger',
            message: "Erreur lors du chargement des appels d'offres",
            timeout: 5000,
          });
          this.loadingAppelsOffres = false;
        },
      });
  }

  /**
   * Sélectionner un appel d'offre et charger ses documents
   */
  selectAppelOffre(appel: IAppelOffreEvaluation): void {
    this.selectedAppelOffre = appel;
    this.selectedDocuments = [];
    this.loadDocuments();
  }

  /**
   * Charger les documents d'une soumission pour l'appel sélectionné
   */
  loadDocuments(): void {
    if (!this.selectedAppelOffre) {
      return;
    }

    this.loadingDocuments = true;
    // Pour l'instant, on utilise une soumission simulée
    const soumissionId = 1;

    this.evaluationService
      .getDocumentsSoumission(soumissionId, this.selectedAppelOffre.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: docs => {
          this.soumissionDocuments = docs;
          this.currentStep = 'select-docs';
          this.loadingDocuments = false;
        },
        error: err => {
          console.error('Erreur lors du chargement des documents:', err);
          this.alertService.addAlert({
            type: 'danger',
            message: 'Erreur lors du chargement des documents',
            timeout: 5000,
          });
          this.loadingDocuments = false;
        },
      });
  }

  /**
   * Basculer la sélection d'un document
   */
  toggleDocument(docId: number): void {
    const index = this.selectedDocuments.indexOf(docId);
    if (index > -1) {
      this.selectedDocuments.splice(index, 1);
    } else {
      this.selectedDocuments.push(docId);
    }
  }

  /**
   * Cocher tous les documents
   */
  selectAllDocuments(): void {
    if (!this.soumissionDocuments) {
      return;
    }

    this.selectedDocuments = this.soumissionDocuments.documents.map(doc => doc.id);
  }

  /**
   * Décocher tous les documents
   */
  clearAllDocuments(): void {
    this.selectedDocuments = [];
  }

  /**
   * Retour à la sélection de l'appel d'offre
   */
  backToAppelSelection(): void {
    this.currentStep = 'select-appel';
    this.selectedAppelOffre = null;
    this.soumissionDocuments = null;
    this.selectedDocuments = [];
  }

  /**
   * Lancer l'évaluation AI pour les documents sélectionnés
   */
  lancerEvaluation(): void {
    if (!this.selectedAppelOffre || this.selectedDocuments.length === 0 || !this.soumissionDocuments) {
      this.alertService.addAlert({
        type: 'warning',
        message: 'Veuillez sélectionner au moins un document',
        timeout: 5000,
      });
      return;
    }

    this.evaluatingInProgress = true;
    this.currentStep = 'evaluating';
    this.evaluationStatus = "Initialisation de l'évaluation...";

    const request = {
      soumissionId: this.soumissionDocuments.soumissionId,
      appelOffreId: this.selectedAppelOffre.id,
      documentsIds: this.selectedDocuments,
    };

    this.evaluationService
      .lancerEvaluationAi(request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: response => {
          console.log('Évaluation lancée avec succès:', response.body);
          this.evaluatingInProgress = false;
          this.currentStep = 'results';
          this.alertService.addAlert({
            type: 'success',
            message: 'Évaluation lancée avec succès!',
            timeout: 5000,
          });
        },
        error: err => {
          console.error("Erreur lors du lancement de l'évaluation:", err);
          this.alertService.addAlert({
            type: 'danger',
            message: "Erreur lors du lancement de l'évaluation",
            timeout: 5000,
          });
          this.evaluatingInProgress = false;
          this.currentStep = 'select-docs';
        },
      });
  }

  /**
   * Retour aux étapes précédentes
   */
  continueEvaluation(): void {
    this.currentStep = 'select-appel';
    this.selectedAppelOffre = null;
    this.soumissionDocuments = null;
    this.selectedDocuments = [];
  }

  /**
   * Vérifier si au moins un document est sélectionné
   */
  hasSelectedDocuments(): boolean {
    return this.selectedDocuments.length > 0;
  }

  /**
   * Formater le statut de l'appel d'offre
   */
  getStatutClass(statut?: string): string {
    switch (statut) {
      case 'OUVERT':
        return 'badge bg-success';
      case 'EN_COURS_EVALUATION':
        return 'badge bg-warning';
      case 'EVALUE':
        return 'badge bg-info';
      case 'CLOTURE':
        return 'badge bg-danger';
      default:
        return 'badge bg-secondary';
    }
  }
}
