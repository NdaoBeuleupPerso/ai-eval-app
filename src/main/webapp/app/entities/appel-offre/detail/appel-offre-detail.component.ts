import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router'; // Ajout de Router

import { DataUtils } from 'app/core/util/data-util.service';
import { CritereService } from 'app/entities/critere/service/critere.service'; // Import du service Critere
import { FormatMediumDatetimePipe } from 'app/shared/date';
import SharedModule from 'app/shared/shared.module';
import { IAppelOffre } from '../appel-offre.model';
import { AppelOffreService } from '../service/appel-offre.service';

@Component({
  standalone: true,
  selector: 'jhi-appel-offre-detail',
  templateUrl: './appel-offre-detail.component.html',
  styleUrls: ['./appel-offre-detail.component.scss'],
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class AppelOffreDetailComponent implements OnInit {
  // --- Propriétés ---
  appelOffre: IAppelOffre | null = null;
  isSaving = false;

  // --- Injections ---
  protected activatedRoute = inject(ActivatedRoute);
  protected router = inject(Router);
  protected appelOffreService = inject(AppelOffreService);
  protected critereService = inject(CritereService); // Injection du service Critere
  protected dataUtils = inject(DataUtils);

  ngOnInit(): void {
    // Récupération des données de l'appel d'offre via le resolver
    this.activatedRoute.data.subscribe(({ appelOffre }) => {
      this.appelOffre = appelOffre;
    });
  }

  /**
   * Appelle l'IA pour générer des suggestions de critères basées sur la description
   */
  genererCriteres(): void {
    const id = this.appelOffre?.id;
    if (id) {
      this.isSaving = true;
      this.critereService.suggestForAppelOffre(Number(id)).subscribe({
        next: () => {
          this.isSaving = false;
          // Rediriger vers la liste des critères filtrée pour cet Appel d'Offre
          this.router.navigate(['/critere'], { queryParams: { 'appelOffreId.equals': id } });
        },
        error: () => {
          this.isSaving = false;
          alert('Erreur lors de la génération des critères.');
        },
      });
    }
  }

  /**
   * Déclenche l'analyse IA de toutes les soumissions liées
   */

  lancerEvaluationIA(): void {
    if (this.appelOffre?.id) {
      this.isSaving = true;
      this.appelOffreService.evaluerTout(this.appelOffre.id).subscribe({
        next: () => {
          this.isSaving = false;
          alert("L'analyse globale par l'IA a été lancée. Les rapports seront bientôt disponibles dans l'onglet Évaluations.");
        },
        error: () => {
          this.isSaving = false;
          alert("Une erreur est survenue lors du lancement de l'analyse IA.");
        },
      });
    }
  }

  previousState(): void {
    window.history.back();
  }

  // Note: openFile et byteSize ne sont plus utiles pour 'description' car c'est du String (TEXT)
  // mais peuvent servir pour d'autres champs si nécessaire.
  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }
}
