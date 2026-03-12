package com.mycompany.iaeval.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.iaeval.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SoumissionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SoumissionDTO.class);
        SoumissionDTO soumissionDTO1 = new SoumissionDTO();
        soumissionDTO1.setId(1L);
        SoumissionDTO soumissionDTO2 = new SoumissionDTO();
        assertThat(soumissionDTO1).isNotEqualTo(soumissionDTO2);
        soumissionDTO2.setId(soumissionDTO1.getId());
        assertThat(soumissionDTO1).isEqualTo(soumissionDTO2);
        soumissionDTO2.setId(2L);
        assertThat(soumissionDTO1).isNotEqualTo(soumissionDTO2);
        soumissionDTO1.setId(null);
        assertThat(soumissionDTO1).isNotEqualTo(soumissionDTO2);
    }
}
