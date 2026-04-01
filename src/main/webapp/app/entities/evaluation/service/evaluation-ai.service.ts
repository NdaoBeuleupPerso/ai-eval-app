import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApplicationConfigService } from 'app/core/config/application-config.service';

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
  evaluationId: number;
  scoreGlobal: number;
  scoreAdmin?: number;
  scoreTech?: number;
  scoreFin?: number;
  rapportAnalyse: string;
  status: string;
  message?: string;
}

@Injectable({ providedIn: 'root' })
export class EvaluationAiService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/soumissionnaire');

  /**
   * Récupère les appels d'offres ouverts pour évaluation
   * Note: Mock pour l'instant, sera remplacé par l'endpoint réel
   */
  getAppelsOffresMock(): Observable<IAppelOffreEvaluation[]> {
    // Données simulées
    const mockData: IAppelOffreEvaluation[] = [
      {
        id: 1,
        reference: 'AO-2024-001',
        titre: 'Développement Application Web',
        description: "Création d'une application web pour gestion des ressources",
        dateCloture: '2024-06-30',
        statut: 'OUVERT',
      },
      {
        id: 2,
        reference: 'AO-2024-002',
        titre: 'Maintenance Infrastructure IT',
        description: 'Contrat de maintenance pour infrastructure IT sur 3 ans',
        dateCloture: '2024-05-15',
        statut: 'OUVERT',
      },
      {
        id: 3,
        reference: 'AO-2024-003',
        titre: 'Audit de Sécurité',
        description: 'Audit complet de sécurité informatique',
        dateCloture: '2024-07-20',
        statut: 'EN_COURS_EVALUATION',
      },
    ];

    return new Observable(observer => {
      observer.next(mockData);
      observer.complete();
    });
  }

  /**
   * Récupère les appels d'offres ouverts pour évaluation - Endpoint réel (quand disponible)
   */
  getAppelsOffres(): Observable<IAppelOffreEvaluation[]> {
    // Pour l'instant, utilise les données simulées
    return this.getAppelsOffresMock();
    // Futur: return this.http.get<IAppelOffreEvaluation[]>(`${this.resourceUrl}/appels-offres`, { observe: 'response' });
  }

  /**
   * Récupère les documents d'une soumission
   * Note: Mock pour l'instant
   */
  getDocumentsSoumissionMock(soumissionId: number, appelOffreId: number): Observable<ISoumisisonnaireDocs> {
    const mockDocs: ISoumisisonnaireDocs = {
      soumissionId,
      documents: [
        {
          id: 1,
          nom: 'Offre_Technique_2024.pdf',
          format: 'OFFRE_TECHNIQUE',
          url: '/documents/offre-tech-1.pdf',
          idExterne: 'doc-001',
        },
        {
          id: 2,
          nom: 'Attestation_Financiere.pdf',
          format: 'ATTESTATION',
          url: '/documents/attestation-1.pdf',
          idExterne: 'doc-002',
        },
        {
          id: 3,
          nom: 'Garanties_Contract.pdf',
          format: 'GARANTIE',
          url: '/documents/garantie-1.pdf',
          idExterne: 'doc-003',
        },
        {
          id: 4,
          nom: 'PV_Conformite.pdf',
          format: 'PV_CONFORMITE',
          url: '/documents/pv-conformite-1.pdf',
          idExterne: 'doc-004',
        },
      ],
    };

    return new Observable(observer => {
      observer.next(mockDocs);
      observer.complete();
    });
  }

  /**
   * Récupère les documents d'une soumission - Endpoint réel (quand disponible)
   */
  getDocumentsSoumission(soumissionId: number, appelOffreId: number): Observable<ISoumisisonnaireDocs> {
    // Pour l'instant, utilise les données simulées
    return this.getDocumentsSoumissionMock(soumissionId, appelOffreId);
    // Futur: return this.http.get<ISoumisisonnaireDocs>(
    //   `${this.resourceUrl}/soumissions/${soumissionId}/documents?appelOffreId=${appelOffreId}`
    // );
  }

  /**
   * Lance une évaluation AI pour les documents sélectionnés
   */
  lancerEvaluationAi(request: IAiEvaluationRequest): Observable<HttpResponse<IAiEvaluationResponse>> {
    return this.http.post<IAiEvaluationResponse>(`${this.resourceUrl}/evaluations/lancer`, request, { observe: 'response' });
  }

  /**
   * Récupère le statut d'une évaluation en cours
   */
  getStatusEvaluation(evaluationId: number): Observable<IAiEvaluationResponse> {
    return this.http.get<IAiEvaluationResponse>(`${this.resourceUrl}/evaluations/${evaluationId}/status`);
  }

  /**
   * Récupère la liste des soumissionnaires (pour l'admin)
   * Note: Mock pour l'instant
   */
  getSoumissionnairesListMock(): Observable<any[]> {
    const mockSoumissionnaires = [
      {
        id: 1,
        nom: 'Société ABC',
        siret: '12345678901234',
        email: 'contact@abc-company.fr',
      },
      {
        id: 2,
        nom: 'Entreprise XYZ',
        siret: '98765432109876',
        email: 'contact@xyz-corp.fr',
      },
    ];

    return new Observable(observer => {
      observer.next(mockSoumissionnaires);
      observer.complete();
    });
  }
}
