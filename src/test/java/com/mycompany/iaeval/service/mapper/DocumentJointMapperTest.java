package com.mycompany.iaeval.service.mapper;

import static com.mycompany.iaeval.domain.DocumentJointAsserts.*;
import static com.mycompany.iaeval.domain.DocumentJointTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DocumentJointMapperTest {

    private DocumentJointMapper documentJointMapper;

    @BeforeEach
    void setUp() {
        documentJointMapper = new DocumentJointMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDocumentJointSample1();
        var actual = documentJointMapper.toEntity(documentJointMapper.toDto(expected));
        assertDocumentJointAllPropertiesEquals(expected, actual);
    }
}
