import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { DataUtils } from 'app/core/util/data-util.service';
import { IAppelOffre } from '../appel-offre.model';
import { AppelOffreService } from '../service/appel-offre.service';

@Component({
  standalone: true, // JHipster 8 utilise les standalone components par défaut
  selector: 'jhi-appel-offre-detail',
  templateUrl: './appel-offre-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe, SharedModule],
})
export class AppelOffreDetailComponent implements OnInit {
  // --- Propriétés ---
  appelOffre: IAppelOffre | null = null;
  isSaving = false;

  // --- Injections (Syntaxe moderne inject()) ---
  protected activatedRoute = inject(ActivatedRoute);
  protected appelOffreService = inject(AppelOffreService);
  protected dataUtils = inject(DataUtils);

  ngOnInit(): void {
    // Récupération des données de l'appel d'offre
    this.activatedRoute.data.subscribe(({ appelOffre }) => {
      this.appelOffre = appelOffre;
    });
  }

  /**
   * Méthode pour déclencher l'analyse IA globale
   */
  lancerEvaluationIA(): void {
    if (this.appelOffre?.id) {
      this.isSaving = true;
      this.appelOffreService.evaluerTout(this.appelOffre.id).subscribe({
        next: () => {
          this.isSaving = false;
          alert("L'analyse globale par l'IA a été lancée. Les rapports seront bientôt disponibles.");
        },
        error: () => {
          this.isSaving = false;
          alert("Une erreur est survenue lors du lancement de l'analyse IA.");
        },
      });
    }
  }

  // --- Méthodes utilitaires JHipster pour les Blobs/Fichiers ---

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
