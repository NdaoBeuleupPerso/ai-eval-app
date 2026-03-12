package com.mycompany.iaeval.service.mapper;

import static com.mycompany.iaeval.domain.TraceAuditAsserts.*;
import static com.mycompany.iaeval.domain.TraceAuditTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TraceAuditMapperTest {

    private TraceAuditMapper traceAuditMapper;

    @BeforeEach
    void setUp() {
        traceAuditMapper = new TraceAuditMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTraceAuditSample1();
        var actual = traceAuditMapper.toEntity(traceAuditMapper.toDto(expected));
        assertTraceAuditAllPropertiesEquals(expected, actual);
    }
}
