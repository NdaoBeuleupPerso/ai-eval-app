package com.mycompany.iaeval.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.iaeval.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReferenceLegaleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReferenceLegaleDTO.class);
        ReferenceLegaleDTO referenceLegaleDTO1 = new ReferenceLegaleDTO();
        referenceLegaleDTO1.setId(1L);
        ReferenceLegaleDTO referenceLegaleDTO2 = new ReferenceLegaleDTO();
        assertThat(referenceLegaleDTO1).isNotEqualTo(referenceLegaleDTO2);
        referenceLegaleDTO2.setId(referenceLegaleDTO1.getId());
        assertThat(referenceLegaleDTO1).isEqualTo(referenceLegaleDTO2);
        referenceLegaleDTO2.setId(2L);
        assertThat(referenceLegaleDTO1).isNotEqualTo(referenceLegaleDTO2);
        referenceLegaleDTO1.setId(null);
        assertThat(referenceLegaleDTO1).isNotEqualTo(referenceLegaleDTO2);
    }
}
