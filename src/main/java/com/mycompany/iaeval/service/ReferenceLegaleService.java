package com.mycompany.iaeval.service;

import com.mycompany.iaeval.domain.ReferenceLegale;
import com.mycompany.iaeval.repository.ReferenceLegaleRepository;
import com.mycompany.iaeval.service.dto.ReferenceLegaleDTO;
import com.mycompany.iaeval.service.mapper.ReferenceLegaleMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.iaeval.domain.ReferenceLegale}.
 */
@Service
@Transactional
public class ReferenceLegaleService {

    private static final Logger LOG = LoggerFactory.getLogger(ReferenceLegaleService.class);

    private final ReferenceLegaleRepository referenceLegaleRepository;

    private final ReferenceLegaleMapper referenceLegaleMapper;

    public ReferenceLegaleService(ReferenceLegaleRepository referenceLegaleRepository, ReferenceLegaleMapper referenceLegaleMapper) {
        this.referenceLegaleRepository = referenceLegaleRepository;
        this.referenceLegaleMapper = referenceLegaleMapper;
    }

    /**
     * Save a referenceLegale.
     *
     * @param referenceLegaleDTO the entity to save.
     * @return the persisted entity.
     */
    public ReferenceLegaleDTO save(ReferenceLegaleDTO referenceLegaleDTO) {
        LOG.debug("Request to save ReferenceLegale : {}", referenceLegaleDTO);
        ReferenceLegale referenceLegale = referenceLegaleMapper.toEntity(referenceLegaleDTO);
        referenceLegale = referenceLegaleRepository.save(referenceLegale);
        return referenceLegaleMapper.toDto(referenceLegale);
    }

    /**
     * Update a referenceLegale.
     *
     * @param referenceLegaleDTO the entity to save.
     * @return the persisted entity.
     */
    public ReferenceLegaleDTO update(ReferenceLegaleDTO referenceLegaleDTO) {
        LOG.debug("Request to update ReferenceLegale : {}", referenceLegaleDTO);
        ReferenceLegale referenceLegale = referenceLegaleMapper.toEntity(referenceLegaleDTO);
        referenceLegale = referenceLegaleRepository.save(referenceLegale);
        return referenceLegaleMapper.toDto(referenceLegale);
    }

    /**
     * Partially update a referenceLegale.
     *
     * @param referenceLegaleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ReferenceLegaleDTO> partialUpdate(ReferenceLegaleDTO referenceLegaleDTO) {
        LOG.debug("Request to partially update ReferenceLegale : {}", referenceLegaleDTO);

        return referenceLegaleRepository
            .findById(referenceLegaleDTO.getId())
            .map(existingReferenceLegale -> {
                referenceLegaleMapper.partialUpdate(existingReferenceLegale, referenceLegaleDTO);

                return existingReferenceLegale;
            })
            .map(referenceLegaleRepository::save)
            .map(referenceLegaleMapper::toDto);
    }

    /**
     * Get all the referenceLegales.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ReferenceLegaleDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ReferenceLegales");
        return referenceLegaleRepository.findAll(pageable).map(referenceLegaleMapper::toDto);
    }

    /**
     * Get one referenceLegale by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ReferenceLegaleDTO> findOne(Long id) {
        LOG.debug("Request to get ReferenceLegale : {}", id);
        return referenceLegaleRepository.findById(id).map(referenceLegaleMapper::toDto);
    }

    /**
     * Delete the referenceLegale by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ReferenceLegale : {}", id);
        referenceLegaleRepository.deleteById(id);
    }
}
