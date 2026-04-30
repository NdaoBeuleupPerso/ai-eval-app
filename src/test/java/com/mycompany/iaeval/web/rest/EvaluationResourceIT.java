package com.mycompany.iaeval.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.iaeval.IntegrationTest;
import com.mycompany.iaeval.domain.Evaluation;
import com.mycompany.iaeval.repository.EvaluationRepository;
import com.mycompany.iaeval.service.dto.EvaluationDTO;
import com.mycompany.iaeval.service.mapper.EvaluationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EvaluationResourceIT {

    private static final String DEFAULT_RAPPORT_ANALYSE = "AAAAAAAAAA";
    private static final String UPDATED_RAPPORT_ANALYSE = "BBBBBBBBBB";

    private static final String DEFAULT_DOCUMENT_PV = "AAAAAAAAAA";
    private static final String UPDATED_DOCUMENT_PV = "BBBBBBBBBB";

    private static final Double DEFAULT_SCORE_GLOBAL = 1D;
    private static final Double UPDATED_SCORE_GLOBAL = 2D;

    private static final String ENTITY_API_URL = "/api/evaluations";

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc restEvaluationMockMvc;

    private Evaluation evaluation;

    public static Evaluation createEntity() {
        return new Evaluation().rapportAnalyse(DEFAULT_RAPPORT_ANALYSE).documentPv(DEFAULT_DOCUMENT_PV).scoreGlobal(DEFAULT_SCORE_GLOBAL);
    }

    @BeforeEach
    void initTest() {
        evaluation = createEntity();
    }

    @Test
    @Transactional
    void createEvaluation() throws Exception {
        int databaseSizeBeforeCreate = evaluationRepository.findAll().size();
        EvaluationDTO evaluationDTO = evaluationMapper.toDto(evaluation);
        restEvaluationMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(evaluationDTO)))
            .andExpect(status().isCreated());

        assertThat(evaluationRepository.findAll()).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    void getAllEvaluations() throws Exception {
        evaluationRepository.saveAndFlush(evaluation);

        restEvaluationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].rapportAnalyse").value(hasItem(DEFAULT_RAPPORT_ANALYSE)))
            .andExpect(jsonPath("$.[*].documentPv").value(hasItem(DEFAULT_DOCUMENT_PV)));
    }
}
