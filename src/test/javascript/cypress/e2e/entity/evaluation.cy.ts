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

describe('Evaluation e2e test', () => {
  const evaluationPageUrl = '/evaluation';
  const evaluationPageUrlPattern = new RegExp('/evaluation(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const evaluationSample = {};

  let evaluation;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/evaluations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/evaluations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/evaluations/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (evaluation) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/evaluations/${evaluation.id}`,
      }).then(() => {
        evaluation = undefined;
      });
    }
  });

  it('Evaluations menu should load Evaluations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('evaluation');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Evaluation').should('exist');
    cy.url().should('match', evaluationPageUrlPattern);
  });

  describe('Evaluation page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(evaluationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Evaluation page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/evaluation/new$'));
        cy.getEntityCreateUpdateHeading('Evaluation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', evaluationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/evaluations',
          body: evaluationSample,
        }).then(({ body }) => {
          evaluation = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/evaluations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/evaluations?page=0&size=20>; rel="last",<http://localhost/api/evaluations?page=0&size=20>; rel="first"',
              },
              body: [evaluation],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(evaluationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Evaluation page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('evaluation');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', evaluationPageUrlPattern);
      });

      it('edit button click should load edit Evaluation page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Evaluation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', evaluationPageUrlPattern);
      });

      it('edit button click should load edit Evaluation page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Evaluation');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', evaluationPageUrlPattern);
      });

      it('last delete button click should delete instance of Evaluation', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('evaluation').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', evaluationPageUrlPattern);

        evaluation = undefined;
      });
    });
  });

  describe('new Evaluation page', () => {
    beforeEach(() => {
      cy.visit(`${evaluationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Evaluation');
    });

    it('should create an instance of Evaluation', () => {
      cy.get(`[data-cy="scoreGlobal"]`).type('1410.45');
      cy.get(`[data-cy="scoreGlobal"]`).should('have.value', '1410.45');

      cy.get(`[data-cy="scoreAdmin"]`).type('5318.79');
      cy.get(`[data-cy="scoreAdmin"]`).should('have.value', '5318.79');

      cy.get(`[data-cy="scoreTech"]`).type('3021.73');
      cy.get(`[data-cy="scoreTech"]`).should('have.value', '3021.73');

      cy.get(`[data-cy="scoreFin"]`).type('30178.41');
      cy.get(`[data-cy="scoreFin"]`).should('have.value', '30178.41');

      cy.get(`[data-cy="rapportAnalyse"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="rapportAnalyse"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.setFieldImageAsBytesOfEntity('documentPv', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="dateEvaluation"]`).type('2026-03-11T23:41');
      cy.get(`[data-cy="dateEvaluation"]`).blur();
      cy.get(`[data-cy="dateEvaluation"]`).should('have.value', '2026-03-11T23:41');

      cy.get(`[data-cy="estValidee"]`).should('not.be.checked');
      cy.get(`[data-cy="estValidee"]`).click();
      cy.get(`[data-cy="estValidee"]`).should('be.checked');

      cy.get(`[data-cy="commentaireEvaluateur"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="commentaireEvaluateur"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        evaluation = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', evaluationPageUrlPattern);
    });
  });
});
