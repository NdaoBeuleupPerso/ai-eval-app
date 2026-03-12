package com.mycompany.iaeval.service.mapper;

import static com.mycompany.iaeval.domain.ReferenceLegaleAsserts.*;
import static com.mycompany.iaeval.domain.ReferenceLegaleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReferenceLegaleMapperTest {

    private ReferenceLegaleMapper referenceLegaleMapper;

    @BeforeEach
    void setUp() {
        referenceLegaleMapper = new ReferenceLegaleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReferenceLegaleSample1();
        var actual = referenceLegaleMapper.toEntity(referenceLegaleMapper.toDto(expected));
        assertReferenceLegaleAllPropertiesEquals(expected, actual);
    }
}
