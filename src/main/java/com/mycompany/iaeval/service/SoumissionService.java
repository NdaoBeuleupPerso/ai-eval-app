package com.mycompany.iaeval.service;

import com.mycompany.iaeval.domain.DocumentJoint;
import com.mycompany.iaeval.domain.Soumission;
import com.mycompany.iaeval.domain.enumeration.FormatDocument;
import com.mycompany.iaeval.repository.SoumissionRepository;
import com.mycompany.iaeval.service.dto.DocumentJointDTO;
import com.mycompany.iaeval.service.dto.SoumissionDTO;
import com.mycompany.iaeval.service.mapper.SoumissionMapper;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import org.apache.tika.Tika;
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
    private final Tika tika = new Tika();
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
    @Transactional
    public SoumissionDTO save(SoumissionDTO soumissionDTO) {
        LOG.debug("Enregistrement d'une nouvelle soumission avec documents");

        // 1. Conversion DTO -> Entity
        Soumission soumission = soumissionMapper.toEntity(soumissionDTO);

        // 2. Traitement des nouveaux documents (OCR / Zero-Blob)
        if (soumissionDTO.getNouveauxDocuments() != null && !soumissionDTO.getNouveauxDocuments().isEmpty()) {
            for (DocumentJointDTO docDto : soumissionDTO.getNouveauxDocuments()) {
                if (docDto.getContenu() != null) {
                    byte[] bytes = docDto.getContenu();

                    // A. Extraire le texte pour l'IA
                    String texteOcr = extraireTexte(bytes);

                    // B. Créer l'objet DocumentJoint
                    DocumentJoint dj = new DocumentJoint();
                    dj.setNom(docDto.getNom());
                    dj.setFormat(detecterFormat(bytes));
                    dj.setContenuOcr(texteOcr);
                    dj.setUrl("storage/soumissions/" + System.currentTimeMillis() + "_" + docDto.getNom());

                    // C. Lier à la soumission
                    soumission.addDocuments(dj);

                    LOG.debug("Fichier {} analysé ({} caractères)", dj.getNom(), texteOcr.length());
                }
            }
        }

        // 3. Sauvegarde (Le CascadeType.ALL gère les DocumentJoint automatiquement)
        soumission = soumissionRepository.save(soumission);
        return soumissionMapper.toDto(soumission);
    }

    private String extraireTexte(byte[] content) {
        try {
            String result = tika.parseToString(new ByteArrayInputStream(content));
            return (result != null) ? result.trim().replaceAll("\\s{2,}", " ") : "";
        } catch (Exception e) {
            LOG.error("Erreur OCR : {}", e.getMessage());
            return "[Erreur d'extraction de texte]";
        }
    }

    private FormatDocument detecterFormat(byte[] content) {
        try {
            String mimeType = tika.detect(content).toLowerCase();
            if (mimeType.contains("pdf")) return FormatDocument.PDF;
            if (mimeType.contains("word")) return FormatDocument.DOCX;
            return FormatDocument.PDF; // Par défaut
        } catch (Exception e) {
            return FormatDocument.PDF;
        }
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
