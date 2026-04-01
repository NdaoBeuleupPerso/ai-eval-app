package com.mycompany.iaeval.web.rest;

import static com.mycompany.iaeval.domain.AppelOffreAsserts.*;
import static com.mycompany.iaeval.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.iaeval.IntegrationTest;
import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.domain.enumeration.StatutAppel;
import com.mycompany.iaeval.repository.AppelOffreRepository;
import com.mycompany.iaeval.service.dto.AppelOffreDTO;
import com.mycompany.iaeval.service.mapper.AppelOffreMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
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
 * Integration tests for the {@link AppelOffreResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AppelOffreResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_TITRE = "AAAAAAAAAA";
    private static final String UPDATED_TITRE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_DESCRIPTION = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_DESCRIPTION = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_DESCRIPTION_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_DESCRIPTION_CONTENT_TYPE = "image/png";

    private static final Instant DEFAULT_DATE_CLOTURE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CLOTURE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final StatutAppel DEFAULT_STATUT = StatutAppel.OUVERT;
    private static final StatutAppel UPDATED_STATUT = StatutAppel.EN_COURS_EVALUATION;

    private static final String ENTITY_API_URL = "/api/appel-offres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AppelOffreRepository appelOffreRepository;

    @Autowired
    private AppelOffreMapper appelOffreMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAppelOffreMockMvc;

    private AppelOffre appelOffre;

    private AppelOffre insertedAppelOffre;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppelOffre createEntity() {
        return new AppelOffre()
            .reference(DEFAULT_REFERENCE)
            .titre(DEFAULT_TITRE)
            .description(DEFAULT_DESCRIPTION)
            .descriptionContentType(DEFAULT_DESCRIPTION_CONTENT_TYPE)
            .dateCloture(DEFAULT_DATE_CLOTURE)
            .statut(DEFAULT_STATUT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppelOffre createUpdatedEntity() {
        return new AppelOffre()
            .reference(UPDATED_REFERENCE)
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .descriptionContentType(UPDATED_DESCRIPTION_CONTENT_TYPE)
            .dateCloture(UPDATED_DATE_CLOTURE)
            .statut(UPDATED_STATUT);
    }

    @BeforeEach
    void initTest() {
        appelOffre = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAppelOffre != null) {
            appelOffreRepository.delete(insertedAppelOffre);
            insertedAppelOffre = null;
        }
    }

    @Test
    @Transactional
    void createAppelOffre() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AppelOffre
        AppelOffreDTO appelOffreDTO = appelOffreMapper.toDto(appelOffre);
        var returnedAppelOffreDTO = om.readValue(
            restAppelOffreMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appelOffreDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AppelOffreDTO.class
        );

        // Validate the AppelOffre in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAppelOffre = appelOffreMapper.toEntity(returnedAppelOffreDTO);
        assertAppelOffreUpdatableFieldsEquals(returnedAppelOffre, getPersistedAppelOffre(returnedAppelOffre));

        insertedAppelOffre = returnedAppelOffre;
    }

    @Test
    @Transactional
    void createAppelOffreWithExistingId() throws Exception {
        // Create the AppelOffre with an existing ID
        appelOffre.setId(1L);
        AppelOffreDTO appelOffreDTO = appelOffreMapper.toDto(appelOffre);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppelOffreMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appelOffreDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AppelOffre in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appelOffre.setReference(null);

        // Create the AppelOffre, which fails.
        AppelOffreDTO appelOffreDTO = appelOffreMapper.toDto(appelOffre);

        restAppelOffreMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appelOffreDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTitreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appelOffre.setTitre(null);

        // Create the AppelOffre, which fails.
        AppelOffreDTO appelOffreDTO = appelOffreMapper.toDto(appelOffre);

        restAppelOffreMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appelOffreDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAppelOffres() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList
        restAppelOffreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appelOffre.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].descriptionContentType").value(hasItem(DEFAULT_DESCRIPTION_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_DESCRIPTION))))
            .andExpect(jsonPath("$.[*].dateCloture").value(hasItem(DEFAULT_DATE_CLOTURE.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));
    }

    @Test
    @Transactional
    void getAppelOffre() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get the appelOffre
        restAppelOffreMockMvc
            .perform(get(ENTITY_API_URL_ID, appelOffre.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appelOffre.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.titre").value(DEFAULT_TITRE))
            .andExpect(jsonPath("$.descriptionContentType").value(DEFAULT_DESCRIPTION_CONTENT_TYPE))
            .andExpect(jsonPath("$.description").value(Base64.getEncoder().encodeToString(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.dateCloture").value(DEFAULT_DATE_CLOTURE.toString()))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()));
    }

    @Test
    @Transactional
    void getAppelOffresByIdFiltering() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        Long id = appelOffre.getId();

        defaultAppelOffreFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAppelOffreFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAppelOffreFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAppelOffresByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList where reference equals to
        defaultAppelOffreFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllAppelOffresByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList where reference in
        defaultAppelOffreFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllAppelOffresByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList where reference is not null
        defaultAppelOffreFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllAppelOffresByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList where reference contains
        defaultAppelOffreFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllAppelOffresByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList where reference does not contain
        defaultAppelOffreFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllAppelOffresByTitreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList where titre equals to
        defaultAppelOffreFiltering("titre.equals=" + DEFAULT_TITRE, "titre.equals=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllAppelOffresByTitreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList where titre in
        defaultAppelOffreFiltering("titre.in=" + DEFAULT_TITRE + "," + UPDATED_TITRE, "titre.in=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllAppelOffresByTitreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList where titre is not null
        defaultAppelOffreFiltering("titre.specified=true", "titre.specified=false");
    }

    @Test
    @Transactional
    void getAllAppelOffresByTitreContainsSomething() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList where titre contains
        defaultAppelOffreFiltering("titre.contains=" + DEFAULT_TITRE, "titre.contains=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllAppelOffresByTitreNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList where titre does not contain
        defaultAppelOffreFiltering("titre.doesNotContain=" + UPDATED_TITRE, "titre.doesNotContain=" + DEFAULT_TITRE);
    }

    @Test
    @Transactional
    void getAllAppelOffresByDateClotureIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList where dateCloture equals to
        defaultAppelOffreFiltering("dateCloture.equals=" + DEFAULT_DATE_CLOTURE, "dateCloture.equals=" + UPDATED_DATE_CLOTURE);
    }

    @Test
    @Transactional
    void getAllAppelOffresByDateClotureIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList where dateCloture in
        defaultAppelOffreFiltering(
            "dateCloture.in=" + DEFAULT_DATE_CLOTURE + "," + UPDATED_DATE_CLOTURE,
            "dateCloture.in=" + UPDATED_DATE_CLOTURE
        );
    }

    @Test
    @Transactional
    void getAllAppelOffresByDateClotureIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList where dateCloture is not null
        defaultAppelOffreFiltering("dateCloture.specified=true", "dateCloture.specified=false");
    }

    @Test
    @Transactional
    void getAllAppelOffresByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList where statut equals to
        defaultAppelOffreFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllAppelOffresByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList where statut in
        defaultAppelOffreFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllAppelOffresByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        // Get all the appelOffreList where statut is not null
        defaultAppelOffreFiltering("statut.specified=true", "statut.specified=false");
    }

    private void defaultAppelOffreFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAppelOffreShouldBeFound(shouldBeFound);
        defaultAppelOffreShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAppelOffreShouldBeFound(String filter) throws Exception {
        restAppelOffreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appelOffre.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].descriptionContentType").value(hasItem(DEFAULT_DESCRIPTION_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_DESCRIPTION))))
            .andExpect(jsonPath("$.[*].dateCloture").value(hasItem(DEFAULT_DATE_CLOTURE.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));

        // Check, that the count call also returns 1
        restAppelOffreMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAppelOffreShouldNotBeFound(String filter) throws Exception {
        restAppelOffreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAppelOffreMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAppelOffre() throws Exception {
        // Get the appelOffre
        restAppelOffreMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAppelOffre() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appelOffre
        AppelOffre updatedAppelOffre = appelOffreRepository.findById(appelOffre.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAppelOffre are not directly saved in db
        em.detach(updatedAppelOffre);
        updatedAppelOffre
            .reference(UPDATED_REFERENCE)
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .descriptionContentType(UPDATED_DESCRIPTION_CONTENT_TYPE)
            .dateCloture(UPDATED_DATE_CLOTURE)
            .statut(UPDATED_STATUT);
        AppelOffreDTO appelOffreDTO = appelOffreMapper.toDto(updatedAppelOffre);

        restAppelOffreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appelOffreDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appelOffreDTO))
            )
            .andExpect(status().isOk());

        // Validate the AppelOffre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAppelOffreToMatchAllProperties(updatedAppelOffre);
    }

    @Test
    @Transactional
    void putNonExistingAppelOffre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appelOffre.setId(longCount.incrementAndGet());

        // Create the AppelOffre
        AppelOffreDTO appelOffreDTO = appelOffreMapper.toDto(appelOffre);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppelOffreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appelOffreDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appelOffreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppelOffre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAppelOffre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appelOffre.setId(longCount.incrementAndGet());

        // Create the AppelOffre
        AppelOffreDTO appelOffreDTO = appelOffreMapper.toDto(appelOffre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppelOffreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appelOffreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppelOffre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAppelOffre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appelOffre.setId(longCount.incrementAndGet());

        // Create the AppelOffre
        AppelOffreDTO appelOffreDTO = appelOffreMapper.toDto(appelOffre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppelOffreMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appelOffreDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppelOffre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAppelOffreWithPatch() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appelOffre using partial update
        AppelOffre partialUpdatedAppelOffre = new AppelOffre();
        partialUpdatedAppelOffre.setId(appelOffre.getId());

        partialUpdatedAppelOffre.dateCloture(UPDATED_DATE_CLOTURE);

        restAppelOffreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppelOffre.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAppelOffre))
            )
            .andExpect(status().isOk());

        // Validate the AppelOffre in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppelOffreUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAppelOffre, appelOffre),
            getPersistedAppelOffre(appelOffre)
        );
    }

    @Test
    @Transactional
    void fullUpdateAppelOffreWithPatch() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appelOffre using partial update
        AppelOffre partialUpdatedAppelOffre = new AppelOffre();
        partialUpdatedAppelOffre.setId(appelOffre.getId());

        partialUpdatedAppelOffre
            .reference(UPDATED_REFERENCE)
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .descriptionContentType(UPDATED_DESCRIPTION_CONTENT_TYPE)
            .dateCloture(UPDATED_DATE_CLOTURE)
            .statut(UPDATED_STATUT);

        restAppelOffreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppelOffre.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAppelOffre))
            )
            .andExpect(status().isOk());

        // Validate the AppelOffre in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppelOffreUpdatableFieldsEquals(partialUpdatedAppelOffre, getPersistedAppelOffre(partialUpdatedAppelOffre));
    }

    @Test
    @Transactional
    void patchNonExistingAppelOffre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appelOffre.setId(longCount.incrementAndGet());

        // Create the AppelOffre
        AppelOffreDTO appelOffreDTO = appelOffreMapper.toDto(appelOffre);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppelOffreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, appelOffreDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appelOffreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppelOffre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAppelOffre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appelOffre.setId(longCount.incrementAndGet());

        // Create the AppelOffre
        AppelOffreDTO appelOffreDTO = appelOffreMapper.toDto(appelOffre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppelOffreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appelOffreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppelOffre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAppelOffre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appelOffre.setId(longCount.incrementAndGet());

        // Create the AppelOffre
        AppelOffreDTO appelOffreDTO = appelOffreMapper.toDto(appelOffre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppelOffreMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(appelOffreDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppelOffre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAppelOffre() throws Exception {
        // Initialize the database
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the appelOffre
        restAppelOffreMockMvc
            .perform(delete(ENTITY_API_URL_ID, appelOffre.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return appelOffreRepository.count();
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

    protected AppelOffre getPersistedAppelOffre(AppelOffre appelOffre) {
        return appelOffreRepository.findById(appelOffre.getId()).orElseThrow();
    }

    protected void assertPersistedAppelOffreToMatchAllProperties(AppelOffre expectedAppelOffre) {
        assertAppelOffreAllPropertiesEquals(expectedAppelOffre, getPersistedAppelOffre(expectedAppelOffre));
    }

    protected void assertPersistedAppelOffreToMatchUpdatableProperties(AppelOffre expectedAppelOffre) {
        assertAppelOffreAllUpdatablePropertiesEquals(expectedAppelOffre, getPersistedAppelOffre(expectedAppelOffre));
    }
}
