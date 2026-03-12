package com.mycompany.iaeval.service;

import com.mycompany.iaeval.domain.TraceAudit;
import com.mycompany.iaeval.repository.TraceAuditRepository;
import com.mycompany.iaeval.service.dto.TraceAuditDTO;
import com.mycompany.iaeval.service.mapper.TraceAuditMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.iaeval.domain.TraceAudit}.
 */
@Service
@Transactional
public class TraceAuditService {

    private static final Logger LOG = LoggerFactory.getLogger(TraceAuditService.class);

    private final TraceAuditRepository traceAuditRepository;

    private final TraceAuditMapper traceAuditMapper;

    public TraceAuditService(TraceAuditRepository traceAuditRepository, TraceAuditMapper traceAuditMapper) {
        this.traceAuditRepository = traceAuditRepository;
        this.traceAuditMapper = traceAuditMapper;
    }

    /**
     * Save a traceAudit.
     *
     * @param traceAuditDTO the entity to save.
     * @return the persisted entity.
     */
    public TraceAuditDTO save(TraceAuditDTO traceAuditDTO) {
        LOG.debug("Request to save TraceAudit : {}", traceAuditDTO);
        TraceAudit traceAudit = traceAuditMapper.toEntity(traceAuditDTO);
        traceAudit = traceAuditRepository.save(traceAudit);
        return traceAuditMapper.toDto(traceAudit);
    }

    /**
     * Update a traceAudit.
     *
     * @param traceAuditDTO the entity to save.
     * @return the persisted entity.
     */
    public TraceAuditDTO update(TraceAuditDTO traceAuditDTO) {
        LOG.debug("Request to update TraceAudit : {}", traceAuditDTO);
        TraceAudit traceAudit = traceAuditMapper.toEntity(traceAuditDTO);
        traceAudit = traceAuditRepository.save(traceAudit);
        return traceAuditMapper.toDto(traceAudit);
    }

    /**
     * Partially update a traceAudit.
     *
     * @param traceAuditDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TraceAuditDTO> partialUpdate(TraceAuditDTO traceAuditDTO) {
        LOG.debug("Request to partially update TraceAudit : {}", traceAuditDTO);

        return traceAuditRepository
            .findById(traceAuditDTO.getId())
            .map(existingTraceAudit -> {
                traceAuditMapper.partialUpdate(existingTraceAudit, traceAuditDTO);

                return existingTraceAudit;
            })
            .map(traceAuditRepository::save)
            .map(traceAuditMapper::toDto);
    }

    /**
     * Get one traceAudit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TraceAuditDTO> findOne(Long id) {
        LOG.debug("Request to get TraceAudit : {}", id);
        return traceAuditRepository.findById(id).map(traceAuditMapper::toDto);
    }

    /**
     * Delete the traceAudit by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TraceAudit : {}", id);
        traceAuditRepository.deleteById(id);
    }
}
