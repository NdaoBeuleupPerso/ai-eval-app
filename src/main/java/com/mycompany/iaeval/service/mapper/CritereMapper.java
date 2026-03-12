package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.domain.Critere;
import com.mycompany.iaeval.service.dto.AppelOffreDTO;
import com.mycompany.iaeval.service.dto.CritereDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Critere} and its DTO {@link CritereDTO}.
 */
@Mapper(componentModel = "spring")
public interface CritereMapper extends EntityMapper<CritereDTO, Critere> {
    @Mapping(target = "appelOffre", source = "appelOffre", qualifiedByName = "appelOffreReference")
    CritereDTO toDto(Critere s);

    @Named("appelOffreReference")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "reference", source = "reference")
    AppelOffreDTO toDtoAppelOffreReference(AppelOffre appelOffre);
}
