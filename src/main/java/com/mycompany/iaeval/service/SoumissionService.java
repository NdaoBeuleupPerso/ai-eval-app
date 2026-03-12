package com.mycompany.iaeval.service;

import com.mycompany.iaeval.domain.Soumission;
import com.mycompany.iaeval.repository.SoumissionRepository;
import com.mycompany.iaeval.service.dto.SoumissionDTO;
import com.mycompany.iaeval.service.mapper.SoumissionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.iaeval.domain.Soumission}.
 */
@Service
@Transactional
public class SoumissionService {

    private static final Logger LOG = LoggerFactory.getLogger(SoumissionService.class);

    private final SoumissionRepository soumissionRepository;

    private final SoumissionMapper soumissionMapper;

    public SoumissionService(SoumissionRepository soumissionRepository, SoumissionMapper soumissionMapper) {
        this.soumissionRepository = soumissionRepository;
        this.soumissionMapper = soumissionMapper;
    }

    /**
     * Save a soumission.
     *
     * @param soumissionDTO the entity to save.
     * @return the persisted entity.
     */
    public SoumissionDTO save(SoumissionDTO soumissionDTO) {
        LOG.debug("Request to save Soumission : {}", soumissionDTO);
        Soumission soumission = soumissionMapper.toEntity(soumissionDTO);
        soumission = soumissionRepository.save(soumission);
        return soumissionMapper.toDto(soumission);
    }

    /**
     * Update a soumission.
     *
     * @param soumissionDTO the entity to save.
     * @return the persisted entity.
     */
    public SoumissionDTO update(SoumissionDTO soumissionDTO) {
        LOG.debug("Request to update Soumission : {}", soumissionDTO);
        Soumission soumission = soumissionMapper.toEntity(soumissionDTO);
        soumission = soumissionRepository.save(soumission);
        return soumissionMapper.toDto(soumission);
    }

    /**
     * Partially update a soumission.
     *
     * @param soumissionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SoumissionDTO> partialUpdate(SoumissionDTO soumissionDTO) {
        LOG.debug("Request to partially update Soumission : {}", soumissionDTO);

        return soumissionRepository
            .findById(soumissionDTO.getId())
            .map(existingSoumission -> {
                soumissionMapper.partialUpdate(existingSoumission, soumissionDTO);

                return existingSoumission;
            })
            .map(soumissionRepository::save)
            .map(soumissionMapper::toDto);
    }

    /**
     * Get all the soumissions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<SoumissionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return soumissionRepository.findAllWithEagerRelationships(pageable).map(soumissionMapper::toDto);
    }

    /**
     * Get one soumission by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SoumissionDTO> findOne(Long id) {
        LOG.debug("Request to get Soumission : {}", id);
        return soumissionRepository.findOneWithEagerRelationships(id).map(soumissionMapper::toDto);
    }

    /**
     * Delete the soumission by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Soumission : {}", id);
        soumissionRepository.deleteById(id);
    }
}
