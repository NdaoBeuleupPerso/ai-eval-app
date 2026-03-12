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

describe('DocumentJoint e2e test', () => {
  const documentJointPageUrl = '/document-joint';
  const documentJointPageUrlPattern = new RegExp('/document-joint(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const documentJointSample = { nom: 'étant donné que', format: 'AUTRE' };

  let documentJoint;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/document-joints+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/document-joints').as('postEntityRequest');
    cy.intercept('DELETE', '/api/document-joints/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (documentJoint) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/document-joints/${documentJoint.id}`,
      }).then(() => {
        documentJoint = undefined;
      });
    }
  });

  it('DocumentJoints menu should load DocumentJoints page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('document-joint');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('DocumentJoint').should('exist');
    cy.url().should('match', documentJointPageUrlPattern);
  });

  describe('DocumentJoint page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(documentJointPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create DocumentJoint page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/document-joint/new$'));
        cy.getEntityCreateUpdateHeading('DocumentJoint');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', documentJointPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/document-joints',
          body: documentJointSample,
        }).then(({ body }) => {
          documentJoint = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/document-joints+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/document-joints?page=0&size=20>; rel="last",<http://localhost/api/document-joints?page=0&size=20>; rel="first"',
              },
              body: [documentJoint],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(documentJointPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details DocumentJoint page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('documentJoint');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', documentJointPageUrlPattern);
      });

      it('edit button click should load edit DocumentJoint page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DocumentJoint');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', documentJointPageUrlPattern);
      });

      it('edit button click should load edit DocumentJoint page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DocumentJoint');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', documentJointPageUrlPattern);
      });

      it('last delete button click should delete instance of DocumentJoint', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('documentJoint').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', documentJointPageUrlPattern);

        documentJoint = undefined;
      });
    });
  });

  describe('new DocumentJoint page', () => {
    beforeEach(() => {
      cy.visit(`${documentJointPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('DocumentJoint');
    });

    it('should create an instance of DocumentJoint', () => {
      cy.get(`[data-cy="nom"]`).type('sale cerner sans que');
      cy.get(`[data-cy="nom"]`).should('have.value', 'sale cerner sans que');

      cy.get(`[data-cy="format"]`).select('ATTESTATION');

      cy.get(`[data-cy="url"]`).type('https://agreable-touriste.name');
      cy.get(`[data-cy="url"]`).should('have.value', 'https://agreable-touriste.name');

      cy.get(`[data-cy="contenuOcr"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="contenuOcr"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="idExterne"]`).type('lentement hormis tandis que');
      cy.get(`[data-cy="idExterne"]`).should('have.value', 'lentement hormis tandis que');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        documentJoint = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', documentJointPageUrlPattern);
    });
  });
});
