package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.Candidat;
import com.mycompany.iaeval.service.dto.CandidatDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Candidat} and its DTO {@link CandidatDTO}.
 */
@Mapper(componentModel = "spring")
public interface CandidatMapper extends EntityMapper<CandidatDTO, Candidat> {}
