package com.mycompany.iaeval.service;

import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.repository.AppelOffreRepository;
import com.mycompany.iaeval.service.dto.AppelOffreDTO;
import com.mycompany.iaeval.service.mapper.AppelOffreMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.iaeval.domain.AppelOffre}.
 */
@Service
@Transactional
public class AppelOffreService {

    private static final Logger LOG = LoggerFactory.getLogger(AppelOffreService.class);

    private final AppelOffreRepository appelOffreRepository;

    private final AppelOffreMapper appelOffreMapper;

    public AppelOffreService(AppelOffreRepository appelOffreRepository, AppelOffreMapper appelOffreMapper) {
        this.appelOffreRepository = appelOffreRepository;
        this.appelOffreMapper = appelOffreMapper;
    }

    /**
     * Save a appelOffre.
     *
     * @param appelOffreDTO the entity to save.
     * @return the persisted entity.
     */
    public AppelOffreDTO save(AppelOffreDTO appelOffreDTO) {
        LOG.debug("Request to save AppelOffre : {}", appelOffreDTO);
        AppelOffre appelOffre = appelOffreMapper.toEntity(appelOffreDTO);
        appelOffre = appelOffreRepository.save(appelOffre);
        return appelOffreMapper.toDto(appelOffre);
    }

    /**
     * Update a appelOffre.
     *
     * @param appelOffreDTO the entity to save.
     * @return the persisted entity.
     */
    public AppelOffreDTO update(AppelOffreDTO appelOffreDTO) {
        LOG.debug("Request to update AppelOffre : {}", appelOffreDTO);
        AppelOffre appelOffre = appelOffreMapper.toEntity(appelOffreDTO);
        appelOffre = appelOffreRepository.save(appelOffre);
        return appelOffreMapper.toDto(appelOffre);
    }

    /**
     * Partially update a appelOffre.
     *
     * @param appelOffreDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AppelOffreDTO> partialUpdate(AppelOffreDTO appelOffreDTO) {
        LOG.debug("Request to partially update AppelOffre : {}", appelOffreDTO);

        return appelOffreRepository
            .findById(appelOffreDTO.getId())
            .map(existingAppelOffre -> {
                appelOffreMapper.partialUpdate(existingAppelOffre, appelOffreDTO);

                return existingAppelOffre;
            })
            .map(appelOffreRepository::save)
            .map(appelOffreMapper::toDto);
    }

    /**
     * Get one appelOffre by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AppelOffreDTO> findOne(Long id) {
        LOG.debug("Request to get AppelOffre : {}", id);
        return appelOffreRepository.findById(id).map(appelOffreMapper::toDto);
    }

    /**
     * Delete the appelOffre by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AppelOffre : {}", id);
        appelOffreRepository.deleteById(id);
    }
}
