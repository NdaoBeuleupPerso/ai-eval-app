package com.mycompany.iaeval.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.domain.Critere;
import com.mycompany.iaeval.domain.enumeration.StatutCritere;
import com.mycompany.iaeval.repository.AppelOffreRepository;
import com.mycompany.iaeval.repository.CritereRepository;
import com.mycompany.iaeval.service.dto.CritereDTO;
import com.mycompany.iaeval.service.mapper.CritereMapper;
import java.util.List;
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

    private final AppelOffreRepository appelOffreRepository; // À injecter
    private final AiService aiService; // Votre service IA
    private final ObjectMapper objectMapper;

    public CritereService(
        CritereRepository critereRepository,
        CritereMapper critereMapper,
        AppelOffreRepository appelOffreRepository,
        AiService aiService,
        ObjectMapper objectMapper
    ) {
        this.critereRepository = critereRepository;
        this.critereMapper = critereMapper;
        this.appelOffreRepository = appelOffreRepository;
        this.aiService = aiService;
        this.objectMapper = objectMapper;
    }

    /**
     * Analyse l'Appel d'Offre pour suggérer des critères automatiquement.
     */
    @Transactional
    public List<CritereDTO> genererCriteresSuggestions(Long appelOffreId) {
        LOG.debug("IA : Début de génération pour l'AO : {}", appelOffreId);

        AppelOffre ao = appelOffreRepository.findById(appelOffreId).orElseThrow(() -> new RuntimeException("Appel d'offre introuvable"));

        if (ao.getDescription() == null || ao.getDescription().isBlank()) {
            throw new RuntimeException("La description de l'appel d'offre est vide. L'IA ne peut pas deviner les critères.");
        }

        String prompt = String.format(
            """
            Tu es un Expert en Marchés Publics. Analyse l'AO suivant :
            TITRE : %s
            DESCRIPTION : %s

            INSTRUCTIONS :
            1. Extrais 5 critères essentiels au plus (ADMINISTRATIF, TECHNIQUE ou FINANCIER).
            2. La somme des pondérations doit faire exactement 100.
            3. Réponds UNIQUEMENT par un tableau JSON, sans texte avant ou après.

            FORMAT :
            [{"nom": "...", "ponderation": ..., "categorie": "...", "description": "..."}]
            """,
            ao.getTitre(),
            ao.getDescription()
        );

        String reponseIA = "";
        try {
            reponseIA = aiService.askIA(prompt);
            LOG.debug("IA : Réponse brute reçue : {}", reponseIA);

            // --- NETTOYAGE ROBUSTE DU JSON ---
            // 1. On enlève les balises de code Markdown si présentes
            String jsonClean = reponseIA.replaceAll("```json", "").replaceAll("```", "").trim();

            // 2. On cherche le premier '[' et le dernier ']' pour isoler le tableau JSON
            int firstBracket = jsonClean.indexOf("[");
            int lastBracket = jsonClean.lastIndexOf("]");

            if (firstBracket == -1 || lastBracket == -1) {
                LOG.error("L'IA n'a pas renvoyé un format JSON valide : {}", reponseIA);
                throw new RuntimeException("Format de réponse IA invalide (pas de crochets JSON)");
            }

            jsonClean = jsonClean.substring(firstBracket, lastBracket + 1);

            // 3. Parsing
            List<Critere> suggestions = objectMapper.readValue(jsonClean, new TypeReference<List<Critere>>() {});

            for (Critere critere : suggestions) {
                critere.setAppelOffre(ao);
                critere.setStatut(StatutCritere.A_VALIDER);
                critereRepository.save(critere);
            }

            return critereMapper.toDto(suggestions);
        } catch (Exception e) {
            LOG.error("CRITIQUE : Échec du traitement IA. Réponse brute : {}", reponseIA);
            LOG.error("Détail de l'erreur : ", e); // Affiche la stacktrace complète dans le
            // terminal
            throw new RuntimeException("L'IA n'a pas pu extraire les critères : " + e.getMessage());
        }
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
