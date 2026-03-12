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

describe('AppelOffre e2e test', () => {
  const appelOffrePageUrl = '/appel-offre';
  const appelOffrePageUrlPattern = new RegExp('/appel-offre(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const appelOffreSample = { reference: 'mieux consacrer', titre: 'lever' };

  let appelOffre;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/appel-offres+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/appel-offres').as('postEntityRequest');
    cy.intercept('DELETE', '/api/appel-offres/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (appelOffre) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/appel-offres/${appelOffre.id}`,
      }).then(() => {
        appelOffre = undefined;
      });
    }
  });

  it('AppelOffres menu should load AppelOffres page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('appel-offre');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('AppelOffre').should('exist');
    cy.url().should('match', appelOffrePageUrlPattern);
  });

  describe('AppelOffre page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(appelOffrePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create AppelOffre page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/appel-offre/new$'));
        cy.getEntityCreateUpdateHeading('AppelOffre');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', appelOffrePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/appel-offres',
          body: appelOffreSample,
        }).then(({ body }) => {
          appelOffre = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/appel-offres+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/appel-offres?page=0&size=20>; rel="last",<http://localhost/api/appel-offres?page=0&size=20>; rel="first"',
              },
              body: [appelOffre],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(appelOffrePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details AppelOffre page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('appelOffre');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', appelOffrePageUrlPattern);
      });

      it('edit button click should load edit AppelOffre page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AppelOffre');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', appelOffrePageUrlPattern);
      });

      it('edit button click should load edit AppelOffre page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AppelOffre');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', appelOffrePageUrlPattern);
      });

      it('last delete button click should delete instance of AppelOffre', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('appelOffre').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', appelOffrePageUrlPattern);

        appelOffre = undefined;
      });
    });
  });

  describe('new AppelOffre page', () => {
    beforeEach(() => {
      cy.visit(`${appelOffrePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('AppelOffre');
    });

    it('should create an instance of AppelOffre', () => {
      cy.get(`[data-cy="reference"]`).type('diplomate');
      cy.get(`[data-cy="reference"]`).should('have.value', 'diplomate');

      cy.get(`[data-cy="titre"]`).type('de peur que nonobstant');
      cy.get(`[data-cy="titre"]`).should('have.value', 'de peur que nonobstant');

      cy.setFieldImageAsBytesOfEntity('description', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="dateCloture"]`).type('2026-03-12T17:37');
      cy.get(`[data-cy="dateCloture"]`).blur();
      cy.get(`[data-cy="dateCloture"]`).should('have.value', '2026-03-12T17:37');

      cy.get(`[data-cy="statut"]`).select('CLOTURE');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        appelOffre = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', appelOffrePageUrlPattern);
    });
  });
});
