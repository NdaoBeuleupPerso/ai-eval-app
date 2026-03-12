package com.mycompany.iaeval.service.mapper;

import static com.mycompany.iaeval.domain.SoumissionAsserts.*;
import static com.mycompany.iaeval.domain.SoumissionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SoumissionMapperTest {

    private SoumissionMapper soumissionMapper;

    @BeforeEach
    void setUp() {
        soumissionMapper = new SoumissionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSoumissionSample1();
        var actual = soumissionMapper.toEntity(soumissionMapper.toDto(expected));
        assertSoumissionAllPropertiesEquals(expected, actual);
    }
}
