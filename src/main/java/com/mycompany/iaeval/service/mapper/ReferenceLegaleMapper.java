package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.ReferenceLegale;
import com.mycompany.iaeval.service.dto.ReferenceLegaleDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for the entity {@link ReferenceLegale} and its DTO {@link ReferenceLegaleDTO}.
 */

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReferenceLegaleMapper extends EntityMapper<ReferenceLegaleDTO, ReferenceLegale> {
    @Override
    @Mapping(source = "document", target = "fichierTemporaire") // byte[] DTO -> Transient Entity
    @Mapping(target = "document", ignore = true) // Ignore la relation DocumentJoint
    ReferenceLegale toEntity(ReferenceLegaleDTO dto);

    @Override
    @Mapping(target = "document", ignore = true) // Ne renvoie pas le binaire au client
    ReferenceLegaleDTO toDto(ReferenceLegale entity);

    @Override
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(source = "document", target = "fichierTemporaire")
    @Mapping(target = "document", ignore = true)
    void partialUpdate(@MappingTarget ReferenceLegale entity, ReferenceLegaleDTO dto);
}
