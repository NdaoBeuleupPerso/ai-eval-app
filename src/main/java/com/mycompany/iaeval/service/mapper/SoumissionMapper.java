package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.domain.Candidat;
import com.mycompany.iaeval.domain.Evaluation;
import com.mycompany.iaeval.domain.Soumission;
import com.mycompany.iaeval.service.dto.AppelOffreDTO;
import com.mycompany.iaeval.service.dto.CandidatDTO;
import com.mycompany.iaeval.service.dto.EvaluationDTO;
import com.mycompany.iaeval.service.dto.SoumissionDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
/**
 * Mapper for the entity {@link Soumission} and its DTO {@link SoumissionDTO}.
 */
import org.mapstruct.ReportingPolicy; // Import à ajouter

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { DocumentJointMapper.class })
public interface SoumissionMapper extends EntityMapper<SoumissionDTO, Soumission> {
    /*@Mapping(target = "evaluation", source = "evaluation", qualifiedByName = "evaluationId")
    @Mapping(target = "appelOffre", source = "appelOffre", qualifiedByName = "appelOffreReference")
    @Mapping(target = "candidat", source = "candidat", qualifiedByName = "candidatNom")*/

    @Mapping(target = "evaluation", ignore = true)
    @Mapping(target = "appelOffre", ignore = true)
    @Mapping(target = "candidat", ignore = true)
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
