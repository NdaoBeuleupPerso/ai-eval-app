package com.mycompany.iaeval.web.rest;

import static com.mycompany.iaeval.domain.AppelOffreAsserts.assertAppelOffreAllPropertiesEquals;
import static com.mycompany.iaeval.domain.AppelOffreAsserts.assertAppelOffreUpdatableFieldsEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION_CONTENT_TYPE = "text/plain";
    private static final String UPDATED_DESCRIPTION_CONTENT_TYPE = "text/html";

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

    public static AppelOffre createEntity() {
        return new AppelOffre()
            .reference(DEFAULT_REFERENCE)
            .titre(DEFAULT_TITRE)
            .description(DEFAULT_DESCRIPTION)
            .descriptionContentType(DEFAULT_DESCRIPTION_CONTENT_TYPE)
            .dateCloture(DEFAULT_DATE_CLOTURE)
            .statut(DEFAULT_STATUT);
    }

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

        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAppelOffre = appelOffreMapper.toEntity(returnedAppelOffreDTO);
        assertAppelOffreUpdatableFieldsEquals(returnedAppelOffre, getPersistedAppelOffre(returnedAppelOffre));

        insertedAppelOffre = returnedAppelOffre;
    }

    @Test
    @Transactional
    void getAllAppelOffres() throws Exception {
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        restAppelOffreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appelOffre.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));
    }

    @Test
    @Transactional
    void getAppelOffre() throws Exception {
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);

        restAppelOffreMockMvc
            .perform(get(ENTITY_API_URL_ID, appelOffre.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appelOffre.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.titre").value(DEFAULT_TITRE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void putExistingAppelOffre() throws Exception {
        insertedAppelOffre = appelOffreRepository.saveAndFlush(appelOffre);
        long databaseSizeBeforeUpdate = getRepositoryCount();

        AppelOffre updatedAppelOffre = appelOffreRepository.findById(appelOffre.getId()).orElseThrow();
        em.detach(updatedAppelOffre);
        updatedAppelOffre.reference(UPDATED_REFERENCE).titre(UPDATED_TITRE).description(UPDATED_DESCRIPTION).statut(UPDATED_STATUT);

        AppelOffreDTO appelOffreDTO = appelOffreMapper.toDto(updatedAppelOffre);

        restAppelOffreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appelOffreDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appelOffreDTO))
            )
            .andExpect(status().isOk());

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAppelOffreToMatchAllProperties(updatedAppelOffre);
    }

    protected long getRepositoryCount() {
        return appelOffreRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected AppelOffre getPersistedAppelOffre(AppelOffre appelOffre) {
        return appelOffreRepository.findById(appelOffre.getId()).orElseThrow();
    }

    protected void assertPersistedAppelOffreToMatchAllProperties(AppelOffre expected) {
        assertAppelOffreAllPropertiesEquals(expected, getPersistedAppelOffre(expected));
    }
}
