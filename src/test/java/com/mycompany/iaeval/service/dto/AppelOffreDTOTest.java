package com.mycompany.iaeval.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.iaeval.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AppelOffreDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppelOffreDTO.class);
        AppelOffreDTO appelOffreDTO1 = new AppelOffreDTO();
        appelOffreDTO1.setId(1L);
        AppelOffreDTO appelOffreDTO2 = new AppelOffreDTO();
        assertThat(appelOffreDTO1).isNotEqualTo(appelOffreDTO2);
        appelOffreDTO2.setId(appelOffreDTO1.getId());
        assertThat(appelOffreDTO1).isEqualTo(appelOffreDTO2);
        appelOffreDTO2.setId(2L);
        assertThat(appelOffreDTO1).isNotEqualTo(appelOffreDTO2);
        appelOffreDTO1.setId(null);
        assertThat(appelOffreDTO1).isNotEqualTo(appelOffreDTO2);
    }
}
