package com.mycompany.iaeval.domain;

import static com.mycompany.iaeval.domain.DocumentJointTestSamples.*;
import static com.mycompany.iaeval.domain.SoumissionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.iaeval.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DocumentJointTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DocumentJoint.class);
        DocumentJoint documentJoint1 = getDocumentJointSample1();
        DocumentJoint documentJoint2 = new DocumentJoint();
        assertThat(documentJoint1).isNotEqualTo(documentJoint2);

        documentJoint2.setId(documentJoint1.getId());
        assertThat(documentJoint1).isEqualTo(documentJoint2);

        documentJoint2 = getDocumentJointSample2();
        assertThat(documentJoint1).isNotEqualTo(documentJoint2);
    }

    @Test
    void soumissionTest() {
        DocumentJoint documentJoint = getDocumentJointRandomSampleGenerator();
        Soumission soumissionBack = getSoumissionRandomSampleGenerator();

        documentJoint.setSoumission(soumissionBack);
        assertThat(documentJoint.getSoumission()).isEqualTo(soumissionBack);

        documentJoint.soumission(null);
        assertThat(documentJoint.getSoumission()).isNull();
    }
}
