package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.Evaluation;
import com.mycompany.iaeval.domain.TraceAudit;
import com.mycompany.iaeval.service.dto.EvaluationDTO;
import com.mycompany.iaeval.service.dto.TraceAuditDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy; // Import à ajouter

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TraceAuditMapper extends EntityMapper<TraceAuditDTO, TraceAudit> {
    @Mapping(target = "evaluation", source = "evaluation", qualifiedByName = "evaluationId")
    TraceAuditDTO toDto(TraceAudit s);

    @Named("evaluationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EvaluationDTO toDtoEvaluationId(Evaluation evaluation);
}
