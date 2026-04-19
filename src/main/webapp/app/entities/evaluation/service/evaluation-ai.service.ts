import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { Observable } from 'rxjs';

export interface IAppelOffreEvaluation {
  id: number;
  reference: string;
  titre: string;
  description?: string;
  dateCloture?: string;
  statut?: string;
}

export interface ISoumisisonnaireDocs {
  soumissionId: number;
  documents: {
    id: number;
    nom: string;
    format: string;
    url?: string;
    idExterne?: string;
  }[];
}

export interface IAiEvaluationRequest {
  soumissionId: number;
  appelOffreId: number;
  documentsIds: number[];
}

export interface IAiEvaluationResponse {
  id?: number;
  scoreGlobal?: number;
  scoreAdmin?: number;
  scoreTech?: number;
  scoreFin?: number;
  rapportAnalyse?: string;
  dateEvaluation?: string;
  estValidee?: boolean;
  commentaireEvaluateur?: string;
}

@Injectable({ providedIn: 'root' })
export class EvaluationAiService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  // URL de base pointant vers SoumissionnaireEvaluationResource.java (@RequestMapping("/api/soumissionnaire"))
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/soumissionnaire');

  protected aiEvaluationUrl = this.applicationConfigService.getEndpointFor('api/evaluations');
  /**
   * RÉEL : Récupère les appels d'offres ouverts depuis le Backend
   */
  getAppelsOffres(): Observable<IAppelOffreEvaluation[]> {
    return this.http.get<IAppelOffreEvaluation[]>(`${this.resourceUrl}/appels-offres`);
  }

  // Ajoutez cette méthode dans EvaluationAiService
  simulerEvaluationAvecFichiers(fichiers: File[], appelOffreId: number): Observable<IAiEvaluationResponse> {
    const formData = new FormData();
    fichiers.forEach(file => formData.append('files', file));
    formData.append('appelOffreId', appelOffreId.toString());

    return this.http.post<IAiEvaluationResponse>(`${this.aiEvaluationUrl}/candidature/evaluate-files`, formData);
  }
  /**
   * RÉEL : Récupère les documents d'une soumission existante
   */
  getDocumentsSoumission(soumissionId: number, appelOffreId: number): Observable<ISoumisisonnaireDocs> {
    const params = new HttpParams().set('appelOffreId', appelOffreId.toString());
    return this.http.get<ISoumisisonnaireDocs>(`${this.resourceUrl}/soumissions/${soumissionId}/documents`, { params });
  }

  /**
   * RÉEL : Lance l'évaluation AI officielle (persiste en base)
   */
  lancerEvaluationAi(request: IAiEvaluationRequest): Observable<HttpResponse<IAiEvaluationResponse>> {
    return this.http.post<IAiEvaluationResponse>(`${this.resourceUrl}/candidature/evaluate`, request, { observe: 'response' });
  }

  /**
   * RÉEL : Simule une évaluation (Temporelle - sans création de soumission en base)
   * @param documentIds liste des IDs de documents uploadés
   * @param appelOffreId ID du marché visé
   */
  simulerEvaluationAi(documentIds: number[], appelOffreId: number): Observable<IAiEvaluationResponse> {
    const payload = { documentIds, appelOffreId };
    return this.http.post<IAiEvaluationResponse>(`${this.resourceUrl}/candidature/evaluate`, payload);
  }

  /**
   * RÉEL : Récupère le statut d'une évaluation en cours
   */
  getStatusEvaluation(evaluationId: number): Observable<IAiEvaluationResponse> {
    return this.http.get<IAiEvaluationResponse>(`${this.resourceUrl}/evaluations/${evaluationId}/status`);
  }

  /**
   * RÉEL (Admin uniquement) : Récupère la liste des soumissionnaires
   */
  getSoumissionnairesList(): Observable<any[]> {
    return this.http.get<any[]>(`${this.resourceUrl}/soumissionnaires`);
  }
}
