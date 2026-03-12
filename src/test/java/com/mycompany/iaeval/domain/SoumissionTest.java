package com.mycompany.iaeval.domain;

import static com.mycompany.iaeval.domain.AppelOffreTestSamples.*;
import static com.mycompany.iaeval.domain.CandidatTestSamples.*;
import static com.mycompany.iaeval.domain.DocumentJointTestSamples.*;
import static com.mycompany.iaeval.domain.EvaluationTestSamples.*;
import static com.mycompany.iaeval.domain.SoumissionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.iaeval.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SoumissionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Soumission.class);
        Soumission soumission1 = getSoumissionSample1();
        Soumission soumission2 = new Soumission();
        assertThat(soumission1).isNotEqualTo(soumission2);

        soumission2.setId(soumission1.getId());
        assertThat(soumission1).isEqualTo(soumission2);

        soumission2 = getSoumissionSample2();
        assertThat(soumission1).isNotEqualTo(soumission2);
    }

    @Test
    void evaluationTest() {
        Soumission soumission = getSoumissionRandomSampleGenerator();
        Evaluation evaluationBack = getEvaluationRandomSampleGenerator();

        soumission.setEvaluation(evaluationBack);
        assertThat(soumission.getEvaluation()).isEqualTo(evaluationBack);

        soumission.evaluation(null);
        assertThat(soumission.getEvaluation()).isNull();
    }

    @Test
    void documentsTest() {
        Soumission soumission = getSoumissionRandomSampleGenerator();
        DocumentJoint documentJointBack = getDocumentJointRandomSampleGenerator();

        soumission.addDocuments(documentJointBack);
        assertThat(soumission.getDocuments()).containsOnly(documentJointBack);
        assertThat(documentJointBack.getSoumission()).isEqualTo(soumission);

        soumission.removeDocuments(documentJointBack);
        assertThat(soumission.getDocuments()).doesNotContain(documentJointBack);
        assertThat(documentJointBack.getSoumission()).isNull();

        soumission.documents(new HashSet<>(Set.of(documentJointBack)));
        assertThat(soumission.getDocuments()).containsOnly(documentJointBack);
        assertThat(documentJointBack.getSoumission()).isEqualTo(soumission);

        soumission.setDocuments(new HashSet<>());
        assertThat(soumission.getDocuments()).doesNotContain(documentJointBack);
        assertThat(documentJointBack.getSoumission()).isNull();
    }

    @Test
    void appelOffreTest() {
        Soumission soumission = getSoumissionRandomSampleGenerator();
        AppelOffre appelOffreBack = getAppelOffreRandomSampleGenerator();

        soumission.setAppelOffre(appelOffreBack);
        assertThat(soumission.getAppelOffre()).isEqualTo(appelOffreBack);

        soumission.appelOffre(null);
        assertThat(soumission.getAppelOffre()).isNull();
    }

    @Test
    void candidatTest() {
        Soumission soumission = getSoumissionRandomSampleGenerator();
        Candidat candidatBack = getCandidatRandomSampleGenerator();

        soumission.setCandidat(candidatBack);
        assertThat(soumission.getCandidat()).isEqualTo(candidatBack);

        soumission.candidat(null);
        assertThat(soumission.getCandidat()).isNull();
    }
}
