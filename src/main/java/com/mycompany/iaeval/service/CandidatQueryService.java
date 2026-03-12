package com.mycompany.iaeval.service;

import com.mycompany.iaeval.domain.*; // for static metamodels
import com.mycompany.iaeval.domain.Candidat;
import com.mycompany.iaeval.repository.CandidatRepository;
import com.mycompany.iaeval.service.criteria.CandidatCriteria;
import com.mycompany.iaeval.service.dto.CandidatDTO;
import com.mycompany.iaeval.service.mapper.CandidatMapper;
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
 * Service for executing complex queries for {@link Candidat} entities in the database.
 * The main input is a {@link CandidatCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CandidatDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CandidatQueryService extends QueryService<Candidat> {

    private static final Logger LOG = LoggerFactory.getLogger(CandidatQueryService.class);

    private final CandidatRepository candidatRepository;

    private final CandidatMapper candidatMapper;

    public CandidatQueryService(CandidatRepository candidatRepository, CandidatMapper candidatMapper) {
        this.candidatRepository = candidatRepository;
        this.candidatMapper = candidatMapper;
    }

    /**
     * Return a {@link Page} of {@link CandidatDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CandidatDTO> findByCriteria(CandidatCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Candidat> specification = createSpecification(criteria);
        return candidatRepository.findAll(specification, page).map(candidatMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CandidatCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Candidat> specification = createSpecification(criteria);
        return candidatRepository.count(specification);
    }

    /**
     * Function to convert {@link CandidatCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Candidat> createSpecification(CandidatCriteria criteria) {
        Specification<Candidat> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Candidat_.id),
                buildStringSpecification(criteria.getNom(), Candidat_.nom),
                buildStringSpecification(criteria.getSiret(), Candidat_.siret),
                buildStringSpecification(criteria.getEmail(), Candidat_.email),
                buildSpecification(criteria.getSoumissionsId(), root -> root.join(Candidat_.soumissions, JoinType.LEFT).get(Soumission_.id))
            );
        }
        return specification;
    }
}
