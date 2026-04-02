package com.mycompany.iaeval.service;

import com.mycompany.iaeval.domain.ReferenceLegale;
import com.mycompany.iaeval.repository.ReferenceLegaleRepository;
import com.mycompany.iaeval.service.dto.ReferenceLegaleDTO;
import com.mycompany.iaeval.service.mapper.ReferenceLegaleMapper;
import io.qdrant.client.QdrantClient; // AJOUT
import io.qdrant.client.grpc.Collections.Distance; // AJOUT
import io.qdrant.client.grpc.Collections.VectorParams; // AJOUT
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
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
    private final VectorStore vectorStore;
    private final QdrantClient qdrantClient; // Injection directe du client Qdrant

    public ReferenceLegaleService(
        ReferenceLegaleRepository referenceLegaleRepository,
        ReferenceLegaleMapper referenceLegaleMapper,
        VectorStore vectorStore,
        QdrantClient qdrantClient
    ) {
        this.referenceLegaleRepository = referenceLegaleRepository;
        this.referenceLegaleMapper = referenceLegaleMapper;
        this.vectorStore = vectorStore;
        this.qdrantClient = qdrantClient;
    }

    /**
     * Save a referenceLegale.
     *
     * @param referenceLegaleDTO the entity to save.
     * @return the persisted entity.
     */
    // Ajoutez cet import
    public ReferenceLegaleDTO save(ReferenceLegaleDTO referenceLegaleDTO) {
        LOG.debug("Sauvegarde SQL de la référence");
        ReferenceLegale referenceLegale = referenceLegaleMapper.toEntity(referenceLegaleDTO);
        referenceLegale = referenceLegaleRepository.save(referenceLegale);
        ReferenceLegaleDTO result = referenceLegaleMapper.toDto(referenceLegale);

        // --- AUTOMATISATION QDRANT ---
        try {
            // Assurez-vous que la collection existe (méthode à implémenter si nécessaire)
            // this.assurerPresenceCollection();

            // Préparation des métadonnées communes à tous les chunks
            Map<String, Object> commonMetadata = Map.of(
                "source_type",
                Objects.requireNonNullElse(result.getTypeSource().name(), "UNKNOWN"),
                "titre",
                Objects.requireNonNullElse(result.getTitre(), "Sans titre"),
                "id_sql",
                String.valueOf(result.getId()) // L'ID SQL est crucial pour la gestion des mises à jour/suppressions
            );

            // 1. On crée le document original avec les métadonnées
            Document originalDoc = new Document(result.getContenu(), commonMetadata);

            // 2. On découpe le texte en morceaux (Chunking) avec propagation des métadonnées
            // Les paramètres du TokenTextSplitter sont optimisés pour la recherche sémantique.
            // chunkSize: 800 tokens (taille optimale pour la plupart des LLM)
            // chunkOverlap: 200 tokens (assure la continuité contextuelle entre les chunks)
            var textSplitter = new TokenTextSplitter(800, 200, 5, 10000, true);
            List<Document> chunks = textSplitter.split(originalDoc);
            vectorStore.add(chunks);

            // Ajout d'un index de chunk pour l'ordre si nécessaire
            /* for (int i = 0; i < chunks.size(); i++) {
                chunks.get(i).getMetadata().put("chunk_index", i);
            }*/

            // 3. On envoie les morceaux à Qdrant
            // Avant d'ajouter de nouveaux chunks, il est recommandé de supprimer les anciens
            // associés à cet id_sql pour éviter les doublons et maintenir la cohérence.
            // vectorStore.delete(Map.of("id_sql", String.valueOf(result.getId()))); // Exemple de suppression
            //vectorStore.add(chunks);

            LOG.info("Succès : Document vectorisé en {} morceaux pour id_sql {}.", chunks.size(), result.getId());
        } catch (Exception e) {
            LOG.error("Erreur critique IA lors de la vectorisation de la référence {}: ", result.getId(), e);
            // Gérer l'erreur de vectorisation sans bloquer la sauvegarde de la référence légale
            // Par exemple, marquer la référence comme non vectorisée ou déclencher une alerte.
        }

        return result;
    }

    /* public ReferenceLegaleDTO save(ReferenceLegaleDTO referenceLegaleDTO) {
        LOG.debug("Sauvegarde SQL de la référence");
        ReferenceLegale referenceLegale = referenceLegaleMapper.toEntity(referenceLegaleDTO);
        referenceLegale = referenceLegaleRepository.save(referenceLegale);
        ReferenceLegaleDTO result = referenceLegaleMapper.toDto(referenceLegale);

        // --- AUTOMATISATION QDRANT ---
        try {
            this.assurerPresenceCollection();

            // 1. On crée le document original
            // Dans ReferenceLegaleService.java
            Document originalDoc = new Document(result.getContenu(), Map.of(
                "source_type", result.getTypeSource().name(),
                "titre", result.getTitre(),
                "id_sql", String.valueOf(result.getId()) // <--- CHANGEZ CECI (Long -> String)
            ));

            // 2. On découpe le texte en morceaux (Chunking)
            // Cela règle le problème des requêtes trop lourdes qui causent l'erreur 411
            var textSplitter = new TokenTextSplitter(800, 200, 5, 10000, true);
            List<Document> chunks = textSplitter.split(originalDoc);

            // 3. On envoie les morceaux à Qdrant
            vectorStore.add(chunks);

            LOG.info("Succès : Document vectorisé en {} morceaux.", chunks.size());
        } catch (Exception e) {
            // Log détaillé pour voir si c'est encore un problème HTTP
            LOG.error("Erreur critique IA lors de la vectorisation : ", e);
        }

        return result;
    }*/

    /**
     * Crée la collection dans Qdrant si elle n'existe pas encore.
     */
    private void assurerPresenceCollection() throws Exception {
        String collectionName = "vector_store";

        // On demande à Qdrant la liste des collections
        List<String> collections = qdrantClient.listCollectionsAsync().get();

        if (!collections.contains(collectionName)) {
            LOG.info("La collection '{}' est absente. Création automatique...", collectionName);

            // Configuration pour Mistral (1024 dimensions / Cosine)
            qdrantClient
                .createCollectionAsync(collectionName, VectorParams.newBuilder().setDistance(Distance.Cosine).setSize(1024).build())
                .get();

            LOG.info("Collection '{}' créée avec succès en 1024 dimensions.", collectionName);
        }
    }

    /** public ReferenceLegaleDTO save(ReferenceLegaleDTO referenceLegaleDTO) {
        LOG.debug("Request to save ReferenceLegale : {}", referenceLegaleDTO);
        ReferenceLegale referenceLegale = referenceLegaleMapper.toEntity(referenceLegaleDTO);
        referenceLegale = referenceLegaleRepository.save(referenceLegale);
        return referenceLegaleMapper.toDto(referenceLegale);
    }**/

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
