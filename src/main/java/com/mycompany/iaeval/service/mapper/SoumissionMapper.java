package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.domain.Soumission;
import com.mycompany.iaeval.service.dto.AppelOffreDTO;
import com.mycompany.iaeval.service.dto.SoumissionDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { DocumentJointMapper.class })
public interface SoumissionMapper extends EntityMapper<SoumissionDTO, Soumission> {
    @Override
    @Mapping(target = "appelOffre", source = "appelOffre", qualifiedByName = "appelOffreIdSeul")
    SoumissionDTO toDto(Soumission s);

    @Override
    @Mapping(target = "appelOffre", source = "appelOffre", qualifiedByName = "appelOffreEntityId")
    Soumission toEntity(SoumissionDTO dto);

    @Named("appelOffreIdSeul")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "reference", source = "reference")
    AppelOffreDTO toDtoId(AppelOffre ao);

    @Named("appelOffreEntityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppelOffre toEntityId(AppelOffreDTO dto);

    @Override
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "appelOffre", ignore = true) // Sécurité pour partialUpdate
    void partialUpdate(@MappingTarget Soumission entity, SoumissionDTO dto);
}
