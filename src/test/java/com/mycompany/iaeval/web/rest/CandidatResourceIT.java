package com.mycompany.iaeval.web.rest;

import static com.mycompany.iaeval.domain.CandidatAsserts.*;
import static com.mycompany.iaeval.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.iaeval.IntegrationTest;
import com.mycompany.iaeval.domain.Candidat;
import com.mycompany.iaeval.repository.CandidatRepository;
import com.mycompany.iaeval.service.dto.CandidatDTO;
import com.mycompany.iaeval.service.mapper.CandidatMapper;
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
 * Integration tests for the {@link CandidatResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CandidatResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_SIRET = "AAAAAAAAAA";
    private static final String UPDATED_SIRET = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/candidats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private CandidatMapper candidatMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCandidatMockMvc;

    private Candidat candidat;

    private Candidat insertedCandidat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Candidat createEntity() {
        return new Candidat().nom(DEFAULT_NOM).siret(DEFAULT_SIRET).email(DEFAULT_EMAIL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Candidat createUpdatedEntity() {
        return new Candidat().nom(UPDATED_NOM).siret(UPDATED_SIRET).email(UPDATED_EMAIL);
    }

    @BeforeEach
    void initTest() {
        candidat = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCandidat != null) {
            candidatRepository.delete(insertedCandidat);
            insertedCandidat = null;
        }
    }

    @Test
    @Transactional
    void createCandidat() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);
        var returnedCandidatDTO = om.readValue(
            restCandidatMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(candidatDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CandidatDTO.class
        );

        // Validate the Candidat in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCandidat = candidatMapper.toEntity(returnedCandidatDTO);
        assertCandidatUpdatableFieldsEquals(returnedCandidat, getPersistedCandidat(returnedCandidat));

        insertedCandidat = returnedCandidat;
    }

    @Test
    @Transactional
    void createCandidatWithExistingId() throws Exception {
        // Create the Candidat with an existing ID
        candidat.setId(1L);
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCandidatMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(candidatDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidat.setNom(null);

        // Create the Candidat, which fails.
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        restCandidatMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(candidatDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCandidats() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList
        restCandidatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(candidat.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].siret").value(hasItem(DEFAULT_SIRET)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));
    }

    @Test
    @Transactional
    void getCandidat() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get the candidat
        restCandidatMockMvc
            .perform(get(ENTITY_API_URL_ID, candidat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(candidat.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.siret").value(DEFAULT_SIRET))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));
    }

    @Test
    @Transactional
    void getCandidatsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        Long id = candidat.getId();

        defaultCandidatFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCandidatFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCandidatFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCandidatsByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where nom equals to
        defaultCandidatFiltering("nom.equals=" + DEFAULT_NOM, "nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllCandidatsByNomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where nom in
        defaultCandidatFiltering("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM, "nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllCandidatsByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where nom is not null
        defaultCandidatFiltering("nom.specified=true", "nom.specified=false");
    }

    @Test
    @Transactional
    void getAllCandidatsByNomContainsSomething() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where nom contains
        defaultCandidatFiltering("nom.contains=" + DEFAULT_NOM, "nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllCandidatsByNomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where nom does not contain
        defaultCandidatFiltering("nom.doesNotContain=" + UPDATED_NOM, "nom.doesNotContain=" + DEFAULT_NOM);
    }

    @Test
    @Transactional
    void getAllCandidatsBySiretIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where siret equals to
        defaultCandidatFiltering("siret.equals=" + DEFAULT_SIRET, "siret.equals=" + UPDATED_SIRET);
    }

    @Test
    @Transactional
    void getAllCandidatsBySiretIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where siret in
        defaultCandidatFiltering("siret.in=" + DEFAULT_SIRET + "," + UPDATED_SIRET, "siret.in=" + UPDATED_SIRET);
    }

    @Test
    @Transactional
    void getAllCandidatsBySiretIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where siret is not null
        defaultCandidatFiltering("siret.specified=true", "siret.specified=false");
    }

    @Test
    @Transactional
    void getAllCandidatsBySiretContainsSomething() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where siret contains
        defaultCandidatFiltering("siret.contains=" + DEFAULT_SIRET, "siret.contains=" + UPDATED_SIRET);
    }

    @Test
    @Transactional
    void getAllCandidatsBySiretNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where siret does not contain
        defaultCandidatFiltering("siret.doesNotContain=" + UPDATED_SIRET, "siret.doesNotContain=" + DEFAULT_SIRET);
    }

    @Test
    @Transactional
    void getAllCandidatsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where email equals to
        defaultCandidatFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllCandidatsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where email in
        defaultCandidatFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllCandidatsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where email is not null
        defaultCandidatFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllCandidatsByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where email contains
        defaultCandidatFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllCandidatsByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        // Get all the candidatList where email does not contain
        defaultCandidatFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    private void defaultCandidatFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCandidatShouldBeFound(shouldBeFound);
        defaultCandidatShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCandidatShouldBeFound(String filter) throws Exception {
        restCandidatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(candidat.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].siret").value(hasItem(DEFAULT_SIRET)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));

        // Check, that the count call also returns 1
        restCandidatMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCandidatShouldNotBeFound(String filter) throws Exception {
        restCandidatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCandidatMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCandidat() throws Exception {
        // Get the candidat
        restCandidatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCandidat() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the candidat
        Candidat updatedCandidat = candidatRepository.findById(candidat.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCandidat are not directly saved in db
        em.detach(updatedCandidat);
        updatedCandidat.nom(UPDATED_NOM).siret(UPDATED_SIRET).email(UPDATED_EMAIL);
        CandidatDTO candidatDTO = candidatMapper.toDto(updatedCandidat);

        restCandidatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, candidatDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(candidatDTO))
            )
            .andExpect(status().isOk());

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCandidatToMatchAllProperties(updatedCandidat);
    }

    @Test
    @Transactional
    void putNonExistingCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCandidatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, candidatDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(candidatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandidatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(candidatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandidatMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(candidatDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCandidatWithPatch() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the candidat using partial update
        Candidat partialUpdatedCandidat = new Candidat();
        partialUpdatedCandidat.setId(candidat.getId());

        restCandidatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCandidat.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCandidat))
            )
            .andExpect(status().isOk());

        // Validate the Candidat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCandidatUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCandidat, candidat), getPersistedCandidat(candidat));
    }

    @Test
    @Transactional
    void fullUpdateCandidatWithPatch() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the candidat using partial update
        Candidat partialUpdatedCandidat = new Candidat();
        partialUpdatedCandidat.setId(candidat.getId());

        partialUpdatedCandidat.nom(UPDATED_NOM).siret(UPDATED_SIRET).email(UPDATED_EMAIL);

        restCandidatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCandidat.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCandidat))
            )
            .andExpect(status().isOk());

        // Validate the Candidat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCandidatUpdatableFieldsEquals(partialUpdatedCandidat, getPersistedCandidat(partialUpdatedCandidat));
    }

    @Test
    @Transactional
    void patchNonExistingCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCandidatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, candidatDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(candidatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandidatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(candidatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandidatMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(candidatDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCandidat() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.saveAndFlush(candidat);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the candidat
        restCandidatMockMvc
            .perform(delete(ENTITY_API_URL_ID, candidat.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return candidatRepository.count();
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

    protected Candidat getPersistedCandidat(Candidat candidat) {
        return candidatRepository.findById(candidat.getId()).orElseThrow();
    }

    protected void assertPersistedCandidatToMatchAllProperties(Candidat expectedCandidat) {
        assertCandidatAllPropertiesEquals(expectedCandidat, getPersistedCandidat(expectedCandidat));
    }

    protected void assertPersistedCandidatToMatchUpdatableProperties(Candidat expectedCandidat) {
        assertCandidatAllUpdatablePropertiesEquals(expectedCandidat, getPersistedCandidat(expectedCandidat));
    }
}
