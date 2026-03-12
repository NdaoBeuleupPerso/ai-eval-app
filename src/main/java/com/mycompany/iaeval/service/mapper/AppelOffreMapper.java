package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.service.dto.AppelOffreDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppelOffre} and its DTO {@link AppelOffreDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppelOffreMapper extends EntityMapper<AppelOffreDTO, AppelOffre> {}
