package com.mycompany.iaeval.web.rest;

import static com.mycompany.iaeval.domain.EvaluationAsserts.*;
import static com.mycompany.iaeval.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.iaeval.IntegrationTest;
import com.mycompany.iaeval.domain.Evaluation;
import com.mycompany.iaeval.repository.EvaluationRepository;
import com.mycompany.iaeval.repository.UserRepository;
import com.mycompany.iaeval.service.EvaluationService;
import com.mycompany.iaeval.service.dto.EvaluationDTO;
import com.mycompany.iaeval.service.mapper.EvaluationMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EvaluationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EvaluationResourceIT {

    private static final Double DEFAULT_SCORE_GLOBAL = 1D;
    private static final Double UPDATED_SCORE_GLOBAL = 2D;

    private static final Double DEFAULT_SCORE_ADMIN = 1D;
    private static final Double UPDATED_SCORE_ADMIN = 2D;

    private static final Double DEFAULT_SCORE_TECH = 1D;
    private static final Double UPDATED_SCORE_TECH = 2D;

    private static final Double DEFAULT_SCORE_FIN = 1D;
    private static final Double UPDATED_SCORE_FIN = 2D;

    private static final String DEFAULT_RAPPORT_ANALYSE = "AAAAAAAAAA";
    private static final String UPDATED_RAPPORT_ANALYSE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_DOCUMENT_PV = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_DOCUMENT_PV = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_DOCUMENT_PV_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_DOCUMENT_PV_CONTENT_TYPE = "image/png";

    private static final Instant DEFAULT_DATE_EVALUATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_EVALUATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_EST_VALIDEE = false;
    private static final Boolean UPDATED_EST_VALIDEE = true;

    private static final String DEFAULT_COMMENTAIRE_EVALUATEUR = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTAIRE_EVALUATEUR = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/evaluations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private EvaluationRepository evaluationRepositoryMock;

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Mock
    private EvaluationService evaluationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEvaluationMockMvc;

    private Evaluation evaluation;

    private Evaluation insertedEvaluation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evaluation createEntity() {
        return new Evaluation()
            .scoreGlobal(DEFAULT_SCORE_GLOBAL)
            .scoreAdmin(DEFAULT_SCORE_ADMIN)
            .scoreTech(DEFAULT_SCORE_TECH)
            .scoreFin(DEFAULT_SCORE_FIN)
            .rapportAnalyse(DEFAULT_RAPPORT_ANALYSE)
            .documentPv(DEFAULT_DOCUMENT_PV)
            .documentPvContentType(DEFAULT_DOCUMENT_PV_CONTENT_TYPE)
            .dateEvaluation(DEFAULT_DATE_EVALUATION)
            .estValidee(DEFAULT_EST_VALIDEE)
            .commentaireEvaluateur(DEFAULT_COMMENTAIRE_EVALUATEUR);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evaluation createUpdatedEntity() {
        return new Evaluation()
            .scoreGlobal(UPDATED_SCORE_GLOBAL)
            .scoreAdmin(UPDATED_SCORE_ADMIN)
            .scoreTech(UPDATED_SCORE_TECH)
            .scoreFin(UPDATED_SCORE_FIN)
            .rapportAnalyse(UPDATED_RAPPORT_ANALYSE)
            .documentPv(UPDATED_DOCUMENT_PV)
            .documentPvContentType(UPDATED_DOCUMENT_PV_CONTENT_TYPE)
            .dateEvaluation(UPDATED_DATE_EVALUATION)
            .estValidee(UPDATED_EST_VALIDEE)
            .commentaireEvaluateur(UPDATED_COMMENTAIRE_EVALUATEUR);
    }

    @BeforeEach
    void initTest() {
        evaluation = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEvaluation != null) {
            evaluationRepository.delete(insertedEvaluation);
            insertedEvaluation = null;
        }
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void createEvaluation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Evaluation
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);
        var returnedEvaluationDTO = om.readValue(
            restEvaluationMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evaluationDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EvaluationDTO.class
        );

        // Validate the Evaluation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEvaluation = evaluationMapper.toEntity(returnedEvaluationDTO);
        assertEvaluationUpdatableFieldsEquals(returnedEvaluation, getPersistedEvaluation(returnedEvaluation));

        insertedEvaluation = returnedEvaluation;
    }

    @Test
    @Transactional
    void createEvaluationWithExistingId() throws Exception {
        // Create the Evaluation with an existing ID
        evaluation.setId(1L);
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEvaluationMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evaluationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Evaluation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEvaluations() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList
        restEvaluationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(evaluation.getId().intValue())))
            .andExpect(jsonPath("$.[*].scoreGlobal").value(hasItem(DEFAULT_SCORE_GLOBAL)))
            .andExpect(jsonPath("$.[*].scoreAdmin").value(hasItem(DEFAULT_SCORE_ADMIN)))
            .andExpect(jsonPath("$.[*].scoreTech").value(hasItem(DEFAULT_SCORE_TECH)))
            .andExpect(jsonPath("$.[*].scoreFin").value(hasItem(DEFAULT_SCORE_FIN)))
            .andExpect(jsonPath("$.[*].rapportAnalyse").value(hasItem(DEFAULT_RAPPORT_ANALYSE)))
            .andExpect(jsonPath("$.[*].documentPvContentType").value(hasItem(DEFAULT_DOCUMENT_PV_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].documentPv").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_DOCUMENT_PV))))
            .andExpect(jsonPath("$.[*].dateEvaluation").value(hasItem(DEFAULT_DATE_EVALUATION.toString())))
            .andExpect(jsonPath("$.[*].estValidee").value(hasItem(DEFAULT_EST_VALIDEE)))
            .andExpect(jsonPath("$.[*].commentaireEvaluateur").value(hasItem(DEFAULT_COMMENTAIRE_EVALUATEUR)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEvaluationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(evaluationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEvaluationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(evaluationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEvaluationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(evaluationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEvaluationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(evaluationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEvaluation() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        // Get the evaluation
        restEvaluationMockMvc
            .perform(get(ENTITY_API_URL_ID, evaluation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(evaluation.getId().intValue()))
            .andExpect(jsonPath("$.scoreGlobal").value(DEFAULT_SCORE_GLOBAL))
            .andExpect(jsonPath("$.scoreAdmin").value(DEFAULT_SCORE_ADMIN))
            .andExpect(jsonPath("$.scoreTech").value(DEFAULT_SCORE_TECH))
            .andExpect(jsonPath("$.scoreFin").value(DEFAULT_SCORE_FIN))
            .andExpect(jsonPath("$.rapportAnalyse").value(DEFAULT_RAPPORT_ANALYSE))
            .andExpect(jsonPath("$.documentPvContentType").value(DEFAULT_DOCUMENT_PV_CONTENT_TYPE))
            .andExpect(jsonPath("$.documentPv").value(Base64.getEncoder().encodeToString(DEFAULT_DOCUMENT_PV)))
            .andExpect(jsonPath("$.dateEvaluation").value(DEFAULT_DATE_EVALUATION.toString()))
            .andExpect(jsonPath("$.estValidee").value(DEFAULT_EST_VALIDEE))
            .andExpect(jsonPath("$.commentaireEvaluateur").value(DEFAULT_COMMENTAIRE_EVALUATEUR));
    }

    @Test
    @Transactional
    void getNonExistingEvaluation() throws Exception {
        // Get the evaluation
        restEvaluationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEvaluation() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the evaluation
        Evaluation updatedEvaluation = evaluationRepository.findById(evaluation.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEvaluation are not directly saved in db
        em.detach(updatedEvaluation);
        updatedEvaluation
            .scoreGlobal(UPDATED_SCORE_GLOBAL)
            .scoreAdmin(UPDATED_SCORE_ADMIN)
            .scoreTech(UPDATED_SCORE_TECH)
            .scoreFin(UPDATED_SCORE_FIN)
            .rapportAnalyse(UPDATED_RAPPORT_ANALYSE)
            .documentPv(UPDATED_DOCUMENT_PV)
            .documentPvContentType(UPDATED_DOCUMENT_PV_CONTENT_TYPE)
            .dateEvaluation(UPDATED_DATE_EVALUATION)
            .estValidee(UPDATED_EST_VALIDEE)
            .commentaireEvaluateur(UPDATED_COMMENTAIRE_EVALUATEUR);
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(updatedEvaluation);

        restEvaluationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, evaluationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(evaluationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Evaluation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEvaluationToMatchAllProperties(updatedEvaluation);
    }

    @Test
    @Transactional
    void putNonExistingEvaluation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evaluation.setId(longCount.incrementAndGet());

        // Create the Evaluation
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEvaluationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, evaluationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(evaluationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evaluation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEvaluation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evaluation.setId(longCount.incrementAndGet());

        // Create the Evaluation
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEvaluationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(evaluationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evaluation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEvaluation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evaluation.setId(longCount.incrementAndGet());

        // Create the Evaluation
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEvaluationMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evaluationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Evaluation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEvaluationWithPatch() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the evaluation using partial update
        Evaluation partialUpdatedEvaluation = new Evaluation();
        partialUpdatedEvaluation.setId(evaluation.getId());

        partialUpdatedEvaluation
            .scoreAdmin(UPDATED_SCORE_ADMIN)
            .scoreTech(UPDATED_SCORE_TECH)
            .rapportAnalyse(UPDATED_RAPPORT_ANALYSE)
            .documentPv(UPDATED_DOCUMENT_PV)
            .documentPvContentType(UPDATED_DOCUMENT_PV_CONTENT_TYPE)
            .estValidee(UPDATED_EST_VALIDEE);

        restEvaluationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvaluation.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEvaluation))
            )
            .andExpect(status().isOk());

        // Validate the Evaluation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEvaluationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEvaluation, evaluation),
            getPersistedEvaluation(evaluation)
        );
    }

    @Test
    @Transactional
    void fullUpdateEvaluationWithPatch() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the evaluation using partial update
        Evaluation partialUpdatedEvaluation = new Evaluation();
        partialUpdatedEvaluation.setId(evaluation.getId());

        partialUpdatedEvaluation
            .scoreGlobal(UPDATED_SCORE_GLOBAL)
            .scoreAdmin(UPDATED_SCORE_ADMIN)
            .scoreTech(UPDATED_SCORE_TECH)
            .scoreFin(UPDATED_SCORE_FIN)
            .rapportAnalyse(UPDATED_RAPPORT_ANALYSE)
            .documentPv(UPDATED_DOCUMENT_PV)
            .documentPvContentType(UPDATED_DOCUMENT_PV_CONTENT_TYPE)
            .dateEvaluation(UPDATED_DATE_EVALUATION)
            .estValidee(UPDATED_EST_VALIDEE)
            .commentaireEvaluateur(UPDATED_COMMENTAIRE_EVALUATEUR);

        restEvaluationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvaluation.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEvaluation))
            )
            .andExpect(status().isOk());

        // Validate the Evaluation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEvaluationUpdatableFieldsEquals(partialUpdatedEvaluation, getPersistedEvaluation(partialUpdatedEvaluation));
    }

    @Test
    @Transactional
    void patchNonExistingEvaluation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evaluation.setId(longCount.incrementAndGet());

        // Create the Evaluation
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEvaluationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, evaluationDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(evaluationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evaluation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEvaluation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evaluation.setId(longCount.incrementAndGet());

        // Create the Evaluation
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEvaluationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(evaluationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evaluation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEvaluation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evaluation.setId(longCount.incrementAndGet());

        // Create the Evaluation
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEvaluationMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(evaluationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Evaluation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEvaluation() throws Exception {
        // Initialize the database
        insertedEvaluation = evaluationRepository.saveAndFlush(evaluation);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the evaluation
        restEvaluationMockMvc
            .perform(delete(ENTITY_API_URL_ID, evaluation.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return evaluationRepository.count();
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

    protected Evaluation getPersistedEvaluation(Evaluation evaluation) {
        return evaluationRepository.findById(evaluation.getId()).orElseThrow();
    }

    protected void assertPersistedEvaluationToMatchAllProperties(Evaluation expectedEvaluation) {
        assertEvaluationAllPropertiesEquals(expectedEvaluation, getPersistedEvaluation(expectedEvaluation));
    }

    protected void assertPersistedEvaluationToMatchUpdatableProperties(Evaluation expectedEvaluation) {
        assertEvaluationAllUpdatablePropertiesEquals(expectedEvaluation, getPersistedEvaluation(expectedEvaluation));
    }
}
