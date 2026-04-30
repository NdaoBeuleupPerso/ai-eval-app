package com.mycompany.iaeval.service;

import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.domain.DocumentJoint;
import com.mycompany.iaeval.domain.enumeration.FormatDocument;
import com.mycompany.iaeval.repository.AppelOffreRepository;
import com.mycompany.iaeval.service.dto.AppelOffreDTO;
import com.mycompany.iaeval.service.mapper.AppelOffreMapper;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AppelOffre}.
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

    @Transactional
    public AppelOffreDTO save(AppelOffreDTO appelOffreDTO) {
        LOG.debug("Sauvegarde AppelOffre (Zero Blob) : {}", appelOffreDTO.getTitre());

        AppelOffre appelOffre = appelOffreMapper.toEntity(appelOffreDTO);

        // 1. Gestion du fichier binaire s'il est présent (dans le champ transient)
        if (appelOffre.getFichierTemporaire() != null && appelOffre.getFichierTemporaire().length > 0) {
            byte[] bytes = appelOffre.getFichierTemporaire();

            // A. Extraction du texte via Tika
            String texteExtrait = extraireTexteDuDocument(bytes);

            // B. Création du DocumentJoint pour les métadonnées
            DocumentJoint docJoint = new DocumentJoint();
            docJoint.setNom(appelOffreDTO.getNomFichier() != null ? appelOffreDTO.getNomFichier() : "appel_offre.pdf");
            docJoint.setFormat(detecterFormatViaTika(bytes));
            docJoint.setContenuOcr(texteExtrait);
            docJoint.setUrl("storage/appels/" + docJoint.getNom());

            // C. Liaison
            appelOffre.setDocumentPrincipal(docJoint);

            // On remplit la description de l'appel d'offre avec le texte extrait
            if (texteExtrait != null) {
                appelOffre.setDescription(texteExtrait);
            }
        }

        // 2. Sauvegarde PostgreSQL (Le binaire est ignoré car @Transient)
        appelOffre = appelOffreRepository.save(appelOffre);
        return appelOffreMapper.toDto(appelOffre);
    }

    @Transactional
    public AppelOffreDTO update(AppelOffreDTO appelOffreDTO) {
        LOG.debug("Mise à jour AppelOffre : {}", appelOffreDTO.getId());

        // Récupérer l'existant pour ne pas perdre le document si on ne le change pas
        AppelOffre existing = appelOffreRepository.findById(appelOffreDTO.getId()).orElseThrow();
        AppelOffre appelOffre = appelOffreMapper.toEntity(appelOffreDTO);

        if (appelOffre.getFichierTemporaire() == null || appelOffre.getFichierTemporaire().length == 0) {
            // On garde l'ancien document et l'ancienne description texte
            appelOffre.setDocumentPrincipal(existing.getDocumentPrincipal());
            if (appelOffre.getDescription() == null) {
                appelOffre.setDescription(existing.getDescription());
            }
        } else {
            // Nouveau fichier envoyé -> on utilise la logique du save
            return this.save(appelOffreDTO);
        }

        appelOffre = appelOffreRepository.save(appelOffre);
        return appelOffreMapper.toDto(appelOffre);
    }

    // --- Méthodes utilitaires Tika ---

    private String extraireTexteDuDocument(byte[] content) {
        try {
            Tika tika = new Tika();
            String result = tika.parseToString(new ByteArrayInputStream(content));
            return (result != null) ? result.trim().replaceAll("\\s{2,}", " ") : null;
        } catch (Exception e) {
            LOG.error("Erreur Tika AppelOffre : {}", e.getMessage());
            return null;
        }
    }

    private FormatDocument detecterFormatViaTika(byte[] content) {
        try {
            Tika tika = new Tika();
            String mimeType = tika.detect(content).toLowerCase();
            if (mimeType.contains("pdf")) return FormatDocument.PDF;
            if (mimeType.contains("word")) return FormatDocument.DOCX;
            return FormatDocument.PDF;
        } catch (Exception e) {
            return FormatDocument.PDF;
        }
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
