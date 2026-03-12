package com.mycompany.iaeval.domain;

import static com.mycompany.iaeval.domain.ReferenceLegaleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.iaeval.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReferenceLegaleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReferenceLegale.class);
        ReferenceLegale referenceLegale1 = getReferenceLegaleSample1();
        ReferenceLegale referenceLegale2 = new ReferenceLegale();
        assertThat(referenceLegale1).isNotEqualTo(referenceLegale2);

        referenceLegale2.setId(referenceLegale1.getId());
        assertThat(referenceLegale1).isEqualTo(referenceLegale2);

        referenceLegale2 = getReferenceLegaleSample2();
        assertThat(referenceLegale1).isNotEqualTo(referenceLegale2);
    }
}
