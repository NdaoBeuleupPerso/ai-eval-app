package com.mycompany.iaeval.service;

import com.mycompany.iaeval.domain.DocumentJoint;
import com.mycompany.iaeval.domain.ReferenceLegale;
import com.mycompany.iaeval.domain.enumeration.FormatDocument;
import com.mycompany.iaeval.repository.ReferenceLegaleRepository;
import com.mycompany.iaeval.service.dto.ReferenceLegaleDTO;
import com.mycompany.iaeval.service.mapper.ReferenceLegaleMapper;
import io.qdrant.client.QdrantClient;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.tika.Tika;
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
 * Service Implementation for managing {@link ReferenceLegale}.
 */
@Service
@Transactional
public class ReferenceLegaleService {

    private static final Logger LOG = LoggerFactory.getLogger(ReferenceLegaleService.class);

    private final ReferenceLegaleRepository referenceLegaleRepository;
    private final ReferenceLegaleMapper referenceLegaleMapper;
    private final VectorStore vectorStore;
    private final QdrantClient qdrantClient;

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

    @Transactional
    public ReferenceLegaleDTO save(ReferenceLegaleDTO dto) {
        ReferenceLegale referenceLegale = referenceLegaleMapper.toEntity(dto);
        StringBuilder texteFinal = new StringBuilder();

        // 1. Générer un UUID unique pour cette indexation Qdrant
        String qdrantUuid = java.util.UUID.randomUUID().toString();

        // 2. Traitement du document binaire
        if (referenceLegale.getFichierTemporaire() != null && referenceLegale.getFichierTemporaire().length > 0) {
            String texteTika = extraireTexteDuDocument(referenceLegale.getFichierTemporaire());
            if (texteTika != null) texteFinal.append(texteTika);

            DocumentJoint docJoint = new DocumentJoint();
            docJoint.setNom(dto.getNomFichier() != null ? dto.getNomFichier() : "document.pdf");
            docJoint.setFormat(detecterFormatViaTika(referenceLegale.getFichierTemporaire()));
            docJoint.setUrl("storage/ia/" + docJoint.getNom());

            // --- STOCKAGE DE L'ID QDRANT DANS DOCUMENT JOINT ---
            docJoint.setIdExterne(qdrantUuid);
            // ---------------------------------------------------

            referenceLegale.setDocument(docJoint);
        }

        // 3. Fusion texte manuel
        if (dto.getContenu() != null && !dto.getContenu().isBlank()) {
            if (texteFinal.length() > 0) texteFinal.append("\n\n--- NOTES ---\n\n");
            texteFinal.append(dto.getContenu());
        }

        String texteAIndexer = texteFinal.toString();
        referenceLegale.setContenu(texteAIndexer);

        // On peut aussi stocker l'ID dans ReferenceLegale pour plus de facilité
        referenceLegale.setQdrantUuid(qdrantUuid);

        if (referenceLegale.getDocument() != null) {
            referenceLegale.getDocument().setContenuOcr(texteAIndexer);
        }

        // 4. Sauvegarde SQL
        referenceLegale = referenceLegaleRepository.save(referenceLegale);
        ReferenceLegaleDTO result = referenceLegaleMapper.toDto(referenceLegale);

        // 5. Indexation Qdrant en utilisant l'UUID généré
        if (!texteAIndexer.isBlank()) {
            indexationQdrant(referenceLegale, qdrantUuid);
        }

        return result;
    }

    /**
     * Extraction de texte via Apache Tika.
     */
    private String extraireTexteDuDocument(byte[] content) {
        if (content == null || content.length == 0) return null;
        try {
            Tika tika = new Tika();
            String texteExtrait = tika.parseToString(new ByteArrayInputStream(content));
            if (texteExtrait != null) {
                return texteExtrait.trim().replaceAll("\\s{2,}", " ");
            }
            return null;
        } catch (Exception e) {
            LOG.error("Erreur Tika lors de l'extraction : {}", e.getMessage());
            return null;
        }
    }

    /**
     * Indexation sémantique dans Qdrant.
     */

    private void indexationQdrant(ReferenceLegale entity, String qdrantUuid) {
        try {
            Map<String, Object> metadata = Map.of(
                "id_sql",
                String.valueOf(entity.getId()),
                "qdrant_doc_id",
                qdrantUuid, // Le lien vers DocumentJoint
                "titre",
                entity.getTitre()
            );

            Document doc = new Document(entity.getContenu(), metadata);
            var textSplitter = new TokenTextSplitter(800, 200, 5, 10000, true);
            List<Document> chunks = textSplitter.split(doc);

            // On s'assure que chaque chunk porte l'ID unique
            chunks.forEach(chunk -> chunk.getMetadata().put("doc_uuid", qdrantUuid));

            vectorStore.add(chunks);
            LOG.info("IA : Document indexé avec l'UUID externe : {}", qdrantUuid);
        } catch (Exception e) {
            LOG.error("IA : Erreur lors de l'indexation : {}", e.getMessage());
        }
    }

    // --- Autres méthodes (update, findAll, findOne, delete) ---

    public ReferenceLegaleDTO update(ReferenceLegaleDTO referenceLegaleDTO) {
        LOG.debug("Request to update ReferenceLegale : {}", referenceLegaleDTO);
        ReferenceLegale referenceLegale = referenceLegaleMapper.toEntity(referenceLegaleDTO);
        referenceLegale = referenceLegaleRepository.save(referenceLegale);
        return referenceLegaleMapper.toDto(referenceLegale);
    }

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

    @Transactional(readOnly = true)
    public Page<ReferenceLegaleDTO> findAll(Pageable pageable) {
        return referenceLegaleRepository.findAll(pageable).map(referenceLegaleMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ReferenceLegaleDTO> findOne(Long id) {
        return referenceLegaleRepository.findById(id).map(referenceLegaleMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete ReferenceLegale : {}", id);
        referenceLegaleRepository.deleteById(id);
    }

    private FormatDocument detecterFormatViaTika(byte[] content) {
        Tika tika = new Tika();
        String mimeType = tika.detect(content); // Renvoie "application/pdf", etc.
        return detecterFormat(mimeType);
    }

    private FormatDocument detecterFormat(String mimeType) {
        if (mimeType == null) {
            return FormatDocument.PDF; // Valeur par défaut si inconnu
        }

        // On convertit en minuscule pour faciliter la comparaison
        String type = mimeType.toLowerCase();

        if (type.contains("pdf")) {
            return FormatDocument.PDF;
        } else if (type.contains("word") || type.contains("officedocument.wordprocessingml")) {
            return FormatDocument.DOCX; // Ou FormatDocument.DOC selon votre enum
        } else if (type.contains("text/plain")) {
            return FormatDocument.TXT;
        } else if (type.contains("image")) {
            return FormatDocument.IMAGE; // Si vous gérez les images (OCR)
        }

        // Par défaut, si on ne reconnaît pas, on peut mettre PDF ou une valeur AUTRE
        return FormatDocument.PDF;
    }
}
