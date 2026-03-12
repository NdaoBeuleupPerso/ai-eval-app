import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('TraceAudit e2e test', () => {
  const traceAuditPageUrl = '/trace-audit';
  const traceAuditPageUrlPattern = new RegExp('/trace-audit(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const traceAuditSample = { action: 'en bas de', horodatage: '2026-03-12T00:23:30.168Z' };

  let traceAudit;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trace-audits+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trace-audits').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trace-audits/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (traceAudit) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trace-audits/${traceAudit.id}`,
      }).then(() => {
        traceAudit = undefined;
      });
    }
  });

  it('TraceAudits menu should load TraceAudits page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trace-audit');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TraceAudit').should('exist');
    cy.url().should('match', traceAuditPageUrlPattern);
  });

  describe('TraceAudit page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(traceAuditPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TraceAudit page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trace-audit/new$'));
        cy.getEntityCreateUpdateHeading('TraceAudit');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', traceAuditPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trace-audits',
          body: traceAuditSample,
        }).then(({ body }) => {
          traceAudit = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trace-audits+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/trace-audits?page=0&size=20>; rel="last",<http://localhost/api/trace-audits?page=0&size=20>; rel="first"',
              },
              body: [traceAudit],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(traceAuditPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TraceAudit page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('traceAudit');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', traceAuditPageUrlPattern);
      });

      it('edit button click should load edit TraceAudit page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TraceAudit');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', traceAuditPageUrlPattern);
      });

      it('edit button click should load edit TraceAudit page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TraceAudit');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', traceAuditPageUrlPattern);
      });

      it('last delete button click should delete instance of TraceAudit', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('traceAudit').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', traceAuditPageUrlPattern);

        traceAudit = undefined;
      });
    });
  });

  describe('new TraceAudit page', () => {
    beforeEach(() => {
      cy.visit(`${traceAuditPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TraceAudit');
    });

    it('should create an instance of TraceAudit', () => {
      cy.get(`[data-cy="action"]`).type('plouf à travers');
      cy.get(`[data-cy="action"]`).should('have.value', 'plouf à travers');

      cy.get(`[data-cy="horodatage"]`).type('2026-03-12T11:51');
      cy.get(`[data-cy="horodatage"]`).blur();
      cy.get(`[data-cy="horodatage"]`).should('have.value', '2026-03-12T11:51');

      cy.get(`[data-cy="details"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="details"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="identifiantUtilisateur"]`).type('juriste à partir de');
      cy.get(`[data-cy="identifiantUtilisateur"]`).should('have.value', 'juriste à partir de');

      cy.get(`[data-cy="promptUtilise"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="promptUtilise"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        traceAudit = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', traceAuditPageUrlPattern);
    });
  });
});
