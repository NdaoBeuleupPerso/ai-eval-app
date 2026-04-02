package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.*;
import com.mycompany.iaeval.service.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Evaluation} and its DTO {@link EvaluationDTO}.
 */
@Mapper(componentModel = "spring")
public interface EvaluationCandidatMapper extends EntityMapper<EvaluationCandidatDTO, EvaluationCandidat> {
    @Mapping(target = "evaluateur", source = "evaluateur", qualifiedByName = "userLogin")
    @Mapping(target = "soumission", source = "soumission", qualifiedByName = "soumissionCandidat")
    EvaluationCandidatDTO toDto(Evaluation s);
    //    @Named("soumissionCandidat")
    //    @BeanMapping(ignoreByDefault = true) // <--- CRUCIAL : ignore "evaluation" pour stopper la boucle infinie
    //    @Mapping(target = "id", source = "id")
    //    @Mapping(target = "candidat", source = "candidat", qualifiedByName = "candidatNom")
    //    @Mapping(target = "appelOffre", source = "appelOffre", qualifiedByName = "appelOffreId")
    //    SoumissionDTO toDtoSoumissionCandidat(Soumission soumission);
    //
    //    // --- CETTE MÉTHODE MANQUAIT ET CAUSAIT L'ERREUR ---
    //    @Named("appelOffreId")
    //    @BeanMapping(ignoreByDefault = true)
    //    @Mapping(target = "id", source = "id")
    //    AppelOffreDTO toDtoAppelOffreId(AppelOffre appelOffre);
    //
    //    @Named("candidatNom")
    //    @BeanMapping(ignoreByDefault = true)
    //    @Mapping(target = "id", source = "id")
    //    @Mapping(target = "nom", source = "nom")
    //    CandidatDTO toDtoCandidatNom(Candidat candidat);
}
