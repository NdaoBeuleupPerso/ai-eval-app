package com.mycompany.iaeval.service;

import com.mycompany.iaeval.domain.*; // for static metamodels
import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.repository.AppelOffreRepository;
import com.mycompany.iaeval.service.criteria.AppelOffreCriteria;
import com.mycompany.iaeval.service.dto.AppelOffreDTO;
import com.mycompany.iaeval.service.mapper.AppelOffreMapper;
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
 * Service for executing complex queries for {@link AppelOffre} entities in the database.
 * The main input is a {@link AppelOffreCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AppelOffreDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AppelOffreQueryService extends QueryService<AppelOffre> {

    private static final Logger LOG = LoggerFactory.getLogger(AppelOffreQueryService.class);

    private final AppelOffreRepository appelOffreRepository;

    private final AppelOffreMapper appelOffreMapper;

    public AppelOffreQueryService(AppelOffreRepository appelOffreRepository, AppelOffreMapper appelOffreMapper) {
        this.appelOffreRepository = appelOffreRepository;
        this.appelOffreMapper = appelOffreMapper;
    }

    /**
     * Return a {@link Page} of {@link AppelOffreDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AppelOffreDTO> findByCriteria(AppelOffreCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AppelOffre> specification = createSpecification(criteria);
        return appelOffreRepository.findAll(specification, page).map(appelOffreMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AppelOffreCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AppelOffre> specification = createSpecification(criteria);
        return appelOffreRepository.count(specification);
    }

    /**
     * Function to convert {@link AppelOffreCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AppelOffre> createSpecification(AppelOffreCriteria criteria) {
        Specification<AppelOffre> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), AppelOffre_.id),
                buildStringSpecification(criteria.getReference(), AppelOffre_.reference),
                buildStringSpecification(criteria.getTitre(), AppelOffre_.titre),
                buildRangeSpecification(criteria.getDateCloture(), AppelOffre_.dateCloture),
                buildSpecification(criteria.getStatut(), AppelOffre_.statut),
                buildSpecification(criteria.getCriteresId(), root -> root.join(AppelOffre_.criteres, JoinType.LEFT).get(Critere_.id)),
                buildSpecification(criteria.getSoumissionsId(), root ->
                    root.join(AppelOffre_.soumissions, JoinType.LEFT).get(Soumission_.id)
                )
            );
        }
        return specification;
    }
}
