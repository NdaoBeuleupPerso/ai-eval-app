package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.Candidat;
import com.mycompany.iaeval.service.dto.CandidatDTO;
// Import à ajouter
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy; // Import à ajouter

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CandidatMapper extends EntityMapper<CandidatDTO, Candidat> {}
