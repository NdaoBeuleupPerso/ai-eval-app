package com.mycompany.iaeval.service;

import com.mycompany.iaeval.domain.DocumentJoint;
import com.mycompany.iaeval.repository.DocumentJointRepository;
import com.mycompany.iaeval.service.dto.DocumentJointDTO;
import com.mycompany.iaeval.service.mapper.DocumentJointMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.iaeval.domain.DocumentJoint}.
 */
@Service
@Transactional
public class DocumentJointService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentJointService.class);

    private final DocumentJointRepository documentJointRepository;

    private final DocumentJointMapper documentJointMapper;

    public DocumentJointService(DocumentJointRepository documentJointRepository, DocumentJointMapper documentJointMapper) {
        this.documentJointRepository = documentJointRepository;
        this.documentJointMapper = documentJointMapper;
    }

    /**
     * Save a documentJoint.
     *
     * @param documentJointDTO the entity to save.
     * @return the persisted entity.
     */
    public DocumentJointDTO save(DocumentJointDTO documentJointDTO) {
        LOG.debug("Request to save DocumentJoint : {}", documentJointDTO);
        DocumentJoint documentJoint = documentJointMapper.toEntity(documentJointDTO);
        documentJoint = documentJointRepository.save(documentJoint);
        return documentJointMapper.toDto(documentJoint);
    }

    /**
     * Update a documentJoint.
     *
     * @param documentJointDTO the entity to save.
     * @return the persisted entity.
     */
    public DocumentJointDTO update(DocumentJointDTO documentJointDTO) {
        LOG.debug("Request to update DocumentJoint : {}", documentJointDTO);
        DocumentJoint documentJoint = documentJointMapper.toEntity(documentJointDTO);
        documentJoint = documentJointRepository.save(documentJoint);
        return documentJointMapper.toDto(documentJoint);
    }

    /**
     * Partially update a documentJoint.
     *
     * @param documentJointDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DocumentJointDTO> partialUpdate(DocumentJointDTO documentJointDTO) {
        LOG.debug("Request to partially update DocumentJoint : {}", documentJointDTO);

        return documentJointRepository
            .findById(documentJointDTO.getId())
            .map(existingDocumentJoint -> {
                documentJointMapper.partialUpdate(existingDocumentJoint, documentJointDTO);

                return existingDocumentJoint;
            })
            .map(documentJointRepository::save)
            .map(documentJointMapper::toDto);
    }

    /**
     * Get all the documentJoints.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DocumentJointDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all DocumentJoints");
        return documentJointRepository.findAll(pageable).map(documentJointMapper::toDto);
    }

    /**
     * Get one documentJoint by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DocumentJointDTO> findOne(Long id) {
        LOG.debug("Request to get DocumentJoint : {}", id);
        return documentJointRepository.findById(id).map(documentJointMapper::toDto);
    }

    /**
     * Delete the documentJoint by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete DocumentJoint : {}", id);
        documentJointRepository.deleteById(id);
    }
}
