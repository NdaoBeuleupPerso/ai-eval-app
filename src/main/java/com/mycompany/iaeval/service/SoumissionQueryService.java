package com.mycompany.iaeval.service;

import com.mycompany.iaeval.domain.*; // for static metamodels
import com.mycompany.iaeval.domain.Soumission;
import com.mycompany.iaeval.repository.SoumissionRepository;
import com.mycompany.iaeval.service.criteria.SoumissionCriteria;
import com.mycompany.iaeval.service.dto.SoumissionDTO;
import com.mycompany.iaeval.service.mapper.SoumissionMapper;
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
 * Service for executing complex queries for {@link Soumission} entities in the database.
 * The main input is a {@link SoumissionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SoumissionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SoumissionQueryService extends QueryService<Soumission> {

    private static final Logger LOG = LoggerFactory.getLogger(SoumissionQueryService.class);

    private final SoumissionRepository soumissionRepository;

    private final SoumissionMapper soumissionMapper;

    public SoumissionQueryService(SoumissionRepository soumissionRepository, SoumissionMapper soumissionMapper) {
        this.soumissionRepository = soumissionRepository;
        this.soumissionMapper = soumissionMapper;
    }

    /**
     * Return a {@link Page} of {@link SoumissionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SoumissionDTO> findByCriteria(SoumissionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Soumission> specification = createSpecification(criteria);
        return soumissionRepository.findAll(specification, page).map(soumissionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SoumissionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Soumission> specification = createSpecification(criteria);
        return soumissionRepository.count(specification);
    }

    /**
     * Function to convert {@link SoumissionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Soumission> createSpecification(SoumissionCriteria criteria) {
        Specification<Soumission> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Soumission_.id),
                buildRangeSpecification(criteria.getDateSoumission(), Soumission_.dateSoumission),
                buildSpecification(criteria.getStatut(), Soumission_.statut),
                buildSpecification(criteria.getEvaluationId(), root -> root.join(Soumission_.evaluation, JoinType.LEFT).get(Evaluation_.id)
                ),
                buildSpecification(criteria.getDocumentsId(), root -> root.join(Soumission_.documents, JoinType.LEFT).get(DocumentJoint_.id)
                ),
                buildSpecification(criteria.getAppelOffreId(), root -> root.join(Soumission_.appelOffre, JoinType.LEFT).get(AppelOffre_.id)
                ),
                buildSpecification(criteria.getCandidatId(), root -> root.join(Soumission_.candidat, JoinType.LEFT).get(Candidat_.id))
            );
        }
        return specification;
    }
}
