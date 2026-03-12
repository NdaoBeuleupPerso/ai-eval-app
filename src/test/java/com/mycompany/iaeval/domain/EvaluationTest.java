package com.mycompany.iaeval.domain;

import static com.mycompany.iaeval.domain.EvaluationTestSamples.*;
import static com.mycompany.iaeval.domain.SoumissionTestSamples.*;
import static com.mycompany.iaeval.domain.TraceAuditTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.iaeval.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EvaluationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Evaluation.class);
        Evaluation evaluation1 = getEvaluationSample1();
        Evaluation evaluation2 = new Evaluation();
        assertThat(evaluation1).isNotEqualTo(evaluation2);

        evaluation2.setId(evaluation1.getId());
        assertThat(evaluation1).isEqualTo(evaluation2);

        evaluation2 = getEvaluationSample2();
        assertThat(evaluation1).isNotEqualTo(evaluation2);
    }

    @Test
    void tracesTest() {
        Evaluation evaluation = getEvaluationRandomSampleGenerator();
        TraceAudit traceAuditBack = getTraceAuditRandomSampleGenerator();

        evaluation.addTraces(traceAuditBack);
        assertThat(evaluation.getTraces()).containsOnly(traceAuditBack);
        assertThat(traceAuditBack.getEvaluation()).isEqualTo(evaluation);

        evaluation.removeTraces(traceAuditBack);
        assertThat(evaluation.getTraces()).doesNotContain(traceAuditBack);
        assertThat(traceAuditBack.getEvaluation()).isNull();

        evaluation.traces(new HashSet<>(Set.of(traceAuditBack)));
        assertThat(evaluation.getTraces()).containsOnly(traceAuditBack);
        assertThat(traceAuditBack.getEvaluation()).isEqualTo(evaluation);

        evaluation.setTraces(new HashSet<>());
        assertThat(evaluation.getTraces()).doesNotContain(traceAuditBack);
        assertThat(traceAuditBack.getEvaluation()).isNull();
    }

    @Test
    void soumissionTest() {
        Evaluation evaluation = getEvaluationRandomSampleGenerator();
        Soumission soumissionBack = getSoumissionRandomSampleGenerator();

        evaluation.setSoumission(soumissionBack);
        assertThat(evaluation.getSoumission()).isEqualTo(soumissionBack);
        assertThat(soumissionBack.getEvaluation()).isEqualTo(evaluation);

        evaluation.soumission(null);
        assertThat(evaluation.getSoumission()).isNull();
        assertThat(soumissionBack.getEvaluation()).isNull();
    }
}
