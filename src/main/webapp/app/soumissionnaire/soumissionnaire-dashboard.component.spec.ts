import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpTestingController } from '@angular/common/http/testing';
import { SoumissionnaireDashboardComponent } from './soumissionnaire-dashboard.component';
import { EvaluationAiService } from 'app/entities/evaluation/service/evaluation-ai.service';
import { AlertService } from 'app/core/util/alert.service';
import { of, throwError } from 'rxjs';

describe('SoumissionnaireDashboardComponent', () => {
  let component: SoumissionnaireDashboardComponent;
  let fixture: ComponentFixture<SoumissionnaireDashboardComponent>;
  // @ts-ignore
  let evaluationService: jasmine.SpyObj<EvaluationAiService>;
  // @ts-ignore
  let alertService: jasmine.SpyObj<AlertService>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    const evaluationServiceSpy = jasmine.createSpyObj('EvaluationAiService', [
      'getAppelsOffres',
      'getDocumentsSoumission',
      'lancerEvaluationAi',
      'getStatusEvaluation',
    ]);

    const alertServiceSpy = jasmine.createSpyObj('AlertService', ['addAlert']);

    await TestBed.configureTestingModule({
      imports: [SoumissionnaireDashboardComponent],
      providers: [
        { provide: EvaluationAiService, useValue: evaluationServiceSpy },
        { provide: AlertService, useValue: alertServiceSpy },
      ],
    }).compileComponents();

    // @ts-ignore
    evaluationService = TestBed.inject(EvaluationAiService) as jasmine.SpyObj<EvaluationAiService>;
    // @ts-ignore
    alertService = TestBed.inject(AlertService) as jasmine.SpyObj<AlertService>;
    httpMock = TestBed.inject(HttpTestingController);

    fixture = TestBed.createComponent(SoumissionnaireDashboardComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('loadAppelsOffres', () => {
    it('should load appels offres on init', () => {
      const mockAppels = [{ id: 1, reference: 'AO-2024-001', titre: 'Test 1', statut: 'OUVERT' }];
      evaluationService.getAppelsOffres.and.returnValue(of(mockAppels));

      component.loadAppelsOffres();

      expect(evaluationService.getAppelsOffres).toHaveBeenCalled();
      expect(component.appelsOffres).toEqual(mockAppels);
    });

    it('should show error alert if loading fails', () => {
      evaluationService.getAppelsOffres.and.returnValue(throwError(() => new Error('Network error')));

      component.loadAppelsOffres();

      expect(alertService.addAlert).toHaveBeenCalledWith(
        jasmine.objectContaining({
          type: 'danger',
          message: "Erreur lors du chargement des appels d'offres",
        }),
      );
    });
  });

  describe('selectAppelOffre', () => {
    it('should select appel and load documents', () => {
      const mockAppel = { id: 1, reference: 'AO-2024-001', titre: 'Test' };
      const mockDocs = {
        soumissionId: 1,
        documents: [{ id: 1, nom: 'doc1.pdf', format: 'OFFRE_TECHNIQUE' }],
      };

      evaluationService.getDocumentsSoumission.and.returnValue(of(mockDocs));

      component.selectAppelOffre(mockAppel as any);

      expect(component.selectedAppelOffre).toEqual(mockAppel);
      expect(evaluationService.getDocumentsSoumission).toHaveBeenCalled();
    });
  });

  describe('lancerEvaluation', () => {
    it('should launch evaluation with selected documents', () => {
      component.selectedAppelOffre = { id: 1, reference: 'AO-2024-001', titre: 'Test' };
      component.soumissionDocuments = {
        soumissionId: 1,
        documents: [{ id: 1, nom: 'doc1.pdf', format: 'OFFRE_TECHNIQUE' }],
      };
      component.selectedDocuments = [1];

      const mockResponse = { body: { evaluationId: 1, status: 'EN_COURS' } };
      evaluationService.lancerEvaluationAi.and.returnValue(of(mockResponse as any));

      component.lancerEvaluation();

      expect(evaluationService.lancerEvaluationAi).toHaveBeenCalledWith(
        jasmine.objectContaining({
          soumissionId: 1,
          appelOffreId: 1,
          documentsIds: [1],
        }),
      );
      expect(component.currentStep).toBe('results');
    });

    it('should show error alert if evaluation fails', () => {
      component.selectedAppelOffre = { id: 1, reference: 'AO-2024-001', titre: 'Test' };
      component.soumissionDocuments = {
        soumissionId: 1,
        documents: [{ id: 1, nom: 'doc1.pdf', format: 'OFFRE_TECHNIQUE' }],
      };
      component.selectedDocuments = [1];

      evaluationService.lancerEvaluationAi.and.returnValue(throwError(() => new Error('API error')));

      component.lancerEvaluation();

      expect(alertService.addAlert).toHaveBeenCalledWith(
        jasmine.objectContaining({
          type: 'danger',
          message: "Erreur lors du lancement de l'évaluation",
        }),
      );
    });

    it('should warn if no documents selected', () => {
      component.selectedAppelOffre = { id: 1, reference: 'AO-2024-001', titre: 'Test' };
      component.selectedDocuments = [];

      component.lancerEvaluation();

      expect(alertService.addAlert).toHaveBeenCalledWith(
        jasmine.objectContaining({
          type: 'warning',
          message: 'Veuillez sélectionner au moins un document',
        }),
      );
    });
  });

  describe('document selection', () => {
    it('should toggle document selection', () => {
      component.selectedDocuments = [1];
      component.toggleDocument(1);
      expect(component.selectedDocuments).toEqual([]);

      component.toggleDocument(1);
      expect(component.selectedDocuments).toEqual([1]);
    });

    it('should select all documents', () => {
      component.soumissionDocuments = {
        soumissionId: 1,
        documents: [
          { id: 1, nom: 'doc1.pdf', format: 'OFFRE_TECHNIQUE' },
          { id: 2, nom: 'doc2.pdf', format: 'ATTESTATION' },
        ],
      };

      component.selectAllDocuments();

      expect(component.selectedDocuments).toEqual([1, 2]);
    });

    it('should clear all documents', () => {
      component.selectedDocuments = [1, 2];
      component.clearAllDocuments();
      expect(component.selectedDocuments).toEqual([]);
    });
  });

  describe('navigation', () => {
    it('should reset state when going back', () => {
      component.currentStep = 'select-docs';
      component.selectedAppelOffre = { id: 1, reference: 'AO-2024-001', titre: 'Test' };
      component.selectedDocuments = [1, 2];

      component.backToAppelSelection();

      expect(component.currentStep).toBe('select-appel');
      expect(component.selectedAppelOffre).toBeNull();
      expect(component.selectedDocuments).toEqual([]);
    });
  });

  describe('utility methods', () => {
    it('should check if documents are selected', () => {
      component.selectedDocuments = [];
      expect(component.hasSelectedDocuments()).toBeFalsy();

      component.selectedDocuments = [1];
      expect(component.hasSelectedDocuments()).toBeTruthy();
    });

    it('should format status class correctly', () => {
      expect(component.getStatutClass('OUVERT')).toBe('badge bg-success');
      expect(component.getStatutClass('EN_COURS_EVALUATION')).toBe('badge bg-warning');
      expect(component.getStatutClass('EVALUE')).toBe('badge bg-info');
      expect(component.getStatutClass('CLOTURE')).toBe('badge bg-danger');
    });
  });
});
