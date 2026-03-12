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

describe('Soumission e2e test', () => {
  const soumissionPageUrl = '/soumission';
  const soumissionPageUrlPattern = new RegExp('/soumission(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const soumissionSample = {};

  let soumission;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/soumissions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/soumissions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/soumissions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (soumission) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/soumissions/${soumission.id}`,
      }).then(() => {
        soumission = undefined;
      });
    }
  });

  it('Soumissions menu should load Soumissions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('soumission');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Soumission').should('exist');
    cy.url().should('match', soumissionPageUrlPattern);
  });

  describe('Soumission page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(soumissionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Soumission page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/soumission/new$'));
        cy.getEntityCreateUpdateHeading('Soumission');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', soumissionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/soumissions',
          body: soumissionSample,
        }).then(({ body }) => {
          soumission = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/soumissions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/soumissions?page=0&size=20>; rel="last",<http://localhost/api/soumissions?page=0&size=20>; rel="first"',
              },
              body: [soumission],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(soumissionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Soumission page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('soumission');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', soumissionPageUrlPattern);
      });

      it('edit button click should load edit Soumission page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Soumission');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', soumissionPageUrlPattern);
      });

      it('edit button click should load edit Soumission page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Soumission');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', soumissionPageUrlPattern);
      });

      it('last delete button click should delete instance of Soumission', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('soumission').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', soumissionPageUrlPattern);

        soumission = undefined;
      });
    });
  });

  describe('new Soumission page', () => {
    beforeEach(() => {
      cy.visit(`${soumissionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Soumission');
    });

    it('should create an instance of Soumission', () => {
      cy.get(`[data-cy="dateSoumission"]`).type('2026-03-12T06:08');
      cy.get(`[data-cy="dateSoumission"]`).blur();
      cy.get(`[data-cy="dateSoumission"]`).should('have.value', '2026-03-12T06:08');

      cy.get(`[data-cy="statut"]`).select('EN_ATTENTE');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        soumission = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', soumissionPageUrlPattern);
    });
  });
});
