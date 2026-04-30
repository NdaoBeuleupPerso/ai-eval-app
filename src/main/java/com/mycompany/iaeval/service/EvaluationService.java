package com.mycompany.iaeval.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.domain.DocumentJoint;
import com.mycompany.iaeval.domain.Evaluation;
import com.mycompany.iaeval.domain.EvaluationCandidat;
import com.mycompany.iaeval.domain.Soumission;
import com.mycompany.iaeval.domain.TraceAudit;
import com.mycompany.iaeval.repository.AppelOffreRepository;
import com.mycompany.iaeval.repository.DocumentJointRepository;
import com.mycompany.iaeval.repository.EvaluationCandidatRepository;
import com.mycompany.iaeval.repository.EvaluationRepository;
import com.mycompany.iaeval.repository.SoumissionRepository;
import com.mycompany.iaeval.repository.TraceAuditRepository;
import com.mycompany.iaeval.repository.UserRepository;
import com.mycompany.iaeval.security.SecurityUtils;
import com.mycompany.iaeval.service.dto.EvaluationCandidatDTO;
import com.mycompany.iaeval.service.dto.EvaluationDTO;
import com.mycompany.iaeval.service.dto.SoumissionDTO;
import com.mycompany.iaeval.service.mapper.EvaluationCandidatMapper;
import com.mycompany.iaeval.service.mapper.EvaluationMapper;
import com.mycompany.iaeval.service.mapper.SoumissionMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class EvaluationService {

    private final Logger log = LoggerFactory.getLogger(EvaluationService.class);
    private final EvaluationRepository evaluationRepository;
    private final SoumissionRepository soumissionRepository;
    private final TraceAuditRepository traceAuditRepository;
    private final AiService aiService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EvaluationMapper evaluationMapper;
    private final SoumissionMapper soumissionMapper;
    private final VectorStore vectorStore;
    private final UserRepository userRepository;
    private final EvaluationCandidatRepository evaluationCandidatRepository;
    private final EvaluationCandidatMapper evaluationCandidatMapper;
    private final AppelOffreRepository AppelOffreRepository;
    private final DocumentJointRepository documentJointRepository;

    public EvaluationService(
        EvaluationRepository evaluationRepository,
        SoumissionRepository soumissionRepository,
        TraceAuditRepository traceAuditRepository,
        DocumentJointRepository documentJointRepository,
        AiService aiService,
        EvaluationMapper evaluationMapper,
        SoumissionMapper soumissionMapper,
        VectorStore vectorStore,
        UserRepository userRepository,
        EvaluationCandidatRepository evaluationCandidatRepository,
        EvaluationCandidatMapper evaluationCandidatMapper,
        AppelOffreRepository AppelOffreRepository
    ) {
        this.evaluationRepository = evaluationRepository;
        this.soumissionRepository = soumissionRepository;
        this.traceAuditRepository = traceAuditRepository;
        this.aiService = aiService;
        this.evaluationMapper = evaluationMapper;
        this.documentJointRepository = documentJointRepository;
        this.soumissionMapper = soumissionMapper;
        this.vectorStore = vectorStore;
        this.userRepository = userRepository;
        this.evaluationCandidatRepository = evaluationCandidatRepository;
        this.evaluationCandidatMapper = evaluationCandidatMapper;
        this.AppelOffreRepository = AppelOffreRepository;
    }

    @Transactional
    public EvaluationDTO evaluerByAIAgent(SoumissionDTO soumissionDTO) {
        log.info("Début de l'évaluation IA pour la soumission ID : {}", soumissionDTO.getId());

        // 1. Chargement de la soumission et vérification
        Soumission soumission = soumissionRepository
            .findById(soumissionDTO.getId())
            .orElseThrow(() -> new RuntimeException("Soumission introuvable"));

        if (soumission.getEvaluation() != null) {
            log.info("Soumission déjà évaluée. ID Evaluation : {}", soumission.getEvaluation().getId());
            return evaluationMapper.toDto(soumission.getEvaluation());
        }

        // 2. Préparation du Contexte (RAG)
        AppelOffre ao = soumission.getAppelOffre();
        String descriptionAO = (ao.getDescription() != null) ? ao.getDescription() : "";
        String superRequeteRAG = ao.getTitre() + " " + descriptionAO;

        String contexteIA = recupererContexteIA(superRequeteRAG);

        // 3. Préparation des données candidat (OCR)
        String contenuCandidat = soumission
            .getDocuments()
            .stream()
            .map(DocumentJoint::getContenuOcr)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n---\n"));

        if (contenuCandidat.isBlank()) {
            contenuCandidat = "ERREUR : Aucun texte n'a été extrait des documents du candidat.";
        }

        // 4. Construction du Prompt Expert
        String prompt = String.format(
            """
            Tu es un Secrétaire de Commission des Marchés expert.

            OBJET : %s

            CONTEXTE LÉGAL ET JURISPRUDENCE (Sources à utiliser) :
            %s

            CONTENU DE LA SOUMISSION DU CANDIDAT :
            %s

            INSTRUCTIONS :
            1. Rédige un Procès-Verbal (PV) de dépouillement détaillé en HTML.
            - Utilise uniquement <h1>, <h2>, <p> et <table> (avec bordures).
            - NE FAIS AUCUNE INTRODUCTION (pas de "Voici le rapport"). Commence par <h1>.
            - Cite la JURISPRUDENCE fournie pour justifier tes scores techniques.
            - Cite le CODE_MARCHES pour la conformité administrative.
            2. Calcule des scores RÉELS (0-100) basés sur les preuves trouvées.

            FORMAT DE SORTIE OBLIGATOIRE :
            Termine ta réponse par ce bloc JSON entre les balises [METADATA] :

            [METADATA]
            {
              "score_global": [calcul],
              "score_admin": [calcul],
              "score_tech": [calcul],
              "score_fin": [calcul],
              "pv_draft": "Copie ici le texte intégral du PV HTML généré"
            }
            [/METADATA]
            """,
            ao.getTitre(),
            contexteIA,
            contenuCandidat
        );

        Evaluation evaluation = new Evaluation();
        evaluation.setSoumission(soumission);

        try {
            // 5. Appel à l'IA
            String reponseIA = aiService.askIA(prompt);

            if (reponseIA.contains("[METADATA]")) {
                // Séparation Rapport / Métadonnées
                String[] segments = reponseIA.split("\\[METADATA\\]");
                String rapportBrut = segments[0].trim();
                String metadataSection = segments[1].split("\\[/METADATA\\]")[0].trim();

                // Nettoyage du HTML (Suppression du bavardage IA)
                rapportBrut = rapportBrut.replace("```html", "").replace("```", "").trim();
                int firstTagIndex = rapportBrut.indexOf("<h");
                if (firstTagIndex != -1) {
                    rapportBrut = rapportBrut.substring(firstTagIndex);
                }
                evaluation.setRapportAnalyse(rapportBrut);

                // Parsing des scores JSON
                String jsonClean = metadataSection.replace("```json", "").replace("```", "").trim();
                //Map<String, Object> data = objectMapper.readValue(jsonClean, Map.class);
                Map<String, Object> data = objectMapper.readValue(jsonClean, new TypeReference<Map<String, Object>>() {});
                evaluation.setScoreGlobal(parseScore(data.get("score_global")));
                evaluation.setScoreAdmin(parseScore(data.get("score_admin")));
                evaluation.setScoreTech(parseScore(data.get("score_tech")));
                evaluation.setScoreFin(parseScore(data.get("score_fin")));

                if (data.containsKey("pv_draft")) {
                    evaluation.setDocumentPv(data.get("pv_draft").toString());
                    evaluation.setDocumentPvContentType("text/plain");
                }
            } else {
                evaluation.setRapportAnalyse(reponseIA);
            }
        } catch (Exception e) {
            log.error("Erreur critique IA : {}", e.getMessage());
            evaluation.setRapportAnalyse("Erreur lors de l'analyse : " + e.getMessage());
        }

        // 6. Finalisation et Audit
        evaluation.setDateEvaluation(Instant.now());
        evaluation.setEstValidee(false);
        evaluation = evaluationRepository.save(evaluation);
        // Récupération de l'utilisateur courant
        SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).ifPresent(evaluation::setEvaluateur);

        // Sauvegarde en cascade (Important pour l'ID)
        //evaluation = evaluationRepository.save(evaluation);
        soumission.setEvaluation(evaluation);
        soumissionRepository.save(soumission);

        // Création de la trace d'audit
        TraceAudit audit = new TraceAudit()
            .action("IA_EVALUATION")
            .horodatage(Instant.now())
            .evaluation(evaluation)
            .promptUtilise(prompt.substring(0, Math.min(prompt.length(), 2000)));

        traceAuditRepository.save(audit);

        // Ajout à la collection (HashSet pour éviter le ClassCastException)
        if (evaluation.getTraces() == null) {
            evaluation.setTraces(new HashSet<>());
        }
        evaluation.getTraces().add(audit);

        evaluation = evaluationRepository.saveAndFlush(evaluation);
        return evaluationMapper.toDto(evaluation);
    }

    private String recupererContexteIA(String theme) {
        StringBuilder sb = new StringBuilder();
        try {
            // Utilisation de SearchRequest.builder() et getText()
            SearchRequest req = SearchRequest.builder().query(theme).topK(3).build();
            List<Document> documents = vectorStore.similaritySearch(req);

            // 2. Séparation par type (basé sur la métadonnée 'source_type')
            String lois = documents
                .stream()
                .filter(d -> "LOI".equals(d.getMetadata().get("source_type")))
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));

            String pvHistoriques = documents
                .stream()
                .filter(d -> "PV_HISTO".equals(d.getMetadata().get("source_type")))
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));

            // 3. Construction du bloc de texte pour l'IA
            StringBuilder context = new StringBuilder();
            if (!lois.isEmpty()) {
                context.append("### ARTICLES DE LOI APPLICABLES :\n").append(lois).append("\n\n");
            }
            if (!pvHistoriques.isEmpty()) {
                context.append("### EXEMPLES DE PV PRÉCÉDENTS (Style à imiter) :\n").append(pvHistoriques).append("\n\n");
            }

            return context.length() > 0 ? context.toString() : "Aucun contexte spécifique trouvé.";
        } catch (Exception e) {
            log.warn("Erreur Qdrant : {}", e.getMessage());
        }
        return !sb.isEmpty() ? sb.toString() : "Contexte general.";
    }

    private Double parseScore(Object value) {
        if (value == null) return 0.0;
        String valStr = value.toString().replaceAll("[^0-9.]", ""); // Enlève tout ce qui n'est pas un chiffre ou un point
        if (valStr.isEmpty()) return 0.0;
        try {
            return Double.valueOf(valStr);
        } catch (NumberFormatException e) {
            log.warn("Valeur de score non numérique reçue de l'IA : {}", value);
            return 0.0;
        }
    }

    @Transactional(readOnly = true)
    public List<EvaluationDTO> findAllByAppelOffre(Long appelOffreId) {
        log.debug("Request to get all Evaluations for AppelOffre : {}", appelOffreId);
        return evaluationRepository
            .findAllBySoumissionAppelOffreId(appelOffreId)
            .stream()
            .map(evaluationMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Async
    @Transactional
    public void evaluerToutAppel(Long id) {
        log.info("Début de l'évaluation globale pour l'Appel d'Offre ID : {}", id);

        // 1. On ne récupère QUE les soumissions liées à cet Appel d'Offre
        List<Soumission> soumissions = soumissionRepository.findByAppelOffreId(id);

        if (soumissions.isEmpty()) {
            log.warn("Aucune soumission trouvée pour l'appel d'offre {}", id);
            return;
        }

        soumissions.forEach(s -> {
            try {
                // 2. On vérifie si elle n'est pas déjà évaluée pour gagner du temps
                if (s.getEvaluation() == null) {
                    log.info("Évaluation automatique de la soumission ID : {}", s.getId());
                    SoumissionDTO dtoSoumission = soumissionMapper.toDto(s);
                    this.evaluerByAIAgent(dtoSoumission);
                } else {
                    log.info("Soumission {} déjà évaluée, passage à la suivante.", s.getId());
                }
            } catch (Exception e) {
                // 3. IMPORTANT : On catch l'erreur ici pour que si UN candidat plante (ex: IA
                // timeout),
                // les autres soient quand même évalués.
                log.error("Erreur lors de l'évaluation du candidat {} : {}", s.getId(), e.getMessage());
            }
        });
    }

    public EvaluationDTO validerEvaluation(Long id, String comment) {
        Evaluation eval = evaluationRepository.findById(id).orElseThrow();
        eval.setEstValidee(true);
        eval.setCommentaireEvaluateur(comment);
        vectorStore.add(List.of(new Document("PV VALIDE: " + eval.getRapportAnalyse(), Map.of("source", "PV"))));
        return evaluationMapper.toDto(evaluationRepository.save(eval));
    }

    // Methodes JHipster de base
    @Transactional(readOnly = true)
    public Page<EvaluationDTO> findAll(Pageable p) {
        return evaluationRepository.findAll(p).map(evaluationMapper::toDto);
    }

    @Transactional(readOnly = true)
    /**
     * Mise à jour partielle requise par JHipster (EvaluationResource).
     */
    public Optional<EvaluationDTO> partialUpdate(EvaluationDTO evaluationDTO) {
        log.debug("Request to partially update Evaluation : {}", evaluationDTO);

        return evaluationRepository
            .findById(evaluationDTO.getId())
            .map(existingEvaluation -> {
                evaluationMapper.partialUpdate(existingEvaluation, evaluationDTO);
                return existingEvaluation;
            })
            .map(evaluationRepository::save)
            .map(evaluationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<EvaluationDTO> findAllPendingValidation(Pageable pageable) {
        return evaluationRepository.findAllByEstValideeFalse(pageable).map(evaluationMapper::toDto);
    }

    public Page<EvaluationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return evaluationRepository.findAllWithEagerRelationships(pageable).map(evaluationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<EvaluationDTO> findAllWhereSoumissionIsNull() {
        return evaluationRepository
            .findAll()
            .stream()
            .filter(evaluation -> evaluation.getSoumission() == null)
            .map(evaluationMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public Optional<EvaluationDTO> findOne(Long id) {
        return evaluationRepository.findOneWithEagerRelationships(id).map(evaluationMapper::toDto);
    }

    public EvaluationDTO update(EvaluationDTO evaluationDTO) {
        Evaluation evaluation = evaluationMapper.toEntity(evaluationDTO);
        evaluation = evaluationRepository.save(evaluation);
        return evaluationMapper.toDto(evaluation);
    }

    public void delete(Long id) {
        evaluationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public String genererPVSynthese(Long appelOffreId) {
        // 1. Récupérer uniquement les évaluations validées par l'humain
        List<Evaluation> evaluationsValidees = evaluationRepository.findAllBySoumissionAppelOffreIdAndEstValideeTrue(appelOffreId);

        if (evaluationsValidees.isEmpty()) {
            return "Aucune évaluation validée pour cet appel d'offre.";
        }

        String titreOffre = evaluationsValidees.get(0).getSoumission().getAppelOffre().getTitre();

        StringBuilder pvFinal = new StringBuilder();
        pvFinal.append("# PROCÈS-VERBAL DE DÉPOUILLEMENT GLOBAL\n");
        pvFinal.append("## APPEL D'OFFRE : ").append(titreOffre).append("\n");
        pvFinal.append("### Date de génération : ").append(java.time.LocalDate.now()).append("\n\n");
        pvFinal.append("--- \n\n");

        for (Evaluation eval : evaluationsValidees) {
            pvFinal.append("### CANDIDAT : ").append(eval.getSoumission().getCandidat().getNom().toUpperCase()).append("\n");
            pvFinal.append("- **Score Global** : ").append(eval.getScoreGlobal()).append("/100\n");
            pvFinal.append("- **Note Technique** : ").append(eval.getScoreTech()).append("/100\n");
            pvFinal.append("- **Note Administrative** : ").append(eval.getScoreAdmin()).append("/100\n");
            pvFinal.append("\n**Synthèse de l'évaluation :**\n");
            pvFinal.append(eval.getRapportAnalyse()).append("\n\n");
            pvFinal.append("--- \n\n");
        }

        pvFinal.append("\n**Conclusion de la Commission :**\n");
        pvFinal.append("Sur la base des scores ci-dessus, le soumissionnaire le mieux-disant est : ");

        // Petite logique pour trouver le gagnant automatiquement
        evaluationsValidees
            .stream()
            .max(Comparator.comparing(Evaluation::getScoreGlobal))
            .ifPresent(best -> pvFinal.append("**").append(best.getSoumission().getCandidat().getNom()).append("**"));

        return pvFinal.toString();
    }

    // Petite classe interne pour faciliter le parsing des scores
    /*private static class AiScoresJson {

        public Double score_global;
        public Double score_admin;
        public Double score_tech;
        public Double score_fin;
        public String pv_draft;
    }*/

    @Transactional
    public EvaluationCandidatDTO evaluerByAIAgentCandidat(List<Long> documentIds, Long appelOffreId) {
        // 1. Chargement de la soumission et vérification

        // 2. Préparation du Contexte (RAG)
        Optional<AppelOffre> ao = AppelOffreRepository.findById(appelOffreId);

        String descriptionAO = (ao.get().getDescription() != null) ? ao.get().getDescription() : "";
        String superRequeteRAG = ao.get().getTitre() + " " + descriptionAO;

        String contexteIA = recupererContexteIA(superRequeteRAG);

        // On récupère le contexte historique et code marché

        // 3. Préparation des données candidat (OCR)
        String contenuCandidat = documentJointRepository
            .findAllById(documentIds)
            .stream()
            .map(DocumentJoint::getContenuOcr)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n---\n"));

        if (contenuCandidat.isBlank()) {
            contenuCandidat = "ERREUR : Aucun texte n'a été extrait des documents du candidat.";
        }

        // 4. Construction du Prompt Expert focalisé sur les chances de succès
        String prompt = String.format(
            """
            Tu es un Consultant Expert en Marchés Publics et Secrétaire de Commission.
            L'utilisateur est un CANDIDAT qui souhaite une auto-évaluation rigoureuse pour connaître ses CHANCES DE REMPORTER le marché.

            OBJET DE L'APPEL D'OFFRE : %s

            CRITÈRES DE SÉLECTION ET JURISPRUDENCE (Sources de référence) :
            %s

            CONTENU DU DOSSIER DÉPOSÉ PAR LE CANDIDAT :
            %s

            INSTRUCTIONS D'ANALYSE :
            1. Rédige un Rapport d'Évaluation Stratégique en HTML.
               - Utilise uniquement <h1>, <h2>, <p> et <table> (avec bordures).
               - NE FAIS AUCUNE INTRODUCTION (pas de "Voici votre analyse"). Commence par <h1>.
               - SECTION "DIAGNOSTIC DES CHANCES" : Évalue clairement la probabilité de victoire (Faible, Moyenne, Forte) en fonction de la concurrence type et des exigences du client.
               - SECTION "POINTS CRITIQUES" : Identifie les manques ou les faiblesses qui pourraient causer le rejet de l'offre (en citant le CODE_MARCHES).
               - SECTION "OPTIMISATION" : Donne 3 conseils concrets pour améliorer le score technique en te basant sur la JURISPRUDENCE fournie.

            2. Calcule des scores RÉELS et SANS COMPLAISANCE (0-100) :
               - Le "score_global" représente ici la PROBABILITÉ DE SUCCÈS.
               - Sois très critique : si une preuve manque, le score doit chuter.

            FORMAT DE SORTIE OBLIGATOIRE :
            Termine impérativement ta réponse par ce bloc JSON entre les balises [METADATA] :

            [METADATA]
            {
              "score_global": [calcul_probabilite_victoire],
              "score_admin": [score_conformite_administrative],
              "score_tech": [score_valeur_technique],
              "score_fin": [score_coherence_prix],
              "pv_draft": "Copie ici le texte intégral du rapport HTML généré"
            }
            [/METADATA]
            """,
            ao.get().getTitre(),
            contexteIA,
            contenuCandidat
        );

        EvaluationCandidat evaluation = new EvaluationCandidat();
        evaluation.setSoumission(null); // On lie l'évaluation à la soumission plus tard pour éviter les problèmes de cascade

        try {
            // 5. Appel à l'IA
            String reponseIA = aiService.askIA(prompt);

            if (reponseIA.contains("[METADATA]")) {
                // Séparation Rapport / Métadonnées
                String[] segments = reponseIA.split("\\[METADATA\\]");
                String rapportBrut = segments[0].trim();
                String metadataSection = segments[1].split("\\[/METADATA\\]")[0].trim();

                // Nettoyage du HTML (Suppression du bavardage IA)
                rapportBrut = rapportBrut.replace("```html", "").replace("```", "").trim();
                int firstTagIndex = rapportBrut.indexOf("<h");
                if (firstTagIndex != -1) {
                    rapportBrut = rapportBrut.substring(firstTagIndex);
                }
                evaluation.setRapportAnalyse(rapportBrut);

                // Parsing des scores JSON
                String jsonClean = metadataSection.replace("```json", "").replace("```", "").trim();
                //Map<String, Object> data = objectMapper.readValue(jsonClean, Map.class);
                Map<String, Object> data = objectMapper.readValue(jsonClean, new TypeReference<Map<String, Object>>() {});
                evaluation.setScoreGlobal(parseScore(data.get("score_global")));
                evaluation.setScoreAdmin(parseScore(data.get("score_admin")));
                evaluation.setScoreTech(parseScore(data.get("score_tech")));
                evaluation.setScoreFin(parseScore(data.get("score_fin")));

                if (data.containsKey("pv_draft")) {
                    evaluation.setDocumentPv(data.get("pv_draft").toString());
                    evaluation.setDocumentPvContentType("text/plain");
                }
            } else {
                evaluation.setRapportAnalyse(reponseIA);
            }
        } catch (Exception e) {
            log.error("Erreur critique IA : {}", e.getMessage());
            evaluation.setRapportAnalyse("Erreur lors de l'analyse : " + e.getMessage());
        }

        // 6. Finalisation et Audit
        evaluation.setDateEvaluation(Instant.now());
        evaluation.setEstValidee(false);
        evaluation = evaluationCandidatRepository.save(evaluation);
        // Récupération de l'utilisateur courant

        // Sauvegarde en cascade (Important pour l'ID)
        //evaluation = evaluationRepository.save(evaluation);
        //soumission.setEvaluation_candidat(evaluation);
        //soumissionRepository.save(soumission);
        //evaluation = evaluationCandidatRepository.saveAndFlush(evaluation);
        return evaluationCandidatMapper.toDto(evaluation);
    }

    @Transactional
    public EvaluationCandidatDTO evaluerFichiersTemporaires(List<MultipartFile> files, Long appelOffreId) {
        log.debug("Simulation d'évaluation pour {} fichiers sur l'AO : {}", files.size(), appelOffreId);

        // 1. Préparation du Contexte (RAG) - Identique à votre code
        AppelOffre ao = AppelOffreRepository.findById(appelOffreId).orElseThrow(() -> new RuntimeException("Appel d'offre non trouvé"));

        String descriptionAO = (ao.getDescription() != null) ? ao.getDescription() : "";
        String superRequeteRAG = ao.getTitre() + " " + descriptionAO;
        String contexteIA = recupererContexteIA(superRequeteRAG);

        // 2. Préparation des données candidat (Extraction directe des MultipartFiles)
        String contenuCandidat = files
            .stream()
            .map(file -> {
                try {
                    // Ici, vous appelez votre service d'extraction de texte (Tika, PDFBox, etc.)
                    return extractTextFromMultipartFile(file);
                } catch (Exception e) {
                    log.error("Erreur d'extraction sur le fichier : " + file.getOriginalFilename());
                    return "[Erreur extraction : " + file.getOriginalFilename() + "]";
                }
            })
            .collect(Collectors.joining("\n---\n"));

        if (contenuCandidat.isBlank()) {
            contenuCandidat = "ERREUR : Aucun texte n'a été extrait des fichiers téléversés.";
        }

        // 3. Le Prompt (Le même que le vôtre, car il est très bien structuré)
        String prompt = String.format(
            """
            Tu es un Consultant Expert en Marchés Publics et Secrétaire de Commission.
            L'utilisateur est un CANDIDAT qui souhaite une auto-évaluation rigoureuse pour connaître ses CHANCES DE REMPORTER le marché.

            OBJET DE L'APPEL D'OFFRE : %s

            CRITÈRES DE SÉLECTION ET JURISPRUDENCE (Sources de référence) :
            %s

            CONTENU DU DOSSIER DÉPOSÉ PAR LE CANDIDAT :
            %s

            INSTRUCTIONS D'ANALYSE :
            1. Rédige un Rapport d'Évaluation Stratégique en HTML.
               - Utilise uniquement <h1>, <h2>, <p> et <table> (avec bordures).
               - NE FAIS AUCUNE INTRODUCTION (pas de "Voici votre analyse"). Commence par <h1>.
               - SECTION "DIAGNOSTIC DES CHANCES" : Évalue clairement la probabilité de victoire (Faible, Moyenne, Forte) en fonction de la concurrence type et des exigences du client.
               - SECTION "POINTS CRITIQUES" : Identifie les manques ou les faiblesses qui pourraient causer le rejet de l'offre (en citant le CODE_MARCHES).
               - SECTION "OPTIMISATION" : Donne 3 conseils concrets pour améliorer le score technique en te basant sur la JURISPRUDENCE fournie.

            2. Calcule des scores RÉELS et SANS COMPLAISANCE (0-100) :
               - Le "score_global" représente ici la PROBABILITÉ DE SUCCÈS.
               - Sois très critique : si une preuve manque, le score doit chuter.

            FORMAT DE SORTIE OBLIGATOIRE :
            Termine impérativement ta réponse par ce bloc JSON entre les balises [METADATA] :

            [METADATA]
            {
              "score_global": [calcul_probabilite_victoire],
              "score_admin": [score_conformite_administrative],
              "score_tech": [score_valeur_technique],
              "score_fin": [score_coherence_prix],
              "pv_draft": "Copie ici le texte intégral du rapport HTML généré"
            }
            [/METADATA]
            """,
            ao.getTitre(),
            contexteIA,
            contenuCandidat
        );

        // 4. Création de l'entité d'évaluation (Audit de la simulation)
        EvaluationCandidat evaluation = new EvaluationCandidat();
        evaluation.setSoumission(null); // Important : C'est une simulation, pas encore de soumission officielle

        try {
            // 5. Appel à l'IA et Parsing (Identique à votre logique actuelle)
            String reponseIA = aiService.askIA(prompt);
            parseIAResponseToEvaluation(reponseIA, evaluation); // Factorisez votre logique de split [METADATA] ici
        } catch (Exception e) {
            log.error("Erreur critique IA : {}", e.getMessage());
            evaluation.setRapportAnalyse("Erreur lors de l'analyse : " + e.getMessage());
        }

        // 6. Finalisation
        evaluation.setDateEvaluation(Instant.now());
        evaluation.setEstValidee(false);
        //evaluation.set("Analyse de simulation (Documents téléversés)");

        evaluation = evaluationCandidatRepository.save(evaluation);
        return evaluationCandidatMapper.toDto(evaluation);
    }

    /**
     * Aide à l'extraction de texte (Exemple simplifié)
     */
    private String extractTextFromMultipartFile(MultipartFile file) throws Exception {
        // Si c'est du texte brut :
        if (file.getContentType().equals("text/plain")) {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        }
        // Pour les PDF, il est fortement conseillé d'utiliser Apache Tika :
        // return tikaFacade.parseToString(file.getInputStream());
        return "Contenu du fichier " + file.getOriginalFilename() + " (simulation d'extraction)";
    }

    /**
     * Extrait le rapport HTML et les métadonnées JSON de la réponse de l'IA. Remplit l'entité
     * EvaluationCandidat fournie.
     */
    private void parseIAResponseToEvaluation(String reponseIA, EvaluationCandidat evaluation) {
        if (reponseIA == null || reponseIA.isBlank()) {
            evaluation.setRapportAnalyse("L'IA n'a retourné aucune réponse.");
            return;
        }

        if (reponseIA.contains("[METADATA]")) {
            try {
                if (reponseIA.contains("[METADATA]")) {
                    // Séparation Rapport / Métadonnées
                    String[] segments = reponseIA.split("\\[METADATA\\]");
                    String rapportBrut = segments[0].trim();
                    String metadataSection = segments[1].split("\\[/METADATA\\]")[0].trim();

                    // Nettoyage du HTML (Suppression du bavardage IA)
                    rapportBrut = rapportBrut.replace("```html", "").replace("```", "").trim();
                    int firstTagIndex = rapportBrut.indexOf("<h");
                    if (firstTagIndex != -1) {
                        rapportBrut = rapportBrut.substring(firstTagIndex);
                    }
                    evaluation.setRapportAnalyse(rapportBrut);

                    // Parsing des scores JSON
                    String jsonClean = metadataSection.replace("```json", "").replace("```", "").trim();
                    //Map<String, Object> data = objectMapper.readValue(jsonClean, Map.class);
                    Map<String, Object> data = objectMapper.readValue(jsonClean, new TypeReference<Map<String, Object>>() {});
                    evaluation.setScoreGlobal(parseScore(data.get("score_global")));
                    evaluation.setScoreAdmin(parseScore(data.get("score_admin")));
                    evaluation.setScoreTech(parseScore(data.get("score_tech")));
                    evaluation.setScoreFin(parseScore(data.get("score_fin")));

                    if (data.containsKey("pv_draft")) {
                        evaluation.setDocumentPv(data.get("pv_draft").toString());
                        evaluation.setDocumentPvContentType("text/plain");
                    }
                } else {
                    evaluation.setRapportAnalyse(reponseIA);
                }
            } catch (Exception e) {
                log.error("Erreur lors du parsing de la réponse IA : {}", e.getMessage());
                evaluation.setRapportAnalyse("Erreur lors du parsing de la réponse IA : " + e.getMessage());
            }
        } else {
            evaluation.setRapportAnalyse(reponseIA);
        }
    }
}
