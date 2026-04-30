package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.Evaluation;
import com.mycompany.iaeval.domain.TraceAudit;
import com.mycompany.iaeval.service.dto.EvaluationDTO;
import com.mycompany.iaeval.service.dto.TraceAuditDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TraceAuditMapper extends EntityMapper<TraceAuditDTO, TraceAudit> {
    @Override
    @Mapping(target = "evaluation", source = "evaluation", qualifiedByName = "evaluationStrict")
    TraceAuditDTO toDto(TraceAudit s);

    @Override
    @Mapping(target = "evaluation", source = "evaluation", qualifiedByName = "evaluationEntityId")
    TraceAudit toEntity(TraceAuditDTO dto);

    // FIX POUR LES ERREURS MAVEN : On ignore la relation lors de la mise à jour partielle
    @Override
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "evaluation", ignore = true)
    void partialUpdate(@MappingTarget TraceAudit entity, TraceAuditDTO dto);

    @Named("evaluationStrict")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EvaluationDTO toDtoEvaluationId(Evaluation evaluation);

    @Named("evaluationEntityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Evaluation toEntityEvaluationId(EvaluationDTO evaluationDTO);
}
