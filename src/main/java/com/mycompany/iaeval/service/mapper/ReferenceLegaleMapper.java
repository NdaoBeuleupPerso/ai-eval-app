package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.ReferenceLegale;
import com.mycompany.iaeval.service.dto.ReferenceLegaleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ReferenceLegale} and its DTO {@link ReferenceLegaleDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReferenceLegaleMapper extends EntityMapper<ReferenceLegaleDTO, ReferenceLegale> {}
