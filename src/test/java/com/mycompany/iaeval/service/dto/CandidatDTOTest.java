package com.mycompany.iaeval.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.iaeval.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CandidatDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CandidatDTO.class);
        CandidatDTO candidatDTO1 = new CandidatDTO();
        candidatDTO1.setId(1L);
        CandidatDTO candidatDTO2 = new CandidatDTO();
        assertThat(candidatDTO1).isNotEqualTo(candidatDTO2);
        candidatDTO2.setId(candidatDTO1.getId());
        assertThat(candidatDTO1).isEqualTo(candidatDTO2);
        candidatDTO2.setId(2L);
        assertThat(candidatDTO1).isNotEqualTo(candidatDTO2);
        candidatDTO1.setId(null);
        assertThat(candidatDTO1).isNotEqualTo(candidatDTO2);
    }
}
