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

describe('Critere e2e test', () => {
  const criterePageUrl = '/critere';
  const criterePageUrlPattern = new RegExp('/critere(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const critereSample = { nom: 'pis plouf', ponderation: 7177.56, categorie: 'FINANCIER' };

  let critere;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/criteres+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/criteres').as('postEntityRequest');
    cy.intercept('DELETE', '/api/criteres/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (critere) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/criteres/${critere.id}`,
      }).then(() => {
        critere = undefined;
      });
    }
  });

  it('Criteres menu should load Criteres page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('critere');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Critere').should('exist');
    cy.url().should('match', criterePageUrlPattern);
  });

  describe('Critere page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(criterePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Critere page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/critere/new$'));
        cy.getEntityCreateUpdateHeading('Critere');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', criterePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/criteres',
          body: critereSample,
        }).then(({ body }) => {
          critere = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/criteres+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/criteres?page=0&size=20>; rel="last",<http://localhost/api/criteres?page=0&size=20>; rel="first"',
              },
              body: [critere],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(criterePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Critere page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('critere');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', criterePageUrlPattern);
      });

      it('edit button click should load edit Critere page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Critere');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', criterePageUrlPattern);
      });

      it('edit button click should load edit Critere page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Critere');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', criterePageUrlPattern);
      });

      it('last delete button click should delete instance of Critere', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('critere').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', criterePageUrlPattern);

        critere = undefined;
      });
    });
  });

  describe('new Critere page', () => {
    beforeEach(() => {
      cy.visit(`${criterePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Critere');
    });

    it('should create an instance of Critere', () => {
      cy.get(`[data-cy="nom"]`).type('anéantir');
      cy.get(`[data-cy="nom"]`).should('have.value', 'anéantir');

      cy.get(`[data-cy="ponderation"]`).type('29140.99');
      cy.get(`[data-cy="ponderation"]`).should('have.value', '29140.99');

      cy.get(`[data-cy="categorie"]`).select('FINANCIER');

      cy.get(`[data-cy="description"]`).type('administration quand');
      cy.get(`[data-cy="description"]`).should('have.value', 'administration quand');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        critere = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', criterePageUrlPattern);
    });
  });
});
