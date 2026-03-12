package com.mycompany.iaeval.service;

import com.mycompany.iaeval.domain.*; // for static metamodels
import com.mycompany.iaeval.domain.TraceAudit;
import com.mycompany.iaeval.repository.TraceAuditRepository;
import com.mycompany.iaeval.service.criteria.TraceAuditCriteria;
import com.mycompany.iaeval.service.dto.TraceAuditDTO;
import com.mycompany.iaeval.service.mapper.TraceAuditMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TraceAudit} entities in the database.
 * The main input is a {@link TraceAuditCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TraceAuditDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TraceAuditQueryService extends QueryService<TraceAudit> {

    private static final Logger LOG = LoggerFactory.getLogger(TraceAuditQueryService.class);

    private final TraceAuditRepository traceAuditRepository;

    private final TraceAuditMapper traceAuditMapper;

    public TraceAuditQueryService(TraceAuditRepository traceAuditRepository, TraceAuditMapper traceAuditMapper) {
        this.traceAuditRepository = traceAuditRepository;
        this.traceAuditMapper = traceAuditMapper;
    }

    /**
     * Return a {@link Page} of {@link TraceAuditDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TraceAuditDTO> findByCriteria(TraceAuditCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TraceAudit> specification = createSpecification(criteria);
        return traceAuditRepository.findAll(specification, page).map(traceAuditMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TraceAuditCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TraceAudit> specification = createSpecification(criteria);
        return traceAuditRepository.count(specification);
    }

    /**
     * Function to convert {@link TraceAuditCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TraceAudit> createSpecification(TraceAuditCriteria criteria) {
        Specification<TraceAudit> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), TraceAudit_.id),
                buildStringSpecification(criteria.getAction(), TraceAudit_.action),
                buildRangeSpecification(criteria.getHorodatage(), TraceAudit_.horodatage),
                buildStringSpecification(criteria.getIdentifiantUtilisateur(), TraceAudit_.identifiantUtilisateur),
                buildSpecification(criteria.getEvaluationId(), root -> root.join(TraceAudit_.evaluation, JoinType.LEFT).get(Evaluation_.id))
            );
        }
        return specification;
    }
}
