package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.domain.Candidat;
import com.mycompany.iaeval.domain.EvaluationCandidat;
import com.mycompany.iaeval.domain.Soumission;
import com.mycompany.iaeval.service.dto.AppelOffreDTO;
import com.mycompany.iaeval.service.dto.CandidatDTO;
import com.mycompany.iaeval.service.dto.EvaluationCandidatDTO;
import com.mycompany.iaeval.service.dto.SoumissionDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EvaluationCandidatMapper extends EntityMapper<EvaluationCandidatDTO, EvaluationCandidat> {
    @Override
    @Mapping(target = "soumission", source = "soumission", qualifiedByName = "soumissionCandidat")
    EvaluationCandidatDTO toDto(EvaluationCandidat s);

    @Override
    @Mapping(target = "soumission", source = "soumission", qualifiedByName = "soumissionEntityId")
    EvaluationCandidat toEntity(EvaluationCandidatDTO dto);

    // --- FIX POUR LES 11 ERREURS (partialUpdate) ---
    @Override
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "soumission", ignore = true)
    void partialUpdate(@MappingTarget EvaluationCandidat entity, EvaluationCandidatDTO dto);

    @Named("soumissionCandidat")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "candidat", source = "candidat", qualifiedByName = "candidatNom")
    @Mapping(target = "appelOffre", source = "appelOffre", qualifiedByName = "appelOffreTitre")
    SoumissionDTO toDtoSoumissionCandidat(Soumission soumission);

    @Named("appelOffreTitre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titre", source = "titre")
    AppelOffreDTO toDtoAppelOffreTitre(AppelOffre appelOffre);

    @Named("candidatNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    CandidatDTO toDtoCandidatNom(Candidat candidat);

    @Named("soumissionEntityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Soumission toEntitySoumissionId(SoumissionDTO dto);
}
