package com.mycompany.iaeval.domain;

import static com.mycompany.iaeval.domain.AppelOffreTestSamples.*;
import static com.mycompany.iaeval.domain.CritereTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.iaeval.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CritereTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Critere.class);
        Critere critere1 = getCritereSample1();
        Critere critere2 = new Critere();
        assertThat(critere1).isNotEqualTo(critere2);

        critere2.setId(critere1.getId());
        assertThat(critere1).isEqualTo(critere2);

        critere2 = getCritereSample2();
        assertThat(critere1).isNotEqualTo(critere2);
    }

    @Test
    void appelOffreTest() {
        Critere critere = getCritereRandomSampleGenerator();
        AppelOffre appelOffreBack = getAppelOffreRandomSampleGenerator();

        critere.setAppelOffre(appelOffreBack);
        assertThat(critere.getAppelOffre()).isEqualTo(appelOffreBack);

        critere.appelOffre(null);
        assertThat(critere.getAppelOffre()).isNull();
    }
}
