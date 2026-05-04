package com.mycompany.iaeval.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.domain.Critere;
import com.mycompany.iaeval.domain.Evaluation;
import com.mycompany.iaeval.domain.EvaluationCandidat;
import com.mycompany.iaeval.domain.Soumission;
import com.mycompany.iaeval.domain.TraceAudit;
import com.mycompany.iaeval.domain.enumeration.StatutCritere;
import com.mycompany.iaeval.domain.enumeration.TypeCritere;
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
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    //private final SoumissionMapper soumissionMapper;
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
        //this.soumissionMapper = soumissionMapper;
        this.vectorStore = vectorStore;
        this.userRepository = userRepository;
        this.evaluationCandidatRepository = evaluationCandidatRepository;
        this.evaluationCandidatMapper = evaluationCandidatMapper;
        this.AppelOffreRepository = AppelOffreRepository;
    }

    //@Async
    @Transactional
    public void evaluerToutAppel(Long appelOffreId) {
        // 1. Récupérer l'Appel d'Offre et ses critères
        List<EvaluationDTO> evaluationsDtos = new LinkedList<>();
        AppelOffre ao = AppelOffreRepository.findById(appelOffreId).orElseThrow(() -> new RuntimeException("Appel d'offre introuvable"));

        // 1. On ne récupère que les critères VALIDÉS
        List<Critere> criteresValides = ao
            .getCriteres()
            .stream()
            .filter(c -> c.getStatut() == StatutCritere.VALIDE)
            .collect(Collectors.toList());

        // 2. Sécurité : Si aucun critère n'est validé, on arrête tout
        if (criteresValides.isEmpty()) {
            throw new RuntimeException(
                "Impossible de lancer l'évaluation. " +
                "L'administrateur doit d'abord valider au moins un critère (statut VALIDE) pour cet appel d'offre."
            );
        }

        // 3. Regroupement par catégorie (uniquement sur les validés)
        Map<TypeCritere, List<Critere>> criteresParCat = criteresValides.stream().collect(Collectors.groupingBy(Critere::getCategorie));

        // 4. Construction de la grille textuelle pour le prompt
        StringBuilder sbCriteres = new StringBuilder();
        criteresParCat.forEach((cat, list) -> {
            sbCriteres.append("### VOLET ").append(cat.name()).append(" :\n");
            for (Critere c : list) {
                sbCriteres.append(
                    String.format(
                        "- %s (Poids: %.1f points) : %s\n",
                        c.getNom().toUpperCase(),
                        c.getPonderation(),
                        (c.getDescription() != null ? c.getDescription() : "Pas de description")
                    )
                );
            }
            sbCriteres.append("\n");
        });

        String grilleEvaluation = sbCriteres.toString();

        // 2. Récupérer toutes les soumissions de cet appel
        List<Soumission> soumissions = soumissionRepository.findByAppelOffreId(appelOffreId);

        for (Soumission soumission : soumissions) {
            // 3. Préparation des données du candidat à partir de la DB (OCR déjà fait)
            String contenuCandidat = soumission
                .getDocuments()
                .stream()
                .map(doc -> {
                    String titreDoc = (doc.getNom() != null) ? doc.getNom().toUpperCase() : "DOCUMENT SANS NOM";
                    String formatDoc = (doc.getFormat() != null) ? doc.getFormat().toString() : "NON PRÉCISÉ";
                    String texte = (doc.getContenuOcr() != null) ? doc.getContenuOcr() : "[Aucun texte extrait]";

                    // On crée un bloc structuré pour chaque fichier
                    return String.format(
                        """
                        ========== DÉBUT DU FICHIER : %s ==========
                        TYPE/NOM : %s
                        FORMAT : %s
                        CONTENU EXTRAIT :
                        %s
                        ========== FIN DU FICHIER : %s ==========
                        """,
                        titreDoc,
                        titreDoc,
                        formatDoc,
                        texte,
                        titreDoc
                    );
                })
                .collect(Collectors.joining("\n\n"));

            if (contenuCandidat.isBlank()) {
                log.warn("Soumission {} : aucun texte OCR trouvé.", soumission.getId());
                continue;
            }

            // Dans votre Service d'évaluation
            String criteresPourRecherche = ao
                .getCriteres()
                .stream()
                .map(Critere::getNom)
                .limit(5) // On
                // ne
                // prend
                // que
                // les
                // 5
                // plus
                // importants
                // pour
                // ne
                // pas
                // noyer
                // le
                // vecteur
                .collect(Collectors.joining(" "));

            // On construit une requête "dense"
            String termeOptimisé = String.format(
                "%s %s %s",
                ao.getTitre(),
                criteresPourRecherche,
                "Code des marchés publics jurisprudence" // On force le domaine lexical
            );

            String contexteLegal = recupererContexteIA(termeOptimisé);

            if (contexteLegal == null) {
                log.warn("Aucun contexte legal trouvé pour la soumission {}", soumission.getId());
                continue;
            }
            // 4. Lancer l'évaluation pour ce candidat spécifique
            evaluerUnCandidat(ao, soumission, grilleEvaluation, contenuCandidat, contexteLegal);
        }
    }

    public EvaluationDTO evaluerUnCandidat(
        AppelOffre ao,
        Soumission soumission,
        String grilleEvaluation,
        String contenu,
        String contexteLegal
    ) {
        List<Critere> criteresValides = ao
            .getCriteres()
            .stream()
            .filter(c -> c.getStatut() == StatutCritere.VALIDE)
            .collect(Collectors.toList());

        String prompt = String.format(
            """
            Tu es le Rapporteur d'une Commission de Dépouillement.
            Ton analyse porte sur trois volets distincts : ADMINISTRATIF, TECHNIQUE et FINANCIER.

            OBJET DU MARCHÉ : %s
            CANDIDAT : %s

            --- GRILLE D'ÉVALUATION PAR VOLET ---
            %s
            ---  CONTEXTE LÉGAL DE RÉFÉRENCE :
                %s
            --- DOSSIER DU CANDIDAT (Texte OCR par fichier) ---
            %s

            INSTRUCTIONS SPÉCIFIQUES PAR CATÉGORIE :
            1. VOLET ADMINISTRATIF : Vérifie la présence et la validité des pièces (Quitus, Attestations). C'est du "Conforme" ou "Non-Conforme". Si une pièce manque, le score du critère est 0.
            2. VOLET TECHNIQUE : Analyse la qualité de la réponse. Attribue une note proportionnelle à la valeur ajoutée par rapport au poids maximum défini.
            3. VOLET FINANCIER : Vérifie la cohérence des prix et la présence du bordereau de prix unitaire.

            LOGIQUE DE CALCUL (OBLIGATOIRE) :
            - Pour chaque catégorie, la note ne peut excéder la somme des pondérations de ses critères.
            - score_global = (Somme des points obtenus / Somme des pondérations totales) * 100.

            RAPPORT HTML ATTENDU :
            - <h1> PV de Dépouillement - %s </h1>
            - <h2> I. Conformité Administrative </h2> (avec tableau des pièces)
            - <h2> II. Évaluation Technique </h2> (avec analyse des forces/faiblesses)
            - <h2> III. Analyse Financière </h2>

            FORMAT DE SORTIE JSON :
            [METADATA]
            {
              "score_global": [0-100],
              "score_admin": [total_points_admin],
              "score_tech": [total_points_tech],
              "score_fin": [total_points_fin],
              "pv_draft": "Contenu HTML complet"
            }
            [/METADATA]

                Note importante :
                La somme totale des pondérations fournies est de %s points.
                Ton calcul du 'score_global' doit être le total des points obtenus divisé par X,
                ramené sur une échelle de 0 à 100.
            """,
            ao.getTitre(),
            soumission.getCandidat().getNom(),
            grilleEvaluation,
            contexteLegal,
            contenu,
            criteresValides.stream().mapToDouble(Critere::getPonderation).sum()
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
        try {
            // 1. On augmente topK à 10 pour être sûr d'avoir un échantillon représentatif
            SearchRequest req = SearchRequest.builder().query(theme).topK(10).build();

            List<Document> documents = vectorStore.similaritySearch(req);

            // 2. Regroupement par type via un Map pour une gestion propre
            Map<String, List<Document>> docsParType = documents
                .stream()
                .filter(d -> d.getMetadata().get("source_type") != null)
                .collect(Collectors.groupingBy(d -> d.getMetadata().get("source_type").toString()));

            StringBuilder context = new StringBuilder();

            // --- SECTION LOIS ---
            if (docsParType.containsKey("LOI") || docsParType.containsKey("CODE_MARCHES")) {
                context.append("--- CATEGORIE : RÉFÉRENCES LÉGALES ET RÉGLEMENTAIRES ---\n");
                context.append("Instructions IA : Ces articles sont des règles OBLIGATOIRES.\n");

                List<Document> lois = docsParType.getOrDefault("LOI", docsParType.get("CODE_MARCHES"));
                lois.forEach(d -> context.append("- ").append(d.getText()).append("\n"));
                context.append("\n");
            }

            // --- SECTION JURISPRUDENCE ---
            if (docsParType.containsKey("JURISPRUDENCE")) {
                context.append("--- CATEGORIE : JURISPRUDENCE ET DÉCISIONS DE JUSTICE ---\n");
                context.append("Instructions IA : Utilise ces cas pour justifier ton interprétation technique.\n");

                docsParType.get("JURISPRUDENCE").forEach(d -> context.append("- ").append(d.getText()).append("\n"));
                context.append("\n");
            }

            // --- SECTION PV HISTORIQUES ---
            if (docsParType.containsKey("PV_HISTO")) {
                context.append("--- CATEGORIE : EXEMPLES DE PV DE DÉPOUILLEMENT PASSÉS ---\n");
                context.append("Instructions IA : Inspire-toi de ce style de rédaction et de ces barèmes.\n");

                docsParType.get("PV_HISTO").forEach(d -> context.append("- ").append(d.getText()).append("\n"));
            }

            return context.length() > 0 ? context.toString() : "Aucun contexte spécifique trouvé dans la base vectorielle.";
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du contexte Qdrant : {}", e.getMessage());
            return "Erreur technique de récupération du contexte.";
        }
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

    /*@Async
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
    }*/

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
        // 1. Chargement de l'Appel d'Offre et de ses critères
        AppelOffre ao = AppelOffreRepository.findById(appelOffreId).orElseThrow(() -> new RuntimeException("Appel d'offre introuvable"));
        // 2. Préparation de la grille de critères (Regroupée par catégorie)
        String grilleDetaillee = ao
            .getCriteres()
            .stream()
            .filter(c -> c.getStatut() == StatutCritere.VALIDE) // <--- ON FILTRE ICI !
            .map(c ->
                String.format(
                    "- [%s] %s (Importance: %.1f points) : %s",
                    c.getCategorie(),
                    c.getNom(),
                    c.getPonderation(),
                    c.getDescription()
                )
            )
            .collect(Collectors.joining("\n"));

        if (grilleDetaillee.isBlank()) {
            throw new RuntimeException("Aucun critère n'a encore été validé par l'administrateur.");
        }

        // 4. Préparation des données candidat (Structurées par fichier même si c'est une
        // auto-évaluation)
        String contenuCandidat = documentJointRepository
            .findAllById(documentIds)
            .stream()
            .map(doc ->
                String.format(
                    "--- NOM DU DOCUMENT : %s ---\nFORMAT : %s\nCONTENU :\n%s\n--- FIN DU DOCUMENT ---",
                    doc.getNom(),
                    doc.getFormat(),
                    doc.getContenuOcr()
                )
            )
            .collect(Collectors.joining("\n\n"));

        if (contenuCandidat.isBlank()) {
            contenuCandidat = "ERREUR : Le dossier est vide ou illisible.";
        }
        // Dans votre Service d'évaluation
        String criteresPourRecherche = ao
            .getCriteres()
            .stream()
            .map(Critere::getNom)
            .limit(5) // On
            // ne
            // prend
            // que
            // les
            // 5
            // plus
            // importants
            // pour
            // ne
            // pas
            // noyer
            // le
            // vecteur
            .collect(Collectors.joining(" "));

        // On construit une requête "dense"
        String termeOptimisé = String.format(
            "%s %s %s",
            ao.getTitre(),
            criteresPourRecherche,
            "Code des marchés publics jurisprudence" // On force le domaine lexical
        );

        String contexteLegal = recupererContexteIA(termeOptimisé);

        if (contexteLegal == null) {
            log.warn("Aucun contexte legal trouvé pour la soumission {}", appelOffreId);
            contexteLegal = "";
        }

        List<Critere> criteresValides = ao
            .getCriteres()
            .stream()
            .filter(c -> c.getStatut() == StatutCritere.VALIDE)
            .collect(Collectors.toList());

        // 5. Prompt "Coach Stratégique"
        String prompt = String.format(
            """
            Tu es un Consultant Expert en Stratégie de Réponse aux Marchés Publics.
            Ton client est un CANDIDAT qui veut maximiser ses chances de gagner.

            OBJET DU MARCHÉ : %s

            GRILLE D'ÉVALUATION OFFICIELLE (Utilisée par la commission) :
            %s

            CONTEXTE LÉGAL DE RÉFÉRENCE :
            %s

            VOTRE DOSSIER DE CANDIDATURE (Texte extrait) :
            %s

            INSTRUCTIONS D'ANALYSE (Rôle de Coach) :
            1. ÉVALUATION RIGUREUSE : Simule la note que la commission donnerait en respectant les pondérations.
            2. ANALYSE DES LACUNES (Gap Analysis) : Identifie précisément ce qui manque pour atteindre la note maximale sur chaque critère.
            3. CONSEILS D'OPTIMISATION : Pour chaque point faible, donne une action concrète à réaliser avant le dépôt final.

            STRUCTURE DU RAPPORT HTML (Sortie attendue) :
            - <h1> Diagnostic de votre Candidature </h1>
            - <h2> 1. Tableau de Bord des Scores </h2> (Tableau HTML avec colonnes : Critère | Note Estimée | Note Max | Statut)
            - <h2> 2. Analyse des Points Critiques </h2> (Cite les documents sources pour justifier tes doutes)
            - <h2> 3. Plan d'Action pour Gagner </h2> (3 à 5 recommandations prioritaires)

            FORMAT DE SORTIE OBLIGATOIRE :
            [METADATA]
            {
              "score_global": [probabilité_succès_0_100],
              "score_admin": [total_points_admin],
              "score_tech": [total_points_tech],
              "score_fin": [total_points_fin],
              "pv_draft": "Contenu HTML complet"
            }
            [/METADATA]


            Note importante :
            La somme totale des pondérations fournies est de %s points.
            Ton calcul du 'score_global' doit être le total des points obtenus divisé par X,
            ramené sur une échelle de 0 à 100.
            """,
            ao.getTitre(),
            grilleDetaillee,
            contexteLegal,
            contenuCandidat,
            criteresValides.stream().mapToDouble(Critere::getPonderation).sum()
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

        // 1. Chargement de l'Appel d'Offre (Correction : instance au lieu de classe)
        AppelOffre ao = AppelOffreRepository.findById(appelOffreId).orElseThrow(() -> new RuntimeException("Appel d'offre non trouvé"));

        // 2. Récupérer les critères (avec gestion de liste vide)
        String listeCriteres = ao.getCriteres().isEmpty()
            ? "Analyse les besoins à partir du titre et de la description pour déduire les critères logiques."
            : ao
                .getCriteres()
                .stream()
                .filter(c -> c.getStatut() == StatutCritere.VALIDE) // <--- ON FILTRE ICI !
                .map(c -> "- " + c.getNom() + " : " + c.getDescription())
                .collect(Collectors.joining("\n"));

        String descriptionAO = (ao.getDescription() != null) ? ao.getDescription() : "";
        String superRequeteRAG = ao.getTitre() + " " + descriptionAO;
        String contexteIA = recupererContexteIA(superRequeteRAG);

        // 3. Préparation des données candidat
        String contenuCandidat = files
            .stream()
            .map(file -> {
                try {
                    return extractTextFromMultipartFile(file);
                } catch (Exception e) {
                    return "[Erreur extraction file]";
                }
            })
            .collect(Collectors.joining("\n---\n"));

        // 4. Construction du Prompt final
        String prompt = String.format(
            """
            Tu es un Consultant Expert en Marchés Publics et Secrétaire de Commission.
            L'utilisateur est un CANDIDAT qui souhaite une auto-évaluation rigoureuse.

            OBJET DE L'APPEL D'OFFRE : %s

            GRILLE D'ÉVALUATION STRUCTUREE (À vérifier prioritairement) :
            %s

            CONTEXTE LÉGAL ET JURISPRUDENCE :
            %s

            CONTENU DU DOSSIER DÉPOSÉ PAR LE CANDIDAT :
            %s

            LOGIQUE D'ANALYSE :
            Pour CHAQUE critère de la "GRILLE D'ÉVALUATION" :
            1. Localise précisément la preuve dans le dossier du candidat.
            2. Si la preuve est absente ou floue, pénalise lourdement le score.

            INSTRUCTIONS DE RÉPONSE :
            1. Rédige un Rapport d'Évaluation Stratégique en HTML (commence directement par <h1>).
               - Inclus obligatoirement un <table> résumant la conformité pour chaque critère de la grille.
               - SECTION "DIAGNOSTIC" : Probabilité de victoire (Faible, Moyenne, Forte).
               - SECTION "POINTS CRITIQUES" : Identifie les manques selon le CODE_MARCHES.
               - SECTION "OPTIMISATION" : 3 conseils basés sur la JURISPRUDENCE.

            FORMAT DE SORTIE OBLIGATOIRE :
            [METADATA]
            {
              "score_global": [calcul],
              "score_admin": [calcul],
              "score_tech": [calcul],
              "score_fin": [calcul],
              "pv_draft": "Texte HTML intégral"
            }
            [/METADATA]
            """,
            ao.getTitre(),
            listeCriteres,
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
        if (file == null || file.isEmpty()) {
            return "";
        }

        log.debug("Extraction de texte pour le fichier : {} ({})", file.getOriginalFilename(), file.getContentType());

        // 1. Cas simple : Texte brut
        if ("text/plain".equals(file.getContentType())) {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        }

        // 2. Cas général : PDF, DOCX, etc. via Apache Tika
        try (java.io.InputStream is = file.getInputStream()) {
            Tika tika = new Tika();

            // Optionnel : Limiter la taille pour éviter de saturer la mémoire du serveur
            // tika.setMaxStringLength(10 * 1024 * 1024); // ex: 10MB max

            String texteExtrait = tika.parseToString(is);

            if (texteExtrait != null) {
                // NETTOYAGE CRUCIAL POUR L'IA :
                // - On enlève les espaces multiples
                // - On enlève les retours à la ligne excessifs
                // - On trim
                return texteExtrait
                    .trim()
                    .replaceAll("\\s{2,}", " ") // Remplace 2+ espaces par 1 seul
                    .replaceAll("(\\r\\n|\\n|\\r)", " "); // Met tout sur une ligne pour le découpage en chunks
            }
        } catch (Exception e) {
            log.error("Échec de l'extraction de texte (Tika) pour {}: {}", file.getOriginalFilename(), e.getMessage());
            // On renvoie une erreur explicite pour que le prompt de l'IA le sache
            return "[ERREUR EXTRACTION : Impossible de lire le contenu de " + file.getOriginalFilename() + "]";
        }

        return "";
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
