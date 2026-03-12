package com.mycompany.iaeval.service;

import com.mycompany.iaeval.domain.Critere;
import com.mycompany.iaeval.repository.CritereRepository;
import com.mycompany.iaeval.service.dto.CritereDTO;
import com.mycompany.iaeval.service.mapper.CritereMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.iaeval.domain.Critere}.
 */
@Service
@Transactional
public class CritereService {

    private static final Logger LOG = LoggerFactory.getLogger(CritereService.class);

    private final CritereRepository critereRepository;

    private final CritereMapper critereMapper;

    public CritereService(CritereRepository critereRepository, CritereMapper critereMapper) {
        this.critereRepository = critereRepository;
        this.critereMapper = critereMapper;
    }

    /**
     * Save a critere.
     *
     * @param critereDTO the entity to save.
     * @return the persisted entity.
     */
    public CritereDTO save(CritereDTO critereDTO) {
        LOG.debug("Request to save Critere : {}", critereDTO);
        Critere critere = critereMapper.toEntity(critereDTO);
        critere = critereRepository.save(critere);
        return critereMapper.toDto(critere);
    }

    /**
     * Update a critere.
     *
     * @param critereDTO the entity to save.
     * @return the persisted entity.
     */
    public CritereDTO update(CritereDTO critereDTO) {
        LOG.debug("Request to update Critere : {}", critereDTO);
        Critere critere = critereMapper.toEntity(critereDTO);
        critere = critereRepository.save(critere);
        return critereMapper.toDto(critere);
    }

    /**
     * Partially update a critere.
     *
     * @param critereDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CritereDTO> partialUpdate(CritereDTO critereDTO) {
        LOG.debug("Request to partially update Critere : {}", critereDTO);

        return critereRepository
            .findById(critereDTO.getId())
            .map(existingCritere -> {
                critereMapper.partialUpdate(existingCritere, critereDTO);

                return existingCritere;
            })
            .map(critereRepository::save)
            .map(critereMapper::toDto);
    }

    /**
     * Get all the criteres.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CritereDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Criteres");
        return critereRepository.findAll(pageable).map(critereMapper::toDto);
    }

    /**
     * Get all the criteres with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CritereDTO> findAllWithEagerRelationships(Pageable pageable) {
        return critereRepository.findAllWithEagerRelationships(pageable).map(critereMapper::toDto);
    }

    /**
     * Get one critere by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CritereDTO> findOne(Long id) {
        LOG.debug("Request to get Critere : {}", id);
        return critereRepository.findOneWithEagerRelationships(id).map(critereMapper::toDto);
    }

    /**
     * Delete the critere by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Critere : {}", id);
        critereRepository.deleteById(id);
    }
}
