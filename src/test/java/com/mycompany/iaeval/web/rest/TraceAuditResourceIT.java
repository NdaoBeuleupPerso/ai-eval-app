package com.mycompany.iaeval.web.rest;

import static com.mycompany.iaeval.domain.TraceAuditAsserts.*;
import static com.mycompany.iaeval.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.iaeval.IntegrationTest;
import com.mycompany.iaeval.domain.Evaluation;
import com.mycompany.iaeval.domain.TraceAudit;
import com.mycompany.iaeval.repository.TraceAuditRepository;
import com.mycompany.iaeval.service.dto.TraceAuditDTO;
import com.mycompany.iaeval.service.mapper.TraceAuditMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link TraceAuditResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TraceAuditResourceIT {

    private static final String DEFAULT_ACTION = "AAAAAAAAAA";
    private static final String UPDATED_ACTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_HORODATAGE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_HORODATAGE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    private static final String DEFAULT_IDENTIFIANT_UTILISATEUR = "AAAAAAAAAA";
    private static final String UPDATED_IDENTIFIANT_UTILISATEUR = "BBBBBBBBBB";

    private static final String DEFAULT_PROMPT_UTILISE = "AAAAAAAAAA";
    private static final String UPDATED_PROMPT_UTILISE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/trace-audits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TraceAuditRepository traceAuditRepository;

    @Autowired
    private TraceAuditMapper traceAuditMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTraceAuditMockMvc;

    private TraceAudit traceAudit;

    private TraceAudit insertedTraceAudit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TraceAudit createEntity() {
        return new TraceAudit()
            .action(DEFAULT_ACTION)
            .horodatage(DEFAULT_HORODATAGE)
            .details(DEFAULT_DETAILS)
            .identifiantUtilisateur(DEFAULT_IDENTIFIANT_UTILISATEUR)
            .promptUtilise(DEFAULT_PROMPT_UTILISE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TraceAudit createUpdatedEntity() {
        return new TraceAudit()
            .action(UPDATED_ACTION)
            .horodatage(UPDATED_HORODATAGE)
            .details(UPDATED_DETAILS)
            .identifiantUtilisateur(UPDATED_IDENTIFIANT_UTILISATEUR)
            .promptUtilise(UPDATED_PROMPT_UTILISE);
    }

    @BeforeEach
    void initTest() {
        traceAudit = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTraceAudit != null) {
            traceAuditRepository.delete(insertedTraceAudit);
            insertedTraceAudit = null;
        }
    }

    @Test
    @Transactional
    void createTraceAudit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TraceAudit
        TraceAuditDTO traceAuditDTO = traceAuditMapper.toDto(traceAudit);
        var returnedTraceAuditDTO = om.readValue(
            restTraceAuditMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(traceAuditDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TraceAuditDTO.class
        );

        // Validate the TraceAudit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTraceAudit = traceAuditMapper.toEntity(returnedTraceAuditDTO);
        assertTraceAuditUpdatableFieldsEquals(returnedTraceAudit, getPersistedTraceAudit(returnedTraceAudit));

        insertedTraceAudit = returnedTraceAudit;
    }

    @Test
    @Transactional
    void createTraceAuditWithExistingId() throws Exception {
        // Create the TraceAudit with an existing ID
        traceAudit.setId(1L);
        TraceAuditDTO traceAuditDTO = traceAuditMapper.toDto(traceAudit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTraceAuditMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(traceAuditDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TraceAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkActionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        traceAudit.setAction(null);

        // Create the TraceAudit, which fails.
        TraceAuditDTO traceAuditDTO = traceAuditMapper.toDto(traceAudit);

        restTraceAuditMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(traceAuditDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHorodatageIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        traceAudit.setHorodatage(null);

        // Create the TraceAudit, which fails.
        TraceAuditDTO traceAuditDTO = traceAuditMapper.toDto(traceAudit);

        restTraceAuditMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(traceAuditDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTraceAudits() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        // Get all the traceAuditList
        restTraceAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(traceAudit.getId().intValue())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION)))
            .andExpect(jsonPath("$.[*].horodatage").value(hasItem(DEFAULT_HORODATAGE.toString())))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)))
            .andExpect(jsonPath("$.[*].identifiantUtilisateur").value(hasItem(DEFAULT_IDENTIFIANT_UTILISATEUR)))
            .andExpect(jsonPath("$.[*].promptUtilise").value(hasItem(DEFAULT_PROMPT_UTILISE)));
    }

    @Test
    @Transactional
    void getTraceAudit() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        // Get the traceAudit
        restTraceAuditMockMvc
            .perform(get(ENTITY_API_URL_ID, traceAudit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(traceAudit.getId().intValue()))
            .andExpect(jsonPath("$.action").value(DEFAULT_ACTION))
            .andExpect(jsonPath("$.horodatage").value(DEFAULT_HORODATAGE.toString()))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS))
            .andExpect(jsonPath("$.identifiantUtilisateur").value(DEFAULT_IDENTIFIANT_UTILISATEUR))
            .andExpect(jsonPath("$.promptUtilise").value(DEFAULT_PROMPT_UTILISE));
    }

    @Test
    @Transactional
    void getTraceAuditsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        Long id = traceAudit.getId();

        defaultTraceAuditFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTraceAuditFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTraceAuditFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTraceAuditsByActionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        // Get all the traceAuditList where action equals to
        defaultTraceAuditFiltering("action.equals=" + DEFAULT_ACTION, "action.equals=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllTraceAuditsByActionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        // Get all the traceAuditList where action in
        defaultTraceAuditFiltering("action.in=" + DEFAULT_ACTION + "," + UPDATED_ACTION, "action.in=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllTraceAuditsByActionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        // Get all the traceAuditList where action is not null
        defaultTraceAuditFiltering("action.specified=true", "action.specified=false");
    }

    @Test
    @Transactional
    void getAllTraceAuditsByActionContainsSomething() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        // Get all the traceAuditList where action contains
        defaultTraceAuditFiltering("action.contains=" + DEFAULT_ACTION, "action.contains=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllTraceAuditsByActionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        // Get all the traceAuditList where action does not contain
        defaultTraceAuditFiltering("action.doesNotContain=" + UPDATED_ACTION, "action.doesNotContain=" + DEFAULT_ACTION);
    }

    @Test
    @Transactional
    void getAllTraceAuditsByHorodatageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        // Get all the traceAuditList where horodatage equals to
        defaultTraceAuditFiltering("horodatage.equals=" + DEFAULT_HORODATAGE, "horodatage.equals=" + UPDATED_HORODATAGE);
    }

    @Test
    @Transactional
    void getAllTraceAuditsByHorodatageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        // Get all the traceAuditList where horodatage in
        defaultTraceAuditFiltering("horodatage.in=" + DEFAULT_HORODATAGE + "," + UPDATED_HORODATAGE, "horodatage.in=" + UPDATED_HORODATAGE);
    }

    @Test
    @Transactional
    void getAllTraceAuditsByHorodatageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        // Get all the traceAuditList where horodatage is not null
        defaultTraceAuditFiltering("horodatage.specified=true", "horodatage.specified=false");
    }

    @Test
    @Transactional
    void getAllTraceAuditsByIdentifiantUtilisateurIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        // Get all the traceAuditList where identifiantUtilisateur equals to
        defaultTraceAuditFiltering(
            "identifiantUtilisateur.equals=" + DEFAULT_IDENTIFIANT_UTILISATEUR,
            "identifiantUtilisateur.equals=" + UPDATED_IDENTIFIANT_UTILISATEUR
        );
    }

    @Test
    @Transactional
    void getAllTraceAuditsByIdentifiantUtilisateurIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        // Get all the traceAuditList where identifiantUtilisateur in
        defaultTraceAuditFiltering(
            "identifiantUtilisateur.in=" + DEFAULT_IDENTIFIANT_UTILISATEUR + "," + UPDATED_IDENTIFIANT_UTILISATEUR,
            "identifiantUtilisateur.in=" + UPDATED_IDENTIFIANT_UTILISATEUR
        );
    }

    @Test
    @Transactional
    void getAllTraceAuditsByIdentifiantUtilisateurIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        // Get all the traceAuditList where identifiantUtilisateur is not null
        defaultTraceAuditFiltering("identifiantUtilisateur.specified=true", "identifiantUtilisateur.specified=false");
    }

    @Test
    @Transactional
    void getAllTraceAuditsByIdentifiantUtilisateurContainsSomething() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        // Get all the traceAuditList where identifiantUtilisateur contains
        defaultTraceAuditFiltering(
            "identifiantUtilisateur.contains=" + DEFAULT_IDENTIFIANT_UTILISATEUR,
            "identifiantUtilisateur.contains=" + UPDATED_IDENTIFIANT_UTILISATEUR
        );
    }

    @Test
    @Transactional
    void getAllTraceAuditsByIdentifiantUtilisateurNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        // Get all the traceAuditList where identifiantUtilisateur does not contain
        defaultTraceAuditFiltering(
            "identifiantUtilisateur.doesNotContain=" + UPDATED_IDENTIFIANT_UTILISATEUR,
            "identifiantUtilisateur.doesNotContain=" + DEFAULT_IDENTIFIANT_UTILISATEUR
        );
    }

    @Test
    @Transactional
    void getAllTraceAuditsByEvaluationIsEqualToSomething() throws Exception {
        Evaluation evaluation;
        if (TestUtil.findAll(em, Evaluation.class).isEmpty()) {
            traceAuditRepository.saveAndFlush(traceAudit);
            evaluation = EvaluationResourceIT.createEntity();
        } else {
            evaluation = TestUtil.findAll(em, Evaluation.class).get(0);
        }
        em.persist(evaluation);
        em.flush();
        traceAudit.setEvaluation(evaluation);
        traceAuditRepository.saveAndFlush(traceAudit);
        Long evaluationId = evaluation.getId();
        // Get all the traceAuditList where evaluation equals to evaluationId
        defaultTraceAuditShouldBeFound("evaluationId.equals=" + evaluationId);

        // Get all the traceAuditList where evaluation equals to (evaluationId + 1)
        defaultTraceAuditShouldNotBeFound("evaluationId.equals=" + (evaluationId + 1));
    }

    private void defaultTraceAuditFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTraceAuditShouldBeFound(shouldBeFound);
        defaultTraceAuditShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTraceAuditShouldBeFound(String filter) throws Exception {
        restTraceAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(traceAudit.getId().intValue())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION)))
            .andExpect(jsonPath("$.[*].horodatage").value(hasItem(DEFAULT_HORODATAGE.toString())))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)))
            .andExpect(jsonPath("$.[*].identifiantUtilisateur").value(hasItem(DEFAULT_IDENTIFIANT_UTILISATEUR)))
            .andExpect(jsonPath("$.[*].promptUtilise").value(hasItem(DEFAULT_PROMPT_UTILISE)));

        // Check, that the count call also returns 1
        restTraceAuditMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTraceAuditShouldNotBeFound(String filter) throws Exception {
        restTraceAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTraceAuditMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTraceAudit() throws Exception {
        // Get the traceAudit
        restTraceAuditMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTraceAudit() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the traceAudit
        TraceAudit updatedTraceAudit = traceAuditRepository.findById(traceAudit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTraceAudit are not directly saved in db
        em.detach(updatedTraceAudit);
        updatedTraceAudit
            .action(UPDATED_ACTION)
            .horodatage(UPDATED_HORODATAGE)
            .details(UPDATED_DETAILS)
            .identifiantUtilisateur(UPDATED_IDENTIFIANT_UTILISATEUR)
            .promptUtilise(UPDATED_PROMPT_UTILISE);
        TraceAuditDTO traceAuditDTO = traceAuditMapper.toDto(updatedTraceAudit);

        restTraceAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, traceAuditDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(traceAuditDTO))
            )
            .andExpect(status().isOk());

        // Validate the TraceAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTraceAuditToMatchAllProperties(updatedTraceAudit);
    }

    @Test
    @Transactional
    void putNonExistingTraceAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        traceAudit.setId(longCount.incrementAndGet());

        // Create the TraceAudit
        TraceAuditDTO traceAuditDTO = traceAuditMapper.toDto(traceAudit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTraceAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, traceAuditDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(traceAuditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TraceAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTraceAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        traceAudit.setId(longCount.incrementAndGet());

        // Create the TraceAudit
        TraceAuditDTO traceAuditDTO = traceAuditMapper.toDto(traceAudit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTraceAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(traceAuditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TraceAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTraceAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        traceAudit.setId(longCount.incrementAndGet());

        // Create the TraceAudit
        TraceAuditDTO traceAuditDTO = traceAuditMapper.toDto(traceAudit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTraceAuditMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(traceAuditDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TraceAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTraceAuditWithPatch() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the traceAudit using partial update
        TraceAudit partialUpdatedTraceAudit = new TraceAudit();
        partialUpdatedTraceAudit.setId(traceAudit.getId());

        partialUpdatedTraceAudit
            .details(UPDATED_DETAILS)
            .identifiantUtilisateur(UPDATED_IDENTIFIANT_UTILISATEUR)
            .promptUtilise(UPDATED_PROMPT_UTILISE);

        restTraceAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTraceAudit.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTraceAudit))
            )
            .andExpect(status().isOk());

        // Validate the TraceAudit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTraceAuditUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTraceAudit, traceAudit),
            getPersistedTraceAudit(traceAudit)
        );
    }

    @Test
    @Transactional
    void fullUpdateTraceAuditWithPatch() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the traceAudit using partial update
        TraceAudit partialUpdatedTraceAudit = new TraceAudit();
        partialUpdatedTraceAudit.setId(traceAudit.getId());

        partialUpdatedTraceAudit
            .action(UPDATED_ACTION)
            .horodatage(UPDATED_HORODATAGE)
            .details(UPDATED_DETAILS)
            .identifiantUtilisateur(UPDATED_IDENTIFIANT_UTILISATEUR)
            .promptUtilise(UPDATED_PROMPT_UTILISE);

        restTraceAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTraceAudit.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTraceAudit))
            )
            .andExpect(status().isOk());

        // Validate the TraceAudit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTraceAuditUpdatableFieldsEquals(partialUpdatedTraceAudit, getPersistedTraceAudit(partialUpdatedTraceAudit));
    }

    @Test
    @Transactional
    void patchNonExistingTraceAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        traceAudit.setId(longCount.incrementAndGet());

        // Create the TraceAudit
        TraceAuditDTO traceAuditDTO = traceAuditMapper.toDto(traceAudit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTraceAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, traceAuditDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(traceAuditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TraceAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTraceAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        traceAudit.setId(longCount.incrementAndGet());

        // Create the TraceAudit
        TraceAuditDTO traceAuditDTO = traceAuditMapper.toDto(traceAudit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTraceAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(traceAuditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TraceAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTraceAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        traceAudit.setId(longCount.incrementAndGet());

        // Create the TraceAudit
        TraceAuditDTO traceAuditDTO = traceAuditMapper.toDto(traceAudit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTraceAuditMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(traceAuditDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TraceAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTraceAudit() throws Exception {
        // Initialize the database
        insertedTraceAudit = traceAuditRepository.saveAndFlush(traceAudit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the traceAudit
        restTraceAuditMockMvc
            .perform(delete(ENTITY_API_URL_ID, traceAudit.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return traceAuditRepository.count();
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

    protected TraceAudit getPersistedTraceAudit(TraceAudit traceAudit) {
        return traceAuditRepository.findById(traceAudit.getId()).orElseThrow();
    }

    protected void assertPersistedTraceAuditToMatchAllProperties(TraceAudit expectedTraceAudit) {
        assertTraceAuditAllPropertiesEquals(expectedTraceAudit, getPersistedTraceAudit(expectedTraceAudit));
    }

    protected void assertPersistedTraceAuditToMatchUpdatableProperties(TraceAudit expectedTraceAudit) {
        assertTraceAuditAllUpdatablePropertiesEquals(expectedTraceAudit, getPersistedTraceAudit(expectedTraceAudit));
    }
}
