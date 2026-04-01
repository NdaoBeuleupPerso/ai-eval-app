package com.mycompany.iaeval.web.rest;

import static com.mycompany.iaeval.domain.DocumentJointAsserts.*;
import static com.mycompany.iaeval.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.iaeval.IntegrationTest;
import com.mycompany.iaeval.domain.DocumentJoint;
import com.mycompany.iaeval.domain.enumeration.FormatDocument;
import com.mycompany.iaeval.repository.DocumentJointRepository;
import com.mycompany.iaeval.service.dto.DocumentJointDTO;
import com.mycompany.iaeval.service.mapper.DocumentJointMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DocumentJointResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DocumentJointResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final FormatDocument DEFAULT_FORMAT = FormatDocument.OFFRE_TECHNIQUE;
    private static final FormatDocument UPDATED_FORMAT = FormatDocument.ATTESTATION;

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENU_OCR = "AAAAAAAAAA";
    private static final String UPDATED_CONTENU_OCR = "BBBBBBBBBB";

    private static final String DEFAULT_ID_EXTERNE = "AAAAAAAAAA";
    private static final String UPDATED_ID_EXTERNE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/document-joints";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DocumentJointRepository documentJointRepository;

    @Autowired
    private DocumentJointMapper documentJointMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDocumentJointMockMvc;

    private DocumentJoint documentJoint;

    private DocumentJoint insertedDocumentJoint;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DocumentJoint createEntity() {
        return new DocumentJoint()
            .nom(DEFAULT_NOM)
            .format(DEFAULT_FORMAT)
            .url(DEFAULT_URL)
            .contenuOcr(DEFAULT_CONTENU_OCR)
            .idExterne(DEFAULT_ID_EXTERNE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DocumentJoint createUpdatedEntity() {
        return new DocumentJoint()
            .nom(UPDATED_NOM)
            .format(UPDATED_FORMAT)
            .url(UPDATED_URL)
            .contenuOcr(UPDATED_CONTENU_OCR)
            .idExterne(UPDATED_ID_EXTERNE);
    }

    @BeforeEach
    void initTest() {
        documentJoint = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDocumentJoint != null) {
            documentJointRepository.delete(insertedDocumentJoint);
            insertedDocumentJoint = null;
        }
    }

    @Test
    @Transactional
    void createDocumentJoint() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DocumentJoint
        DocumentJointDTO documentJointDTO = documentJointMapper.toDto(documentJoint);
        var returnedDocumentJointDTO = om.readValue(
            restDocumentJointMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(documentJointDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DocumentJointDTO.class
        );

        // Validate the DocumentJoint in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDocumentJoint = documentJointMapper.toEntity(returnedDocumentJointDTO);
        assertDocumentJointUpdatableFieldsEquals(returnedDocumentJoint, getPersistedDocumentJoint(returnedDocumentJoint));

        insertedDocumentJoint = returnedDocumentJoint;
    }

    @Test
    @Transactional
    void createDocumentJointWithExistingId() throws Exception {
        // Create the DocumentJoint with an existing ID
        documentJoint.setId(1L);
        DocumentJointDTO documentJointDTO = documentJointMapper.toDto(documentJoint);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDocumentJointMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(documentJointDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocumentJoint in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        documentJoint.setNom(null);

        // Create the DocumentJoint, which fails.
        DocumentJointDTO documentJointDTO = documentJointMapper.toDto(documentJoint);

        restDocumentJointMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(documentJointDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFormatIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        documentJoint.setFormat(null);

        // Create the DocumentJoint, which fails.
        DocumentJointDTO documentJointDTO = documentJointMapper.toDto(documentJoint);

        restDocumentJointMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(documentJointDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDocumentJoints() throws Exception {
        // Initialize the database
        insertedDocumentJoint = documentJointRepository.saveAndFlush(documentJoint);

        // Get all the documentJointList
        restDocumentJointMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(documentJoint.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].format").value(hasItem(DEFAULT_FORMAT.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].contenuOcr").value(hasItem(DEFAULT_CONTENU_OCR)))
            .andExpect(jsonPath("$.[*].idExterne").value(hasItem(DEFAULT_ID_EXTERNE)));
    }

    @Test
    @Transactional
    void getDocumentJoint() throws Exception {
        // Initialize the database
        insertedDocumentJoint = documentJointRepository.saveAndFlush(documentJoint);

        // Get the documentJoint
        restDocumentJointMockMvc
            .perform(get(ENTITY_API_URL_ID, documentJoint.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(documentJoint.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.format").value(DEFAULT_FORMAT.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.contenuOcr").value(DEFAULT_CONTENU_OCR))
            .andExpect(jsonPath("$.idExterne").value(DEFAULT_ID_EXTERNE));
    }

    @Test
    @Transactional
    void getNonExistingDocumentJoint() throws Exception {
        // Get the documentJoint
        restDocumentJointMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDocumentJoint() throws Exception {
        // Initialize the database
        insertedDocumentJoint = documentJointRepository.saveAndFlush(documentJoint);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the documentJoint
        DocumentJoint updatedDocumentJoint = documentJointRepository.findById(documentJoint.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDocumentJoint are not directly saved in db
        em.detach(updatedDocumentJoint);
        updatedDocumentJoint
            .nom(UPDATED_NOM)
            .format(UPDATED_FORMAT)
            .url(UPDATED_URL)
            .contenuOcr(UPDATED_CONTENU_OCR)
            .idExterne(UPDATED_ID_EXTERNE);
        DocumentJointDTO documentJointDTO = documentJointMapper.toDto(updatedDocumentJoint);

        restDocumentJointMockMvc
            .perform(
                put(ENTITY_API_URL_ID, documentJointDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(documentJointDTO))
            )
            .andExpect(status().isOk());

        // Validate the DocumentJoint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDocumentJointToMatchAllProperties(updatedDocumentJoint);
    }

    @Test
    @Transactional
    void putNonExistingDocumentJoint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentJoint.setId(longCount.incrementAndGet());

        // Create the DocumentJoint
        DocumentJointDTO documentJointDTO = documentJointMapper.toDto(documentJoint);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocumentJointMockMvc
            .perform(
                put(ENTITY_API_URL_ID, documentJointDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(documentJointDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocumentJoint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDocumentJoint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentJoint.setId(longCount.incrementAndGet());

        // Create the DocumentJoint
        DocumentJointDTO documentJointDTO = documentJointMapper.toDto(documentJoint);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentJointMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(documentJointDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocumentJoint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDocumentJoint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentJoint.setId(longCount.incrementAndGet());

        // Create the DocumentJoint
        DocumentJointDTO documentJointDTO = documentJointMapper.toDto(documentJoint);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentJointMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(documentJointDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DocumentJoint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDocumentJointWithPatch() throws Exception {
        // Initialize the database
        insertedDocumentJoint = documentJointRepository.saveAndFlush(documentJoint);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the documentJoint using partial update
        DocumentJoint partialUpdatedDocumentJoint = new DocumentJoint();
        partialUpdatedDocumentJoint.setId(documentJoint.getId());

        partialUpdatedDocumentJoint.format(UPDATED_FORMAT);

        restDocumentJointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDocumentJoint.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDocumentJoint))
            )
            .andExpect(status().isOk());

        // Validate the DocumentJoint in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDocumentJointUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDocumentJoint, documentJoint),
            getPersistedDocumentJoint(documentJoint)
        );
    }

    @Test
    @Transactional
    void fullUpdateDocumentJointWithPatch() throws Exception {
        // Initialize the database
        insertedDocumentJoint = documentJointRepository.saveAndFlush(documentJoint);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the documentJoint using partial update
        DocumentJoint partialUpdatedDocumentJoint = new DocumentJoint();
        partialUpdatedDocumentJoint.setId(documentJoint.getId());

        partialUpdatedDocumentJoint
            .nom(UPDATED_NOM)
            .format(UPDATED_FORMAT)
            .url(UPDATED_URL)
            .contenuOcr(UPDATED_CONTENU_OCR)
            .idExterne(UPDATED_ID_EXTERNE);

        restDocumentJointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDocumentJoint.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDocumentJoint))
            )
            .andExpect(status().isOk());

        // Validate the DocumentJoint in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDocumentJointUpdatableFieldsEquals(partialUpdatedDocumentJoint, getPersistedDocumentJoint(partialUpdatedDocumentJoint));
    }

    @Test
    @Transactional
    void patchNonExistingDocumentJoint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentJoint.setId(longCount.incrementAndGet());

        // Create the DocumentJoint
        DocumentJointDTO documentJointDTO = documentJointMapper.toDto(documentJoint);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocumentJointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, documentJointDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(documentJointDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocumentJoint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDocumentJoint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentJoint.setId(longCount.incrementAndGet());

        // Create the DocumentJoint
        DocumentJointDTO documentJointDTO = documentJointMapper.toDto(documentJoint);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentJointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(documentJointDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocumentJoint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDocumentJoint() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentJoint.setId(longCount.incrementAndGet());

        // Create the DocumentJoint
        DocumentJointDTO documentJointDTO = documentJointMapper.toDto(documentJoint);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentJointMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(documentJointDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DocumentJoint in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDocumentJoint() throws Exception {
        // Initialize the database
        insertedDocumentJoint = documentJointRepository.saveAndFlush(documentJoint);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the documentJoint
        restDocumentJointMockMvc
            .perform(delete(ENTITY_API_URL_ID, documentJoint.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return documentJointRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected DocumentJoint getPersistedDocumentJoint(DocumentJoint documentJoint) {
        return documentJointRepository.findById(documentJoint.getId()).orElseThrow();
    }

    protected void assertPersistedDocumentJointToMatchAllProperties(DocumentJoint expectedDocumentJoint) {
        assertDocumentJointAllPropertiesEquals(expectedDocumentJoint, getPersistedDocumentJoint(expectedDocumentJoint));
    }

    protected void assertPersistedDocumentJointToMatchUpdatableProperties(DocumentJoint expectedDocumentJoint) {
        assertDocumentJointAllUpdatablePropertiesEquals(expectedDocumentJoint, getPersistedDocumentJoint(expectedDocumentJoint));
    }
}
