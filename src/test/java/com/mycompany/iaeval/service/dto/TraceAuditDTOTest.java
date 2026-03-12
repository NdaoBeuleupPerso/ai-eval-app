package com.mycompany.iaeval.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.iaeval.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TraceAuditDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TraceAuditDTO.class);
        TraceAuditDTO traceAuditDTO1 = new TraceAuditDTO();
        traceAuditDTO1.setId(1L);
        TraceAuditDTO traceAuditDTO2 = new TraceAuditDTO();
        assertThat(traceAuditDTO1).isNotEqualTo(traceAuditDTO2);
        traceAuditDTO2.setId(traceAuditDTO1.getId());
        assertThat(traceAuditDTO1).isEqualTo(traceAuditDTO2);
        traceAuditDTO2.setId(2L);
        assertThat(traceAuditDTO1).isNotEqualTo(traceAuditDTO2);
        traceAuditDTO1.setId(null);
        assertThat(traceAuditDTO1).isNotEqualTo(traceAuditDTO2);
    }
}
