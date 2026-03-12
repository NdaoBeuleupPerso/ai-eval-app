package com.mycompany.iaeval.service.mapper;

import static com.mycompany.iaeval.domain.CandidatAsserts.*;
import static com.mycompany.iaeval.domain.CandidatTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CandidatMapperTest {

    private CandidatMapper candidatMapper;

    @BeforeEach
    void setUp() {
        candidatMapper = new CandidatMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCandidatSample1();
        var actual = candidatMapper.toEntity(candidatMapper.toDto(expected));
        assertCandidatAllPropertiesEquals(expected, actual);
    }
}
