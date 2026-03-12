package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.DocumentJoint;
import com.mycompany.iaeval.domain.Soumission;
import com.mycompany.iaeval.service.dto.DocumentJointDTO;
import com.mycompany.iaeval.service.dto.SoumissionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DocumentJoint} and its DTO {@link DocumentJointDTO}.
 */
@Mapper(componentModel = "spring")
public interface DocumentJointMapper extends EntityMapper<DocumentJointDTO, DocumentJoint> {
    @Mapping(target = "soumission", source = "soumission", qualifiedByName = "soumissionId")
    DocumentJointDTO toDto(DocumentJoint s);

    @Named("soumissionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SoumissionDTO toDtoSoumissionId(Soumission soumission);
}
