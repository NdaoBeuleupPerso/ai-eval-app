package com.mycompany.iaeval.web.rest;

import static com.mycompany.iaeval.domain.ReferenceLegaleAsserts.*;
import static com.mycompany.iaeval.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.iaeval.IntegrationTest;
import com.mycompany.iaeval.domain.ReferenceLegale;
import com.mycompany.iaeval.domain.enumeration.TypeSource;
import com.mycompany.iaeval.repository.ReferenceLegaleRepository;
import com.mycompany.iaeval.service.dto.ReferenceLegaleDTO;
import com.mycompany.iaeval.service.mapper.ReferenceLegaleMapper;
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
 * Integration tests for the {@link ReferenceLegaleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReferenceLegaleResourceIT {

    private static final String DEFAULT_TITRE = "AAAAAAAAAA";
    private static final String UPDATED_TITRE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENU = "AAAAAAAAAA";
    private static final String UPDATED_CONTENU = "BBBBBBBBBB";

    private static final TypeSource DEFAULT_TYPE_SOURCE = TypeSource.CODE_MARCHES;
    private static final TypeSource UPDATED_TYPE_SOURCE = TypeSource.JURISPRUDENCE;

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_QDRANT_UUID = "AAAAAAAAAA";
    private static final String UPDATED_QDRANT_UUID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/reference-legales";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReferenceLegaleRepository referenceLegaleRepository;

    @Autowired
    private ReferenceLegaleMapper referenceLegaleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReferenceLegaleMockMvc;

    private ReferenceLegale referenceLegale;

    private ReferenceLegale insertedReferenceLegale;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReferenceLegale createEntity() {
        return new ReferenceLegale()
            .titre(DEFAULT_TITRE)
            .contenu(DEFAULT_CONTENU)
            .typeSource(DEFAULT_TYPE_SOURCE)
            .version(DEFAULT_VERSION)
            .qdrantUuid(DEFAULT_QDRANT_UUID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReferenceLegale createUpdatedEntity() {
        return new ReferenceLegale()
            .titre(UPDATED_TITRE)
            .contenu(UPDATED_CONTENU)
            .typeSource(UPDATED_TYPE_SOURCE)
            .version(UPDATED_VERSION)
            .qdrantUuid(UPDATED_QDRANT_UUID);
    }

    @BeforeEach
    void initTest() {
        referenceLegale = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedReferenceLegale != null) {
            referenceLegaleRepository.delete(insertedReferenceLegale);
            insertedReferenceLegale = null;
        }
    }

    @Test
    @Transactional
    void createReferenceLegale() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReferenceLegale
        ReferenceLegaleDTO referenceLegaleDTO = referenceLegaleMapper.toDto(referenceLegale);
        var returnedReferenceLegaleDTO = om.readValue(
            restReferenceLegaleMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(referenceLegaleDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReferenceLegaleDTO.class
        );

        // Validate the ReferenceLegale in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReferenceLegale = referenceLegaleMapper.toEntity(returnedReferenceLegaleDTO);
        assertReferenceLegaleUpdatableFieldsEquals(returnedReferenceLegale, getPersistedReferenceLegale(returnedReferenceLegale));

        insertedReferenceLegale = returnedReferenceLegale;
    }

    @Test
    @Transactional
    void createReferenceLegaleWithExistingId() throws Exception {
        // Create the ReferenceLegale with an existing ID
        referenceLegale.setId(1L);
        ReferenceLegaleDTO referenceLegaleDTO = referenceLegaleMapper.toDto(referenceLegale);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReferenceLegaleMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(referenceLegaleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReferenceLegale in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        referenceLegale.setTitre(null);

        // Create the ReferenceLegale, which fails.
        ReferenceLegaleDTO referenceLegaleDTO = referenceLegaleMapper.toDto(referenceLegale);

        restReferenceLegaleMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(referenceLegaleDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeSourceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        referenceLegale.setTypeSource(null);

        // Create the ReferenceLegale, which fails.
        ReferenceLegaleDTO referenceLegaleDTO = referenceLegaleMapper.toDto(referenceLegale);

        restReferenceLegaleMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(referenceLegaleDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReferenceLegales() throws Exception {
        // Initialize the database
        insertedReferenceLegale = referenceLegaleRepository.saveAndFlush(referenceLegale);

        // Get all the referenceLegaleList
        restReferenceLegaleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(referenceLegale.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].contenu").value(hasItem(DEFAULT_CONTENU)))
            .andExpect(jsonPath("$.[*].typeSource").value(hasItem(DEFAULT_TYPE_SOURCE.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].qdrantUuid").value(hasItem(DEFAULT_QDRANT_UUID)));
    }

    @Test
    @Transactional
    void getReferenceLegale() throws Exception {
        // Initialize the database
        insertedReferenceLegale = referenceLegaleRepository.saveAndFlush(referenceLegale);

        // Get the referenceLegale
        restReferenceLegaleMockMvc
            .perform(get(ENTITY_API_URL_ID, referenceLegale.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(referenceLegale.getId().intValue()))
            .andExpect(jsonPath("$.titre").value(DEFAULT_TITRE))
            .andExpect(jsonPath("$.contenu").value(DEFAULT_CONTENU))
            .andExpect(jsonPath("$.typeSource").value(DEFAULT_TYPE_SOURCE.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.qdrantUuid").value(DEFAULT_QDRANT_UUID));
    }

    @Test
    @Transactional
    void getNonExistingReferenceLegale() throws Exception {
        // Get the referenceLegale
        restReferenceLegaleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReferenceLegale() throws Exception {
        // Initialize the database
        insertedReferenceLegale = referenceLegaleRepository.saveAndFlush(referenceLegale);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the referenceLegale
        ReferenceLegale updatedReferenceLegale = referenceLegaleRepository.findById(referenceLegale.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReferenceLegale are not directly saved in db
        em.detach(updatedReferenceLegale);
        updatedReferenceLegale
            .titre(UPDATED_TITRE)
            .contenu(UPDATED_CONTENU)
            .typeSource(UPDATED_TYPE_SOURCE)
            .version(UPDATED_VERSION)
            .qdrantUuid(UPDATED_QDRANT_UUID);
        ReferenceLegaleDTO referenceLegaleDTO = referenceLegaleMapper.toDto(updatedReferenceLegale);

        restReferenceLegaleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, referenceLegaleDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(referenceLegaleDTO))
            )
            .andExpect(status().isOk());

        // Validate the ReferenceLegale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReferenceLegaleToMatchAllProperties(updatedReferenceLegale);
    }

    @Test
    @Transactional
    void putNonExistingReferenceLegale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referenceLegale.setId(longCount.incrementAndGet());

        // Create the ReferenceLegale
        ReferenceLegaleDTO referenceLegaleDTO = referenceLegaleMapper.toDto(referenceLegale);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReferenceLegaleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, referenceLegaleDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(referenceLegaleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReferenceLegale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReferenceLegale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referenceLegale.setId(longCount.incrementAndGet());

        // Create the ReferenceLegale
        ReferenceLegaleDTO referenceLegaleDTO = referenceLegaleMapper.toDto(referenceLegale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferenceLegaleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(referenceLegaleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReferenceLegale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReferenceLegale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referenceLegale.setId(longCount.incrementAndGet());

        // Create the ReferenceLegale
        ReferenceLegaleDTO referenceLegaleDTO = referenceLegaleMapper.toDto(referenceLegale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferenceLegaleMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(referenceLegaleDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReferenceLegale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReferenceLegaleWithPatch() throws Exception {
        // Initialize the database
        insertedReferenceLegale = referenceLegaleRepository.saveAndFlush(referenceLegale);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the referenceLegale using partial update
        ReferenceLegale partialUpdatedReferenceLegale = new ReferenceLegale();
        partialUpdatedReferenceLegale.setId(referenceLegale.getId());

        partialUpdatedReferenceLegale.titre(UPDATED_TITRE).contenu(UPDATED_CONTENU).qdrantUuid(UPDATED_QDRANT_UUID);

        restReferenceLegaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReferenceLegale.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReferenceLegale))
            )
            .andExpect(status().isOk());

        // Validate the ReferenceLegale in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReferenceLegaleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReferenceLegale, referenceLegale),
            getPersistedReferenceLegale(referenceLegale)
        );
    }

    @Test
    @Transactional
    void fullUpdateReferenceLegaleWithPatch() throws Exception {
        // Initialize the database
        insertedReferenceLegale = referenceLegaleRepository.saveAndFlush(referenceLegale);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the referenceLegale using partial update
        ReferenceLegale partialUpdatedReferenceLegale = new ReferenceLegale();
        partialUpdatedReferenceLegale.setId(referenceLegale.getId());

        partialUpdatedReferenceLegale
            .titre(UPDATED_TITRE)
            .contenu(UPDATED_CONTENU)
            .typeSource(UPDATED_TYPE_SOURCE)
            .version(UPDATED_VERSION)
            .qdrantUuid(UPDATED_QDRANT_UUID);

        restReferenceLegaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReferenceLegale.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReferenceLegale))
            )
            .andExpect(status().isOk());

        // Validate the ReferenceLegale in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReferenceLegaleUpdatableFieldsEquals(
            partialUpdatedReferenceLegale,
            getPersistedReferenceLegale(partialUpdatedReferenceLegale)
        );
    }

    @Test
    @Transactional
    void patchNonExistingReferenceLegale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referenceLegale.setId(longCount.incrementAndGet());

        // Create the ReferenceLegale
        ReferenceLegaleDTO referenceLegaleDTO = referenceLegaleMapper.toDto(referenceLegale);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReferenceLegaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, referenceLegaleDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(referenceLegaleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReferenceLegale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReferenceLegale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referenceLegale.setId(longCount.incrementAndGet());

        // Create the ReferenceLegale
        ReferenceLegaleDTO referenceLegaleDTO = referenceLegaleMapper.toDto(referenceLegale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferenceLegaleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(referenceLegaleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReferenceLegale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReferenceLegale() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referenceLegale.setId(longCount.incrementAndGet());

        // Create the ReferenceLegale
        ReferenceLegaleDTO referenceLegaleDTO = referenceLegaleMapper.toDto(referenceLegale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferenceLegaleMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(referenceLegaleDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReferenceLegale in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReferenceLegale() throws Exception {
        // Initialize the database
        insertedReferenceLegale = referenceLegaleRepository.saveAndFlush(referenceLegale);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the referenceLegale
        restReferenceLegaleMockMvc
            .perform(delete(ENTITY_API_URL_ID, referenceLegale.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return referenceLegaleRepository.count();
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

    protected ReferenceLegale getPersistedReferenceLegale(ReferenceLegale referenceLegale) {
        return referenceLegaleRepository.findById(referenceLegale.getId()).orElseThrow();
    }

    protected void assertPersistedReferenceLegaleToMatchAllProperties(ReferenceLegale expectedReferenceLegale) {
        assertReferenceLegaleAllPropertiesEquals(expectedReferenceLegale, getPersistedReferenceLegale(expectedReferenceLegale));
    }

    protected void assertPersistedReferenceLegaleToMatchUpdatableProperties(ReferenceLegale expectedReferenceLegale) {
        assertReferenceLegaleAllUpdatablePropertiesEquals(expectedReferenceLegale, getPersistedReferenceLegale(expectedReferenceLegale));
    }
}
