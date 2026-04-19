package com.mycompany.iaeval.web.rest;

import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.repository.AppelOffreRepository;
import com.mycompany.iaeval.security.AuthoritiesConstants;
import com.mycompany.iaeval.service.EvaluationService;
import com.mycompany.iaeval.service.SoumissionService;
import com.mycompany.iaeval.service.dto.EvaluationDTO;
import com.mycompany.iaeval.service.dto.SoumissionDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Soumissionnaire evaluation operations.
 * Manages AI evaluation requests from budget suppliers (soumissionnaires).
 */
@RestController
@RequestMapping("/api/soumissionnaire")
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.SOUMISSIONNAIRE + "')")
public class SoumissionnaireEvaluationResource {

    private static final Logger LOG = LoggerFactory.getLogger(SoumissionnaireEvaluationResource.class);

    private final EvaluationService evaluationService;
    private final SoumissionService soumissionService;
    private final AppelOffreRepository appelOffreRepository;

    public SoumissionnaireEvaluationResource(
        EvaluationService evaluationService,
        SoumissionService soumissionService,
        AppelOffreRepository appelOffreRepository
    ) {
        this.evaluationService = evaluationService;
        this.soumissionService = soumissionService;
        this.appelOffreRepository = appelOffreRepository;
    }

    /**
     * GET /appels-offres : Get list of open calls for tender for current user
     * Returns all open calls for tender (StatutAppel.OUVERT) that are available for submission.
     * When full integration is complete, this can be filtered by soumissionnaire status.
     *
     * @return list of available AppelOffre for evaluation
     */
    @GetMapping("/appels-offres8RRRR")
    public ResponseEntity<List<Map<String, Object>>> getMocksAppelsOffresDisponibles() {
        LOG.debug("REST request to get AppelsOffres disponibles for soumissionnaire");

        try {
            // Retrieve all open calls for tender
            // Note: When full database schema is implemented, we'll filter by soumissionnaire status
            // List<AppelOffre> appels = appelOffreRepository.findByStatut(StatutAppel.OUVERT);

            // For now, return mock data structure that's compatible with frontend
            List<Map<String, Object>> response = List.of(
                Map.ofEntries(
                    Map.entry("id", 1L),
                    Map.entry("reference", "AO-2024-001"),
                    Map.entry("titre", "Développement Application Web"),
                    Map.entry("description", "Création d'une application web pour gestion des ressources"),
                    Map.entry("dateCloture", "2024-06-30"),
                    Map.entry("statut", "OUVERT")
                ),
                Map.ofEntries(
                    Map.entry("id", 2L),
                    Map.entry("reference", "AO-2024-002"),
                    Map.entry("titre", "Maintenance Infrastructure IT"),
                    Map.entry("description", "Contrat de maintenance pour infrastructure IT sur 3 ans"),
                    Map.entry("dateCloture", "2024-05-15"),
                    Map.entry("statut", "OUVERT")
                ),
                Map.ofEntries(
                    Map.entry("id", 3L),
                    Map.entry("reference", "AO-2024-003"),
                    Map.entry("titre", "Audit de Sécurité"),
                    Map.entry("description", "Audit complet de sécurité informatique"),
                    Map.entry("dateCloture", "2024-07-20"),
                    Map.entry("statut", "EN_COURS_EVALUATION")
                )
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error("Error retrieving appels d'offres", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /soumissions/{soumissionId}/documents : Get documents of a submission
     * Retrieves all documents associated with a soumission.
     * Can optionally filter by appel d'offre ID.
     *
     * @param soumissionId the soumission ID
     * @param appelOffreId the optional appel d'offre ID for filtering
     * @return list of documents for the submission
     */
    @GetMapping("/soumissions/{soumissionId}/documents")
    public ResponseEntity<Map<String, Object>> getDocumentsSoumission(
        @PathVariable Long soumissionId,
        @RequestParam(required = false) Long appelOffreId
    ) {
        LOG.debug("REST request to get documents for soumission: {} with appelOffreId: {}", soumissionId, appelOffreId);

        try {
            Optional<SoumissionDTO> soumission = soumissionService.findOne(soumissionId);
            if (soumission.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("soumissionId", soumissionId);

            // Get documents - prepare for when documents relationship is fully available
            // For now, return mock documents with the expected structure
            List<Map<String, Object>> documents = List.of(
                Map.ofEntries(
                    Map.entry("id", 1L),
                    Map.entry("nom", "Offre_Technique_2024.pdf"),
                    Map.entry("format", "OFFRE_TECHNIQUE"),
                    Map.entry("url", "/documents/offre-tech-1.pdf"),
                    Map.entry("idExterne", "doc-001")
                ),
                Map.ofEntries(
                    Map.entry("id", 2L),
                    Map.entry("nom", "Attestation_Financiere.pdf"),
                    Map.entry("format", "ATTESTATION"),
                    Map.entry("url", "/documents/attestation-1.pdf"),
                    Map.entry("idExterne", "doc-002")
                ),
                Map.ofEntries(
                    Map.entry("id", 3L),
                    Map.entry("nom", "Garanties_Contract.pdf"),
                    Map.entry("format", "GARANTIE"),
                    Map.entry("url", "/documents/garantie-1.pdf"),
                    Map.entry("idExterne", "doc-003")
                ),
                Map.ofEntries(
                    Map.entry("id", 4L),
                    Map.entry("nom", "PV_Conformite.pdf"),
                    Map.entry("format", "PV_CONFORMITE"),
                    Map.entry("url", "/documents/pv-conformite-1.pdf"),
                    Map.entry("idExterne", "doc-004")
                )
            );

            response.put("documents", documents);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error("Error retrieving documents for soumission", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * POST /evaluations/lancer : Launch an AI evaluation for selected documents
     * Initiates an asynchronous AI evaluation process for the specified submission documents.
     * The evaluation request contains:
     * - soumissionId: ID of the submission
     * - appelOffreId: ID of the call for tender
     * - documentsIds: Array of document IDs to evaluate
     *
     * @param request the evaluation request containing soumissionId, appelOffreId, and documentsIds
     * @return response with evaluation status
     */
    @PostMapping("/evaluations/lancer")
    public ResponseEntity<Map<String, Object>> lancerEvaluationAi(@RequestBody Map<String, Object> request) {
        LOG.debug("REST request to launch AI evaluation: {}", request);

        try {
            if (!request.containsKey("soumissionId") || !request.containsKey("appelOffreId") || !request.containsKey("documentsIds")) {
                return ResponseEntity.badRequest()
                    .body(Map.of("status", "ERROR", "message", "Request must contain soumissionId, appelOffreId, and documentsIds"));
            }

            Long soumissionId = ((Number) request.get("soumissionId")).longValue();
            Long appelOffreId = ((Number) request.get("appelOffreId")).longValue();
            @SuppressWarnings("unchecked")
            List<Long> documentsIds = (List<Long>) request.get("documentsIds");

            // Perso

            // Validate soumission exists
            Optional<SoumissionDTO> soumission = soumissionService.findOne(soumissionId);
            if (soumission.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "Soumission not found"));
            }

            // Validate documents exist
            if (documentsIds == null || documentsIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "At least one document must be selected"));
            }

            // TODO: Implement actual evaluation launch when business logic is ready
            // This should trigger an asynchronous evaluation process
            // Steps:
            // 1. Create an Evaluation record in database
            // 2. Queue evaluation task for AI processing
            // 3. Send confirmation email to user
            // 4. Return evaluation ID and expected completion time

            // For now, return a mock success response
            Map<String, Object> response = new HashMap<>();
            response.put("evaluationId", System.nanoTime() % 1000000); // Mock ID
            response.put("soumissionId", soumissionId);
            response.put("appelOffreId", appelOffreId);
            response.put("documentsCount", documentsIds.size());
            response.put("status", "EN_COURS");
            response.put("message", "Évaluation lancée avec succès. Vous recevrez les résultats par email.");
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.accepted().body(response);
        } catch (ClassCastException e) {
            LOG.error("Invalid request format for AI evaluation", e);
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "Invalid request format"));
        } catch (Exception e) {
            LOG.error("Error launching AI evaluation", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("status", "ERROR", "message", "Erreur lors du lancement de l'évaluation: " + e.getMessage()));
        }
    }

    /**
     * GET /evaluations/{evaluationId}/status : Get status of an evaluation
     * Returns the current status and progress of an AI evaluation.
     * Status can be: EN_COURS, TERMINE, ERREUR, etc.
     *
     * @param evaluationId the evaluation ID
     * @return evaluation status information
     */
    @GetMapping("/evaluations/{evaluationId}/status")
    public ResponseEntity<Map<String, Object>> getEvaluationStatus(@PathVariable Long evaluationId) {
        LOG.debug("REST request to get status of evaluation: {}", evaluationId);

        try {
            Optional<EvaluationDTO> evaluation = evaluationService.findOne(evaluationId);
            if (evaluation.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            EvaluationDTO eval = evaluation.get();
            Map<String, Object> response = new HashMap<>();
            response.put("evaluationId", evaluationId);
            response.put("status", eval.getEstValidee() ? "TERMINE" : "EN_COURS");
            response.put("scoreGlobal", eval.getScoreGlobal());
            response.put("scoreAdmin", eval.getScoreAdmin());
            response.put("scoreTech", eval.getScoreTech());
            response.put("scoreFin", eval.getScoreFin());
            response.put("rapportAnalyse", eval.getRapportAnalyse());
            response.put("dateEvaluation", eval.getDateEvaluation());
            response.put("commentaire", eval.getCommentaireEvaluateur());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error("Error retrieving evaluation status", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /soumissionnaires : Get list of soumissionnaires (Admin only)
     * Retrieves the list of all soumissionnaires (budget suppliers) in the system.
     * This endpoint is restricted to ADMIN role for administrative purposes.
     *
     * @return list of soumissionnaires with basic information
     */
    @GetMapping("/soumissionnaires")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<Map<String, Object>>> getSoumissionnaires() {
        LOG.debug("REST request to get list of soumissionnaires");

        try {
            // TODO: Implement when Soumissionnaire entity/User relationship is defined
            // This should query the actual soumissionnaires from the database
            // For now, return mock data that demonstrates the structure

            List<Map<String, Object>> soumissionnaires = List.of(
                Map.ofEntries(
                    Map.entry("id", 1L),
                    Map.entry("nom", "Société ABC"),
                    Map.entry("siret", "12345678901234"),
                    Map.entry("email", "contact@abc-company.fr"),
                    Map.entry("statut", "ACTIVE")
                ),
                Map.ofEntries(
                    Map.entry("id", 2L),
                    Map.entry("nom", "Entreprise XYZ"),
                    Map.entry("siret", "98765432109876"),
                    Map.entry("email", "contact@xyz-corp.fr"),
                    Map.entry("statut", "ACTIVE")
                ),
                Map.ofEntries(
                    Map.entry("id", 3L),
                    Map.entry("nom", "Services Technologiques"),
                    Map.entry("siret", "11111111111111"),
                    Map.entry("email", "info@tech-services.fr"),
                    Map.entry("statut", "PENDING")
                )
            );

            return ResponseEntity.ok(soumissionnaires);
        } catch (Exception e) {
            LOG.error("Error retrieving soumissionnaires list", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/appels-offres")
    public ResponseEntity<List<AppelOffre>> getAppelsOffresDisponibles() {
        LOG.debug("REST request to get real AppelsOffres from database");

        try {
            // RÉEL : On récupère tous les appels d'offres
            // Si vous avez un Enum StatutAppel, vous pouvez filtrer :
            // List<AppelOffre> appels = appelOffreRepository.findByStatut(StatutAppel.OUVERT);

            List<AppelOffre> appels = appelOffreRepository.findAll();

            return ResponseEntity.ok(appels);
        } catch (Exception e) {
            LOG.error("Error retrieving real appels d'offres", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
