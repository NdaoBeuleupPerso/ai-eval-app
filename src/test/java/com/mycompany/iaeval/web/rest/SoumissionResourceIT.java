package com.mycompany.iaeval.web.rest;

import static com.mycompany.iaeval.domain.SoumissionAsserts.*;
import static com.mycompany.iaeval.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.iaeval.IntegrationTest;
import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.domain.Candidat;
import com.mycompany.iaeval.domain.Evaluation;
import com.mycompany.iaeval.domain.Soumission;
import com.mycompany.iaeval.domain.enumeration.StatutEvaluation;
import com.mycompany.iaeval.repository.SoumissionRepository;
import com.mycompany.iaeval.service.SoumissionService;
import com.mycompany.iaeval.service.dto.SoumissionDTO;
import com.mycompany.iaeval.service.mapper.SoumissionMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
 * Integration tests for the {@link SoumissionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SoumissionResourceIT {

    private static final Instant DEFAULT_DATE_SOUMISSION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_SOUMISSION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final StatutEvaluation DEFAULT_STATUT = StatutEvaluation.EN_ATTENTE;
    private static final StatutEvaluation UPDATED_STATUT = StatutEvaluation.EN_COURS;

    private static final String ENTITY_API_URL = "/api/soumissions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SoumissionRepository soumissionRepository;

    @Mock
    private SoumissionRepository soumissionRepositoryMock;

    @Autowired
    private SoumissionMapper soumissionMapper;

    @Mock
    private SoumissionService soumissionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSoumissionMockMvc;

    private Soumission soumission;

    private Soumission insertedSoumission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Soumission createEntity() {
        return new Soumission().dateSoumission(DEFAULT_DATE_SOUMISSION).statut(DEFAULT_STATUT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Soumission createUpdatedEntity() {
        return new Soumission().dateSoumission(UPDATED_DATE_SOUMISSION).statut(UPDATED_STATUT);
    }

    @BeforeEach
    void initTest() {
        soumission = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSoumission != null) {
            soumissionRepository.delete(insertedSoumission);
            insertedSoumission = null;
        }
    }

    @Test
    @Transactional
    void createSoumission() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Soumission
        SoumissionDTO soumissionDTO = soumissionMapper.toDto(soumission);
        var returnedSoumissionDTO = om.readValue(
            restSoumissionMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(soumissionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SoumissionDTO.class
        );

        // Validate the Soumission in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSoumission = soumissionMapper.toEntity(returnedSoumissionDTO);
        assertSoumissionUpdatableFieldsEquals(returnedSoumission, getPersistedSoumission(returnedSoumission));

        insertedSoumission = returnedSoumission;
    }

    @Test
    @Transactional
    void createSoumissionWithExistingId() throws Exception {
        // Create the Soumission with an existing ID
        soumission.setId(1L);
        SoumissionDTO soumissionDTO = soumissionMapper.toDto(soumission);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSoumissionMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(soumissionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Soumission in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSoumissions() throws Exception {
        // Initialize the database
        insertedSoumission = soumissionRepository.saveAndFlush(soumission);

        // Get all the soumissionList
        restSoumissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(soumission.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateSoumission").value(hasItem(DEFAULT_DATE_SOUMISSION.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSoumissionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(soumissionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSoumissionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(soumissionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSoumissionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(soumissionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSoumissionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(soumissionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSoumission() throws Exception {
        // Initialize the database
        insertedSoumission = soumissionRepository.saveAndFlush(soumission);

        // Get the soumission
        restSoumissionMockMvc
            .perform(get(ENTITY_API_URL_ID, soumission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(soumission.getId().intValue()))
            .andExpect(jsonPath("$.dateSoumission").value(DEFAULT_DATE_SOUMISSION.toString()))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()));
    }

    @Test
    @Transactional
    void getSoumissionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedSoumission = soumissionRepository.saveAndFlush(soumission);

        Long id = soumission.getId();

        defaultSoumissionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSoumissionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSoumissionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSoumissionsByDateSoumissionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSoumission = soumissionRepository.saveAndFlush(soumission);

        // Get all the soumissionList where dateSoumission equals to
        defaultSoumissionFiltering("dateSoumission.equals=" + DEFAULT_DATE_SOUMISSION, "dateSoumission.equals=" + UPDATED_DATE_SOUMISSION);
    }

    @Test
    @Transactional
    void getAllSoumissionsByDateSoumissionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSoumission = soumissionRepository.saveAndFlush(soumission);

        // Get all the soumissionList where dateSoumission in
        defaultSoumissionFiltering(
            "dateSoumission.in=" + DEFAULT_DATE_SOUMISSION + "," + UPDATED_DATE_SOUMISSION,
            "dateSoumission.in=" + UPDATED_DATE_SOUMISSION
        );
    }

    @Test
    @Transactional
    void getAllSoumissionsByDateSoumissionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSoumission = soumissionRepository.saveAndFlush(soumission);

        // Get all the soumissionList where dateSoumission is not null
        defaultSoumissionFiltering("dateSoumission.specified=true", "dateSoumission.specified=false");
    }

    @Test
    @Transactional
    void getAllSoumissionsByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSoumission = soumissionRepository.saveAndFlush(soumission);

        // Get all the soumissionList where statut equals to
        defaultSoumissionFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllSoumissionsByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSoumission = soumissionRepository.saveAndFlush(soumission);

        // Get all the soumissionList where statut in
        defaultSoumissionFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllSoumissionsByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSoumission = soumissionRepository.saveAndFlush(soumission);

        // Get all the soumissionList where statut is not null
        defaultSoumissionFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllSoumissionsByEvaluationIsEqualToSomething() throws Exception {
        Evaluation evaluation;
        if (TestUtil.findAll(em, Evaluation.class).isEmpty()) {
            soumissionRepository.saveAndFlush(soumission);
            evaluation = EvaluationResourceIT.createEntity();
        } else {
            evaluation = TestUtil.findAll(em, Evaluation.class).get(0);
        }
        em.persist(evaluation);
        em.flush();
        soumission.setEvaluation(evaluation);
        soumissionRepository.saveAndFlush(soumission);
        Long evaluationId = evaluation.getId();
        // Get all the soumissionList where evaluation equals to evaluationId
        defaultSoumissionShouldBeFound("evaluationId.equals=" + evaluationId);

        // Get all the soumissionList where evaluation equals to (evaluationId + 1)
        defaultSoumissionShouldNotBeFound("evaluationId.equals=" + (evaluationId + 1));
    }

    @Test
    @Transactional
    void getAllSoumissionsByAppelOffreIsEqualToSomething() throws Exception {
        AppelOffre appelOffre;
        if (TestUtil.findAll(em, AppelOffre.class).isEmpty()) {
            soumissionRepository.saveAndFlush(soumission);
            appelOffre = AppelOffreResourceIT.createEntity();
        } else {
            appelOffre = TestUtil.findAll(em, AppelOffre.class).get(0);
        }
        em.persist(appelOffre);
        em.flush();
        soumission.setAppelOffre(appelOffre);
        soumissionRepository.saveAndFlush(soumission);
        Long appelOffreId = appelOffre.getId();
        // Get all the soumissionList where appelOffre equals to appelOffreId
        defaultSoumissionShouldBeFound("appelOffreId.equals=" + appelOffreId);

        // Get all the soumissionList where appelOffre equals to (appelOffreId + 1)
        defaultSoumissionShouldNotBeFound("appelOffreId.equals=" + (appelOffreId + 1));
    }

    @Test
    @Transactional
    void getAllSoumissionsByCandidatIsEqualToSomething() throws Exception {
        Candidat candidat;
        if (TestUtil.findAll(em, Candidat.class).isEmpty()) {
            soumissionRepository.saveAndFlush(soumission);
            candidat = CandidatResourceIT.createEntity();
        } else {
            candidat = TestUtil.findAll(em, Candidat.class).get(0);
        }
        em.persist(candidat);
        em.flush();
        soumission.setCandidat(candidat);
        soumissionRepository.saveAndFlush(soumission);
        Long candidatId = candidat.getId();
        // Get all the soumissionList where candidat equals to candidatId
        defaultSoumissionShouldBeFound("candidatId.equals=" + candidatId);

        // Get all the soumissionList where candidat equals to (candidatId + 1)
        defaultSoumissionShouldNotBeFound("candidatId.equals=" + (candidatId + 1));
    }

    private void defaultSoumissionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSoumissionShouldBeFound(shouldBeFound);
        defaultSoumissionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSoumissionShouldBeFound(String filter) throws Exception {
        restSoumissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(soumission.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateSoumission").value(hasItem(DEFAULT_DATE_SOUMISSION.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));

        // Check, that the count call also returns 1
        restSoumissionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSoumissionShouldNotBeFound(String filter) throws Exception {
        restSoumissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSoumissionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSoumission() throws Exception {
        // Get the soumission
        restSoumissionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSoumission() throws Exception {
        // Initialize the database
        insertedSoumission = soumissionRepository.saveAndFlush(soumission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the soumission
        Soumission updatedSoumission = soumissionRepository.findById(soumission.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSoumission are not directly saved in db
        em.detach(updatedSoumission);
        updatedSoumission.dateSoumission(UPDATED_DATE_SOUMISSION).statut(UPDATED_STATUT);
        SoumissionDTO soumissionDTO = soumissionMapper.toDto(updatedSoumission);

        restSoumissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, soumissionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(soumissionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Soumission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSoumissionToMatchAllProperties(updatedSoumission);
    }

    @Test
    @Transactional
    void putNonExistingSoumission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        soumission.setId(longCount.incrementAndGet());

        // Create the Soumission
        SoumissionDTO soumissionDTO = soumissionMapper.toDto(soumission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSoumissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, soumissionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(soumissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Soumission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSoumission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        soumission.setId(longCount.incrementAndGet());

        // Create the Soumission
        SoumissionDTO soumissionDTO = soumissionMapper.toDto(soumission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSoumissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(soumissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Soumission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSoumission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        soumission.setId(longCount.incrementAndGet());

        // Create the Soumission
        SoumissionDTO soumissionDTO = soumissionMapper.toDto(soumission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSoumissionMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(soumissionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Soumission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSoumissionWithPatch() throws Exception {
        // Initialize the database
        insertedSoumission = soumissionRepository.saveAndFlush(soumission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the soumission using partial update
        Soumission partialUpdatedSoumission = new Soumission();
        partialUpdatedSoumission.setId(soumission.getId());

        partialUpdatedSoumission.statut(UPDATED_STATUT);

        restSoumissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSoumission.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSoumission))
            )
            .andExpect(status().isOk());

        // Validate the Soumission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSoumissionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSoumission, soumission),
            getPersistedSoumission(soumission)
        );
    }

    @Test
    @Transactional
    void fullUpdateSoumissionWithPatch() throws Exception {
        // Initialize the database
        insertedSoumission = soumissionRepository.saveAndFlush(soumission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the soumission using partial update
        Soumission partialUpdatedSoumission = new Soumission();
        partialUpdatedSoumission.setId(soumission.getId());

        partialUpdatedSoumission.dateSoumission(UPDATED_DATE_SOUMISSION).statut(UPDATED_STATUT);

        restSoumissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSoumission.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSoumission))
            )
            .andExpect(status().isOk());

        // Validate the Soumission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSoumissionUpdatableFieldsEquals(partialUpdatedSoumission, getPersistedSoumission(partialUpdatedSoumission));
    }

    @Test
    @Transactional
    void patchNonExistingSoumission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        soumission.setId(longCount.incrementAndGet());

        // Create the Soumission
        SoumissionDTO soumissionDTO = soumissionMapper.toDto(soumission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSoumissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, soumissionDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(soumissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Soumission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSoumission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        soumission.setId(longCount.incrementAndGet());

        // Create the Soumission
        SoumissionDTO soumissionDTO = soumissionMapper.toDto(soumission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSoumissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(soumissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Soumission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSoumission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        soumission.setId(longCount.incrementAndGet());

        // Create the Soumission
        SoumissionDTO soumissionDTO = soumissionMapper.toDto(soumission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSoumissionMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(soumissionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Soumission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSoumission() throws Exception {
        // Initialize the database
        insertedSoumission = soumissionRepository.saveAndFlush(soumission);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the soumission
        restSoumissionMockMvc
            .perform(delete(ENTITY_API_URL_ID, soumission.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return soumissionRepository.count();
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

    protected Soumission getPersistedSoumission(Soumission soumission) {
        return soumissionRepository.findById(soumission.getId()).orElseThrow();
    }

    protected void assertPersistedSoumissionToMatchAllProperties(Soumission expectedSoumission) {
        assertSoumissionAllPropertiesEquals(expectedSoumission, getPersistedSoumission(expectedSoumission));
    }

    protected void assertPersistedSoumissionToMatchUpdatableProperties(Soumission expectedSoumission) {
        assertSoumissionAllUpdatablePropertiesEquals(expectedSoumission, getPersistedSoumission(expectedSoumission));
    }
}
