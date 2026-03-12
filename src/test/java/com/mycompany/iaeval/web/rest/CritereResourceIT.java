package com.mycompany.iaeval.web.rest;

import static com.mycompany.iaeval.domain.CritereAsserts.*;
import static com.mycompany.iaeval.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.iaeval.IntegrationTest;
import com.mycompany.iaeval.domain.Critere;
import com.mycompany.iaeval.domain.enumeration.TypeCritere;
import com.mycompany.iaeval.repository.CritereRepository;
import com.mycompany.iaeval.service.CritereService;
import com.mycompany.iaeval.service.dto.CritereDTO;
import com.mycompany.iaeval.service.mapper.CritereMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link CritereResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CritereResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final Double DEFAULT_PONDERATION = 1D;
    private static final Double UPDATED_PONDERATION = 2D;

    private static final TypeCritere DEFAULT_CATEGORIE = TypeCritere.ADMINISTRATIF;
    private static final TypeCritere UPDATED_CATEGORIE = TypeCritere.TECHNIQUE;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/criteres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CritereRepository critereRepository;

    @Mock
    private CritereRepository critereRepositoryMock;

    @Autowired
    private CritereMapper critereMapper;

    @Mock
    private CritereService critereServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCritereMockMvc;

    private Critere critere;

    private Critere insertedCritere;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Critere createEntity() {
        return new Critere()
            .nom(DEFAULT_NOM)
            .ponderation(DEFAULT_PONDERATION)
            .categorie(DEFAULT_CATEGORIE)
            .description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Critere createUpdatedEntity() {
        return new Critere()
            .nom(UPDATED_NOM)
            .ponderation(UPDATED_PONDERATION)
            .categorie(UPDATED_CATEGORIE)
            .description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        critere = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCritere != null) {
            critereRepository.delete(insertedCritere);
            insertedCritere = null;
        }
    }

    @Test
    @Transactional
    void createCritere() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Critere
        CritereDTO critereDTO = critereMapper.toDto(critere);
        var returnedCritereDTO = om.readValue(
            restCritereMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(critereDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CritereDTO.class
        );

        // Validate the Critere in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCritere = critereMapper.toEntity(returnedCritereDTO);
        assertCritereUpdatableFieldsEquals(returnedCritere, getPersistedCritere(returnedCritere));

        insertedCritere = returnedCritere;
    }

    @Test
    @Transactional
    void createCritereWithExistingId() throws Exception {
        // Create the Critere with an existing ID
        critere.setId(1L);
        CritereDTO critereDTO = critereMapper.toDto(critere);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCritereMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(critereDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Critere in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        critere.setNom(null);

        // Create the Critere, which fails.
        CritereDTO critereDTO = critereMapper.toDto(critere);

        restCritereMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(critereDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPonderationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        critere.setPonderation(null);

        // Create the Critere, which fails.
        CritereDTO critereDTO = critereMapper.toDto(critere);

        restCritereMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(critereDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCategorieIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        critere.setCategorie(null);

        // Create the Critere, which fails.
        CritereDTO critereDTO = critereMapper.toDto(critere);

        restCritereMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(critereDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCriteres() throws Exception {
        // Initialize the database
        insertedCritere = critereRepository.saveAndFlush(critere);

        // Get all the critereList
        restCritereMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(critere.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].ponderation").value(hasItem(DEFAULT_PONDERATION)))
            .andExpect(jsonPath("$.[*].categorie").value(hasItem(DEFAULT_CATEGORIE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCriteresWithEagerRelationshipsIsEnabled() throws Exception {
        when(critereServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCritereMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(critereServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCriteresWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(critereServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCritereMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(critereRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCritere() throws Exception {
        // Initialize the database
        insertedCritere = critereRepository.saveAndFlush(critere);

        // Get the critere
        restCritereMockMvc
            .perform(get(ENTITY_API_URL_ID, critere.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(critere.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.ponderation").value(DEFAULT_PONDERATION))
            .andExpect(jsonPath("$.categorie").value(DEFAULT_CATEGORIE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingCritere() throws Exception {
        // Get the critere
        restCritereMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCritere() throws Exception {
        // Initialize the database
        insertedCritere = critereRepository.saveAndFlush(critere);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the critere
        Critere updatedCritere = critereRepository.findById(critere.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCritere are not directly saved in db
        em.detach(updatedCritere);
        updatedCritere.nom(UPDATED_NOM).ponderation(UPDATED_PONDERATION).categorie(UPDATED_CATEGORIE).description(UPDATED_DESCRIPTION);
        CritereDTO critereDTO = critereMapper.toDto(updatedCritere);

        restCritereMockMvc
            .perform(
                put(ENTITY_API_URL_ID, critereDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(critereDTO))
            )
            .andExpect(status().isOk());

        // Validate the Critere in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCritereToMatchAllProperties(updatedCritere);
    }

    @Test
    @Transactional
    void putNonExistingCritere() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        critere.setId(longCount.incrementAndGet());

        // Create the Critere
        CritereDTO critereDTO = critereMapper.toDto(critere);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCritereMockMvc
            .perform(
                put(ENTITY_API_URL_ID, critereDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(critereDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Critere in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCritere() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        critere.setId(longCount.incrementAndGet());

        // Create the Critere
        CritereDTO critereDTO = critereMapper.toDto(critere);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCritereMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(critereDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Critere in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCritere() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        critere.setId(longCount.incrementAndGet());

        // Create the Critere
        CritereDTO critereDTO = critereMapper.toDto(critere);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCritereMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(critereDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Critere in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCritereWithPatch() throws Exception {
        // Initialize the database
        insertedCritere = critereRepository.saveAndFlush(critere);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the critere using partial update
        Critere partialUpdatedCritere = new Critere();
        partialUpdatedCritere.setId(critere.getId());

        partialUpdatedCritere
            .nom(UPDATED_NOM)
            .ponderation(UPDATED_PONDERATION)
            .categorie(UPDATED_CATEGORIE)
            .description(UPDATED_DESCRIPTION);

        restCritereMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCritere.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCritere))
            )
            .andExpect(status().isOk());

        // Validate the Critere in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCritereUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCritere, critere), getPersistedCritere(critere));
    }

    @Test
    @Transactional
    void fullUpdateCritereWithPatch() throws Exception {
        // Initialize the database
        insertedCritere = critereRepository.saveAndFlush(critere);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the critere using partial update
        Critere partialUpdatedCritere = new Critere();
        partialUpdatedCritere.setId(critere.getId());

        partialUpdatedCritere
            .nom(UPDATED_NOM)
            .ponderation(UPDATED_PONDERATION)
            .categorie(UPDATED_CATEGORIE)
            .description(UPDATED_DESCRIPTION);

        restCritereMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCritere.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCritere))
            )
            .andExpect(status().isOk());

        // Validate the Critere in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCritereUpdatableFieldsEquals(partialUpdatedCritere, getPersistedCritere(partialUpdatedCritere));
    }

    @Test
    @Transactional
    void patchNonExistingCritere() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        critere.setId(longCount.incrementAndGet());

        // Create the Critere
        CritereDTO critereDTO = critereMapper.toDto(critere);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCritereMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, critereDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(critereDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Critere in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCritere() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        critere.setId(longCount.incrementAndGet());

        // Create the Critere
        CritereDTO critereDTO = critereMapper.toDto(critere);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCritereMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(critereDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Critere in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCritere() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        critere.setId(longCount.incrementAndGet());

        // Create the Critere
        CritereDTO critereDTO = critereMapper.toDto(critere);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCritereMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(critereDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Critere in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCritere() throws Exception {
        // Initialize the database
        insertedCritere = critereRepository.saveAndFlush(critere);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the critere
        restCritereMockMvc
            .perform(delete(ENTITY_API_URL_ID, critere.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return critereRepository.count();
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

    protected Critere getPersistedCritere(Critere critere) {
        return critereRepository.findById(critere.getId()).orElseThrow();
    }

    protected void assertPersistedCritereToMatchAllProperties(Critere expectedCritere) {
        assertCritereAllPropertiesEquals(expectedCritere, getPersistedCritere(expectedCritere));
    }

    protected void assertPersistedCritereToMatchUpdatableProperties(Critere expectedCritere) {
        assertCritereAllUpdatablePropertiesEquals(expectedCritere, getPersistedCritere(expectedCritere));
    }
}
