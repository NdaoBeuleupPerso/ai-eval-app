import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { EvaluationAiService } from './evaluation-ai.service';
import { ApplicationConfigService } from 'app/core/config/application-config.service';

describe('EvaluationAiService', () => {
  let service: EvaluationAiService;
  let httpMock: HttpTestingController;
  // @ts-ignore
  let applicationConfigService: jasmine.SpyObj<ApplicationConfigService>;

  beforeEach(() => {
    const configServiceSpy = jasmine.createSpyObj('ApplicationConfigService', ['getEndpointFor']);
    configServiceSpy.getEndpointFor.and.returnValue('api/soumissionnaire');

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [EvaluationAiService, { provide: ApplicationConfigService, useValue: configServiceSpy }],
    });

    service = TestBed.inject(EvaluationAiService);
    httpMock = TestBed.inject(HttpTestingController);
    // @ts-ignore
    applicationConfigService = TestBed.inject(ApplicationConfigService) as jasmine.SpyObj<ApplicationConfigService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAppelsOffresMock', () => {
    it('should return mock appels offres', done => {
      service.getAppelsOffresMock().subscribe(appels => {
        expect(appels).toBeDefined();
        expect(appels.length).toBeGreaterThan(0);
        expect(appels[0]).toHaveProperty('reference');
        expect(appels[0]).toHaveProperty('titre');
        done();
      });
    });

    it('should contain valid appel offre structure', done => {
      service.getAppelsOffresMock().subscribe(appels => {
        const appel = appels[0];
        expect(appel.id).toBeDefined();
        expect(appel.reference).toMatch(/AO-\d{4}-\d{3}/);
        expect(['OUVERT', 'EN_COURS_EVALUATION', 'EVALUE', 'CLOTURE']).toContain(appel.statut);
        done();
      });
    });
  });

  describe('getAppelsOffres', () => {
    it('should return appels offres (currently using mock)', done => {
      service.getAppelsOffres().subscribe(appels => {
        expect(appels).toBeDefined();
        expect(Array.isArray(appels)).toBeTruthy();
        done();
      });
    });
  });

  describe('getDocumentsSoumissionMock', () => {
    it('should return mock documents for submission', done => {
      const soumissionId = 1;
      const appelOffreId = 1;

      service.getDocumentsSoumissionMock(soumissionId, appelOffreId).subscribe(docs => {
        expect(docs.soumissionId).toBe(soumissionId);
        expect(Array.isArray(docs.documents)).toBeTruthy();
        expect(docs.documents.length).toBeGreaterThan(0);
        done();
      });
    });

    it('should contain valid document structure', done => {
      service.getDocumentsSoumissionMock(1, 1).subscribe(docs => {
        const doc = docs.documents[0];
        expect(doc.id).toBeDefined();
        expect(doc.nom).toBeDefined();
        expect(doc.format).toBeDefined();
        expect(['OFFRE_TECHNIQUE', 'ATTESTATION', 'GARANTIE', 'PV_CONFORMITE', 'AUTRE']).toContain(doc.format);
        done();
      });
    });
  });

  describe('getDocumentsSoumission', () => {
    it('should return documents (currently using mock)', done => {
      service.getDocumentsSoumission(1, 1).subscribe(docs => {
        expect(docs).toBeDefined();
        expect(docs.soumissionId).toBe(1);
        expect(Array.isArray(docs.documents)).toBeTruthy();
        done();
      });
    });
  });

  describe('lancerEvaluationAi', () => {
    it('should post evaluation request', () => {
      const request = {
        soumissionId: 1,
        appelOffreId: 1,
        documentsIds: [1, 2, 3],
      };

      service.lancerEvaluationAi(request).subscribe(response => {
        expect(response.body).toBeDefined();
      });

      const req = httpMock.expectOne('api/soumissionnaire/evaluations/lancer');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(request);

      req.flush({ evaluationId: 1, status: 'EN_COURS' });
    });

    it('should handle evaluation error', () => {
      const request = {
        soumissionId: 1,
        appelOffreId: 1,
        documentsIds: [1, 2, 3],
      };

      service.lancerEvaluationAi(request).subscribe({
        next: () => fail('should have failed'),
        error: error => {
          expect(error.status).toBe(400);
        },
      });

      const req = httpMock.expectOne('api/soumissionnaire/evaluations/lancer');
      req.flush('Bad Request', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('getStatusEvaluation', () => {
    it('should get evaluation status', () => {
      const evaluationId = 1;

      service.getStatusEvaluation(evaluationId).subscribe(status => {
        expect(status).toBeDefined();
        expect(status.evaluationId).toBe(evaluationId);
      });

      const req = httpMock.expectOne('api/soumissionnaire/evaluations/1/status');
      expect(req.request.method).toBe('GET');

      req.flush({ evaluationId: 1, status: 'EN_COURS' });
    });
  });

  describe('getSoumissionnairesListMock', () => {
    it('should return mock soumissionnaires', done => {
      service.getSoumissionnairesListMock().subscribe(soumissionnaires => {
        expect(Array.isArray(soumissionnaires)).toBeTruthy();
        expect(soumissionnaires.length).toBeGreaterThan(0);
        expect(soumissionnaires[0]).toHaveProperty('nom');
        expect(soumissionnaires[0]).toHaveProperty('siret');
        expect(soumissionnaires[0]).toHaveProperty('email');
        done();
      });
    });
  });
});
