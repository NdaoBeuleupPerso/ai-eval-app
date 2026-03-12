package com.mycompany.iaeval.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.iaeval.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DocumentJointDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DocumentJointDTO.class);
        DocumentJointDTO documentJointDTO1 = new DocumentJointDTO();
        documentJointDTO1.setId(1L);
        DocumentJointDTO documentJointDTO2 = new DocumentJointDTO();
        assertThat(documentJointDTO1).isNotEqualTo(documentJointDTO2);
        documentJointDTO2.setId(documentJointDTO1.getId());
        assertThat(documentJointDTO1).isEqualTo(documentJointDTO2);
        documentJointDTO2.setId(2L);
        assertThat(documentJointDTO1).isNotEqualTo(documentJointDTO2);
        documentJointDTO1.setId(null);
        assertThat(documentJointDTO1).isNotEqualTo(documentJointDTO2);
    }
}
