package com.mycompany.iaeval.domain;

import static com.mycompany.iaeval.domain.EvaluationTestSamples.*;
import static com.mycompany.iaeval.domain.TraceAuditTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.iaeval.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TraceAuditTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TraceAudit.class);
        TraceAudit traceAudit1 = getTraceAuditSample1();
        TraceAudit traceAudit2 = new TraceAudit();
        assertThat(traceAudit1).isNotEqualTo(traceAudit2);

        traceAudit2.setId(traceAudit1.getId());
        assertThat(traceAudit1).isEqualTo(traceAudit2);

        traceAudit2 = getTraceAuditSample2();
        assertThat(traceAudit1).isNotEqualTo(traceAudit2);
    }

    @Test
    void evaluationTest() {
        TraceAudit traceAudit = getTraceAuditRandomSampleGenerator();
        Evaluation evaluationBack = getEvaluationRandomSampleGenerator();

        traceAudit.setEvaluation(evaluationBack);
        assertThat(traceAudit.getEvaluation()).isEqualTo(evaluationBack);

        traceAudit.evaluation(null);
        assertThat(traceAudit.getEvaluation()).isNull();
    }
}
