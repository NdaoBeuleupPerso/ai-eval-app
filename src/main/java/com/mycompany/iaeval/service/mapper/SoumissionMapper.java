package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.domain.Candidat;
import com.mycompany.iaeval.domain.Evaluation;
import com.mycompany.iaeval.domain.Soumission;
import com.mycompany.iaeval.service.dto.AppelOffreDTO;
import com.mycompany.iaeval.service.dto.CandidatDTO;
import com.mycompany.iaeval.service.dto.EvaluationDTO;
import com.mycompany.iaeval.service.dto.SoumissionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Soumission} and its DTO {@link SoumissionDTO}.
 */
@Mapper(componentModel = "spring")
public interface SoumissionMapper extends EntityMapper<SoumissionDTO, Soumission> {
    @Mapping(target = "evaluation", source = "evaluation", qualifiedByName = "evaluationId")
    @Mapping(target = "appelOffre", source = "appelOffre", qualifiedByName = "appelOffreReference")
    @Mapping(target = "candidat", source = "candidat", qualifiedByName = "candidatNom")
    SoumissionDTO toDto(Soumission s);

    @Named("evaluationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EvaluationDTO toDtoEvaluationId(Evaluation evaluation);

    @Named("appelOffreReference")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "reference", source = "reference")
    AppelOffreDTO toDtoAppelOffreReference(AppelOffre appelOffre);

    @Named("candidatNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    CandidatDTO toDtoCandidatNom(Candidat candidat);
}
