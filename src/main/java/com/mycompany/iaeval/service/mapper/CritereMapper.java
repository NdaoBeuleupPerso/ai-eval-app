package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.domain.Critere;
import com.mycompany.iaeval.service.dto.AppelOffreDTO;
import com.mycompany.iaeval.service.dto.CritereDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CritereMapper extends EntityMapper<CritereDTO, Critere> {
    @Override
    @Mapping(target = "appelOffre", source = "appelOffre", qualifiedByName = "appelOffreReference")
    CritereDTO toDto(Critere s);

    @Override
    @Mapping(target = "appelOffre", source = "appelOffre", qualifiedByName = "appelOffreEntityId")
    Critere toEntity(CritereDTO dto);

    // FIX POUR LES ERREURS MAVEN : On ignore la relation lors de la mise à jour partielle
    @Override
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "appelOffre", ignore = true)
    void partialUpdate(@MappingTarget Critere entity, CritereDTO dto);

    @Named("appelOffreReference")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "reference", source = "reference")
    AppelOffreDTO toDtoAppelOffreReference(AppelOffre appelOffre);

    @Named("appelOffreEntityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppelOffre toEntityAppelOffreId(AppelOffreDTO dto);
}
