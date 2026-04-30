package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.DocumentJoint;
import com.mycompany.iaeval.domain.Soumission;
import com.mycompany.iaeval.service.dto.DocumentJointDTO;
import com.mycompany.iaeval.service.dto.SoumissionDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentJointMapper extends EntityMapper<DocumentJointDTO, DocumentJoint> {
    @Override
    @Mapping(target = "soumission", source = "soumission", qualifiedByName = "soumissionIdSeul")
    DocumentJointDTO toDto(DocumentJoint s);

    @Override
    @Mapping(target = "soumission", ignore = true)
    DocumentJoint toEntity(DocumentJointDTO dto);

    // FIX POUR LES ERREURS MAVEN : On ignore la relation lors de la mise à jour partielle
    @Override
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "soumission", ignore = true)
    void partialUpdate(@MappingTarget DocumentJoint entity, DocumentJointDTO dto);

    @Named("soumissionIdSeul")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SoumissionDTO toDtoSoumissionId(Soumission soumission);
}
