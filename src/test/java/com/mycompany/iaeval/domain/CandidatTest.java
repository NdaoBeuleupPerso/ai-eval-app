package com.mycompany.iaeval.domain;

import static com.mycompany.iaeval.domain.CandidatTestSamples.*;
import static com.mycompany.iaeval.domain.SoumissionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.iaeval.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CandidatTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Candidat.class);
        Candidat candidat1 = getCandidatSample1();
        Candidat candidat2 = new Candidat();
        assertThat(candidat1).isNotEqualTo(candidat2);

        candidat2.setId(candidat1.getId());
        assertThat(candidat1).isEqualTo(candidat2);

        candidat2 = getCandidatSample2();
        assertThat(candidat1).isNotEqualTo(candidat2);
    }

    @Test
    void soumissionsTest() {
        Candidat candidat = getCandidatRandomSampleGenerator();
        Soumission soumissionBack = getSoumissionRandomSampleGenerator();

        candidat.addSoumissions(soumissionBack);
        assertThat(candidat.getSoumissions()).containsOnly(soumissionBack);
        assertThat(soumissionBack.getCandidat()).isEqualTo(candidat);

        candidat.removeSoumissions(soumissionBack);
        assertThat(candidat.getSoumissions()).doesNotContain(soumissionBack);
        assertThat(soumissionBack.getCandidat()).isNull();

        candidat.soumissions(new HashSet<>(Set.of(soumissionBack)));
        assertThat(candidat.getSoumissions()).containsOnly(soumissionBack);
        assertThat(soumissionBack.getCandidat()).isEqualTo(candidat);

        candidat.setSoumissions(new HashSet<>());
        assertThat(candidat.getSoumissions()).doesNotContain(soumissionBack);
        assertThat(soumissionBack.getCandidat()).isNull();
    }
}
