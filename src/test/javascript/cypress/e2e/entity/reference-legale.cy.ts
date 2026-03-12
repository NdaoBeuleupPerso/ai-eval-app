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

describe('ReferenceLegale e2e test', () => {
  const referenceLegalePageUrl = '/reference-legale';
  const referenceLegalePageUrlPattern = new RegExp('/reference-legale(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const referenceLegaleSample = { titre: 'multiple efficace pourvu que', contenu: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=' };

  let referenceLegale;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/reference-legales+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/reference-legales').as('postEntityRequest');
    cy.intercept('DELETE', '/api/reference-legales/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (referenceLegale) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/reference-legales/${referenceLegale.id}`,
      }).then(() => {
        referenceLegale = undefined;
      });
    }
  });

  it('ReferenceLegales menu should load ReferenceLegales page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('reference-legale');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ReferenceLegale').should('exist');
    cy.url().should('match', referenceLegalePageUrlPattern);
  });

  describe('ReferenceLegale page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(referenceLegalePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ReferenceLegale page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/reference-legale/new$'));
        cy.getEntityCreateUpdateHeading('ReferenceLegale');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', referenceLegalePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/reference-legales',
          body: referenceLegaleSample,
        }).then(({ body }) => {
          referenceLegale = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/reference-legales+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/reference-legales?page=0&size=20>; rel="last",<http://localhost/api/reference-legales?page=0&size=20>; rel="first"',
              },
              body: [referenceLegale],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(referenceLegalePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ReferenceLegale page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('referenceLegale');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', referenceLegalePageUrlPattern);
      });

      it('edit button click should load edit ReferenceLegale page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ReferenceLegale');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', referenceLegalePageUrlPattern);
      });

      it('edit button click should load edit ReferenceLegale page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ReferenceLegale');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', referenceLegalePageUrlPattern);
      });

      it('last delete button click should delete instance of ReferenceLegale', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('referenceLegale').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', referenceLegalePageUrlPattern);

        referenceLegale = undefined;
      });
    });
  });

  describe('new ReferenceLegale page', () => {
    beforeEach(() => {
      cy.visit(`${referenceLegalePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ReferenceLegale');
    });

    it('should create an instance of ReferenceLegale', () => {
      cy.get(`[data-cy="titre"]`).type('descendre');
      cy.get(`[data-cy="titre"]`).should('have.value', 'descendre');

      cy.get(`[data-cy="contenu"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="contenu"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="qdrantUuid"]`).type('drelin moins gestionnaire');
      cy.get(`[data-cy="qdrantUuid"]`).should('have.value', 'drelin moins gestionnaire');

      cy.get(`[data-cy="source"]`).type('pschitt céans');
      cy.get(`[data-cy="source"]`).should('have.value', 'pschitt céans');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        referenceLegale = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', referenceLegalePageUrlPattern);
    });
  });
});
