package com.mycompany.iaeval.service.mapper;

import static com.mycompany.iaeval.domain.CritereAsserts.*;
import static com.mycompany.iaeval.domain.CritereTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CritereMapperTest {

    private CritereMapper critereMapper;

    @BeforeEach
    void setUp() {
        critereMapper = new CritereMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCritereSample1();
        var actual = critereMapper.toEntity(critereMapper.toDto(expected));
        assertCritereAllPropertiesEquals(expected, actual);
    }
}
