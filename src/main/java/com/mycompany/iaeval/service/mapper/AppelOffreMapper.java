package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.service.dto.AppelOffreDTO;
import org.mapstruct.Mapper;
/**
 * Mapper for the entity {@link AppelOffre} and its DTO {@link AppelOffreDTO}.
 */
import org.mapstruct.ReportingPolicy; // Import à ajouter

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppelOffreMapper extends EntityMapper<AppelOffreDTO, AppelOffre> {}
