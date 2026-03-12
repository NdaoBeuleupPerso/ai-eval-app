package com.mycompany.iaeval.domain;

import static com.mycompany.iaeval.domain.AppelOffreTestSamples.*;
import static com.mycompany.iaeval.domain.CritereTestSamples.*;
import static com.mycompany.iaeval.domain.SoumissionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.iaeval.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AppelOffreTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppelOffre.class);
        AppelOffre appelOffre1 = getAppelOffreSample1();
        AppelOffre appelOffre2 = new AppelOffre();
        assertThat(appelOffre1).isNotEqualTo(appelOffre2);

        appelOffre2.setId(appelOffre1.getId());
        assertThat(appelOffre1).isEqualTo(appelOffre2);

        appelOffre2 = getAppelOffreSample2();
        assertThat(appelOffre1).isNotEqualTo(appelOffre2);
    }

    @Test
    void criteresTest() {
        AppelOffre appelOffre = getAppelOffreRandomSampleGenerator();
        Critere critereBack = getCritereRandomSampleGenerator();

        appelOffre.addCriteres(critereBack);
        assertThat(appelOffre.getCriteres()).containsOnly(critereBack);
        assertThat(critereBack.getAppelOffre()).isEqualTo(appelOffre);

        appelOffre.removeCriteres(critereBack);
        assertThat(appelOffre.getCriteres()).doesNotContain(critereBack);
        assertThat(critereBack.getAppelOffre()).isNull();

        appelOffre.criteres(new HashSet<>(Set.of(critereBack)));
        assertThat(appelOffre.getCriteres()).containsOnly(critereBack);
        assertThat(critereBack.getAppelOffre()).isEqualTo(appelOffre);

        appelOffre.setCriteres(new HashSet<>());
        assertThat(appelOffre.getCriteres()).doesNotContain(critereBack);
        assertThat(critereBack.getAppelOffre()).isNull();
    }

    @Test
    void soumissionsTest() {
        AppelOffre appelOffre = getAppelOffreRandomSampleGenerator();
        Soumission soumissionBack = getSoumissionRandomSampleGenerator();

        appelOffre.addSoumissions(soumissionBack);
        assertThat(appelOffre.getSoumissions()).containsOnly(soumissionBack);
        assertThat(soumissionBack.getAppelOffre()).isEqualTo(appelOffre);

        appelOffre.removeSoumissions(soumissionBack);
        assertThat(appelOffre.getSoumissions()).doesNotContain(soumissionBack);
        assertThat(soumissionBack.getAppelOffre()).isNull();

        appelOffre.soumissions(new HashSet<>(Set.of(soumissionBack)));
        assertThat(appelOffre.getSoumissions()).containsOnly(soumissionBack);
        assertThat(soumissionBack.getAppelOffre()).isEqualTo(appelOffre);

        appelOffre.setSoumissions(new HashSet<>());
        assertThat(appelOffre.getSoumissions()).doesNotContain(soumissionBack);
        assertThat(soumissionBack.getAppelOffre()).isNull();
    }
}
