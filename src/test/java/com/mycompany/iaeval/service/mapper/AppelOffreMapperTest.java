package com.mycompany.iaeval.service.mapper;

import static com.mycompany.iaeval.domain.AppelOffreAsserts.*;
import static com.mycompany.iaeval.domain.AppelOffreTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppelOffreMapperTest {

    private AppelOffreMapper appelOffreMapper;

    @BeforeEach
    void setUp() {
        appelOffreMapper = new AppelOffreMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAppelOffreSample1();
        var actual = appelOffreMapper.toEntity(appelOffreMapper.toDto(expected));
        assertAppelOffreAllPropertiesEquals(expected, actual);
    }
}
