package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.*;
import com.mycompany.iaeval.service.dto.*;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Evaluation} and its DTO {@link EvaluationDTO}.
 */
@Mapper(componentModel = "spring")
public interface EvaluationMapper extends EntityMapper<EvaluationDTO, Evaluation> {
    @Mapping(target = "evaluateur", source = "evaluateur", qualifiedByName = "userLogin")
    @Mapping(target = "soumission", source = "soumission", qualifiedByName = "soumissionCandidat")
    EvaluationDTO toDto(Evaluation s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    // --- FIX POUR LA VUE DE RÉVISION ET LA BOUCLE INFINIE ---

    @Named("soumissionCandidat")
    @BeanMapping(ignoreByDefault = true) // <--- CRUCIAL : ignore "evaluation" pour stopper la boucle infinie
    @Mapping(target = "id", source = "id")
    @Mapping(target = "candidat", source = "candidat", qualifiedByName = "candidatNom")
    @Mapping(target = "appelOffre", source = "appelOffre", qualifiedByName = "appelOffreId")
    SoumissionDTO toDtoSoumissionCandidat(Soumission soumission);

    // --- CETTE MÉTHODE MANQUAIT ET CAUSAIT L'ERREUR ---
    @Named("appelOffreId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppelOffreDTO toDtoAppelOffreId(AppelOffre appelOffre);

    @Named("candidatNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    CandidatDTO toDtoCandidatNom(Candidat candidat);
}
