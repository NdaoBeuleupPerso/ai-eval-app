package com.mycompany.iaeval.web.rest;

import com.mycompany.iaeval.repository.EvaluationRepository;
import com.mycompany.iaeval.service.EvaluationService;
import com.mycompany.iaeval.service.dto.EvaluationDTO;
import com.mycompany.iaeval.service.dto.SoumissionDTO;
import com.mycompany.iaeval.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.iaeval.domain.Evaluation}.
 */
@RestController
@RequestMapping("/api/evaluations")
public class EvaluationResource {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationResource.class);

    private static final String ENTITY_NAME = "evaluation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EvaluationService evaluationService;

    private final EvaluationRepository evaluationRepository;

    public EvaluationResource(EvaluationService evaluationService, EvaluationRepository evaluationRepository) {
        this.evaluationService = evaluationService;
        this.evaluationRepository = evaluationRepository;
    }

    /**
     * {@code POST  /evaluations} : Create a new evaluation.
     *
     * @param soumissionDTO the SoumissionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new evaluationDTO, or with status {@code 400 (Bad Request)} if the evaluation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EvaluationDTO> createEvaluation(@RequestBody SoumissionDTO soumissionDTO) throws URISyntaxException {
        LOG.debug("REST request to start AI Evaluation : {}", soumissionDTO);
        if (soumissionDTO.getId() != null) {
            throw new BadRequestAlertException("A new evaluation cannot already have an ID", ENTITY_NAME, "idexists");
        }

        // L'intelligence est ici : le service va maintenant appeler Mistral et Qdrant
        // avant de retourner l'objet complété avec les scores et le rapport.
        EvaluationDTO evaluationDTO = evaluationService.evaluerByAIAgent(soumissionDTO);

        return ResponseEntity.created(new URI("/api/evaluations/" + evaluationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, evaluationDTO.getId().toString()))
            .body(evaluationDTO);
    }

    /**
     * {@code PUT  /evaluations/:id} : Updates an existing evaluation.
     *
     * @param id the id of the evaluationDTO to save.
     * @param evaluationDTO the evaluationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated evaluationDTO,
     * or with status {@code 400 (Bad Request)} if the evaluationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the evaluationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EvaluationDTO> updateEvaluation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EvaluationDTO evaluationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Evaluation : {}, {}", id, evaluationDTO);
        if (evaluationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, evaluationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!evaluationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        evaluationDTO = evaluationService.update(evaluationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, evaluationDTO.getId().toString()))
            .body(evaluationDTO);
    }

    /**
     * {@code PATCH  /evaluations/:id} : Partial updates given fields of an existing evaluation, field will ignore if it is null
     *
     * @param id the id of the evaluationDTO to save.
     * @param evaluationDTO the evaluationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated evaluationDTO,
     * or with status {@code 400 (Bad Request)} if the evaluationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the evaluationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the evaluationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EvaluationDTO> partialUpdateEvaluation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EvaluationDTO evaluationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Evaluation partially : {}, {}", id, evaluationDTO);
        if (evaluationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, evaluationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!evaluationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EvaluationDTO> result = evaluationService.partialUpdate(evaluationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, evaluationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /evaluations} : get all the evaluations.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of evaluations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<EvaluationDTO>> getAllEvaluations(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "filter", required = false) String filter,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        if ("soumission-is-null".equals(filter)) {
            LOG.debug("REST request to get all Evaluations where soumission is null");
            return new ResponseEntity<>(evaluationService.findAllWhereSoumissionIsNull(), HttpStatus.OK);
        }
        LOG.debug("REST request to get a page of Evaluations");
        Page<EvaluationDTO> page;
        if (eagerload) {
            page = evaluationService.findAllWithEagerRelationships(pageable);
        } else {
            page = evaluationService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /evaluations/:id} : get the "id" evaluation.
     *
     * @param id the id of the evaluationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the evaluationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EvaluationDTO> getEvaluation(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Evaluation : {}", id);
        Optional<EvaluationDTO> evaluationDTO = evaluationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(evaluationDTO);
    }

    @GetMapping("/appel-offre/{id}")
    public ResponseEntity<List<EvaluationDTO>> getEvaluationsByAppelOffre(@PathVariable Long id) {
        LOG.debug("REST request to get Evaluations for AppelOffre : {}", id);
        List<EvaluationDTO> result = evaluationService.findAllByAppelOffre(id);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code DELETE  /evaluations/:id} : delete the "id" evaluation.
     *
     * @param id the id of the evaluationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Evaluation : {}", id);
        evaluationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * POST  /evaluations/:id/valider : Validation humaine d'une analyse IA.
     * Cette méthode déclenche la vectorisation dans Qdrant pour l'apprentissage.
     *
     * @param id l'id de l'évaluation à valider.
     * @param commentaire le commentaire de l'évaluateur (passé en simple texte ou objet).
     * @return l'EvaluationDTO mise à jour.
     */
    @PostMapping("/{id}/valider")
    public ResponseEntity<EvaluationDTO> validerEvaluation(@PathVariable(value = "id") final Long id, @RequestBody String commentaire) {
        LOG.debug("REST request to validate AI Evaluation : {}", id);

        // On appelle la méthode du service que nous avons définie pour l'apprentissage
        EvaluationDTO result = evaluationService.validerEvaluation(id, commentaire);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }

    /**
     * GET /evaluations/a-valider : Récupère les évaluations en attente de validation humaine.
     */
    @GetMapping("/a-valider")
    public ResponseEntity<List<EvaluationDTO>> getEvaluationsAValider(Pageable pageable) {
        LOG.debug("REST request to get Evaluations pending validation");
        // Logique à ajouter dans le service : findAllByEstValideeFalse
        Page<EvaluationDTO> page = evaluationService.findAllPendingValidation(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping("/evaluations/appel-offre/{id}/generate-pv")
    public ResponseEntity<Map<String, String>> generateFinalPV(@PathVariable Long id) {
        LOG.debug("REST request to generate final PV for AppelOffre : {}", id);
        String pvContent = evaluationService.genererPVSynthese(id);

        // On renvoie un objet JSON contenant le texte du PV
        return ResponseEntity.ok(Map.of("content", pvContent));
    }
}
