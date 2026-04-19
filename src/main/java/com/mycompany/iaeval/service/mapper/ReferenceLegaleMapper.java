package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.ReferenceLegale;
import com.mycompany.iaeval.service.dto.ReferenceLegaleDTO;
import org.mapstruct.Mapper;
/**
 * Mapper for the entity {@link ReferenceLegale} and its DTO {@link ReferenceLegaleDTO}.
 */
// Import à ajouter
import org.mapstruct.ReportingPolicy; // Import à ajouter

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReferenceLegaleMapper extends EntityMapper<ReferenceLegaleDTO, ReferenceLegale> {}
