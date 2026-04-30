package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.domain.Candidat;
import com.mycompany.iaeval.domain.Evaluation;
import com.mycompany.iaeval.domain.Soumission;
import com.mycompany.iaeval.domain.User;
import com.mycompany.iaeval.service.dto.AppelOffreDTO;
import com.mycompany.iaeval.service.dto.CandidatDTO;
import com.mycompany.iaeval.service.dto.EvaluationDTO;
import com.mycompany.iaeval.service.dto.SoumissionDTO;
import com.mycompany.iaeval.service.dto.UserDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EvaluationMapper extends EntityMapper<EvaluationDTO, Evaluation> {
    @Override
    @Mapping(target = "evaluateur", source = "evaluateur", qualifiedByName = "userLogin")
    @Mapping(target = "soumission", source = "soumission", qualifiedByName = "soumissionCandidat")
    EvaluationDTO toDto(Evaluation s);

    @Override
    @Mapping(target = "evaluateur", source = "evaluateur", qualifiedByName = "userEntity")
    @Mapping(target = "soumission", source = "soumission", qualifiedByName = "soumissionEntity")
    Evaluation toEntity(EvaluationDTO dto);

    // --- FIX POUR LES 11 ERREURS (partialUpdate) ---
    @Override
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "soumission", ignore = true) // On ignore la relation pour éviter la descente
    // vers description
    @Mapping(target = "evaluateur", ignore = true)
    void partialUpdate(@MappingTarget Evaluation entity, EvaluationDTO dto);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("soumissionCandidat")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "candidat", source = "candidat", qualifiedByName = "candidatNom")
    @Mapping(target = "appelOffre", source = "appelOffre", qualifiedByName = "appelOffreId")
    SoumissionDTO toDtoSoumissionCandidat(Soumission soumission);

    @Named("appelOffreId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titre", source = "titre")
    AppelOffreDTO toDtoAppelOffreId(AppelOffre appelOffre);

    @Named("candidatNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    CandidatDTO toDtoCandidatNom(Candidat candidat);

    @Named("userEntity")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    User toEntityUser(UserDTO dto);

    @Named("soumissionEntity")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Soumission toEntitySoumission(SoumissionDTO dto);
}
