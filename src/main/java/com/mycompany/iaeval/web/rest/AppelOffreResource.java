package com.mycompany.iaeval.web.rest;

import com.mycompany.iaeval.repository.AppelOffreRepository;
import com.mycompany.iaeval.service.AppelOffreQueryService;
import com.mycompany.iaeval.service.AppelOffreService;
import com.mycompany.iaeval.service.EvaluationService;
import com.mycompany.iaeval.service.criteria.AppelOffreCriteria;
import com.mycompany.iaeval.service.dto.AppelOffreDTO;
import com.mycompany.iaeval.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.iaeval.domain.AppelOffre}.
 */
@RestController
@RequestMapping("/api/appel-offres")
public class AppelOffreResource {

    private static final Logger LOG = LoggerFactory.getLogger(AppelOffreResource.class);

    private static final String ENTITY_NAME = "appelOffre";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppelOffreService appelOffreService;

    private final AppelOffreRepository appelOffreRepository;

    private final AppelOffreQueryService appelOffreQueryService;
    private final EvaluationService evaluationService;

    public AppelOffreResource(
        AppelOffreService appelOffreService,
        AppelOffreRepository appelOffreRepository,
        AppelOffreQueryService appelOffreQueryService,
        EvaluationService evaluationService
    ) {
        this.appelOffreService = appelOffreService;
        this.appelOffreRepository = appelOffreRepository;
        this.appelOffreQueryService = appelOffreQueryService;
        this.evaluationService = evaluationService;
    }

    /**
     * {@code POST  /appel-offres} : Create a new appelOffre.
     *
     * @param appelOffreDTO the appelOffreDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appelOffreDTO, or with status {@code 400 (Bad Request)} if the appelOffre has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AppelOffreDTO> createAppelOffre(@Valid @RequestBody AppelOffreDTO appelOffreDTO) throws URISyntaxException {
        LOG.debug("REST request to save AppelOffre : {}", appelOffreDTO);
        if (appelOffreDTO.getId() != null) {
            throw new BadRequestAlertException("A new appelOffre cannot already have an ID", ENTITY_NAME, "idexists");
        }
        appelOffreDTO = appelOffreService.save(appelOffreDTO);
        return ResponseEntity.created(new URI("/api/appel-offres/" + appelOffreDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, appelOffreDTO.getId().toString()))
            .body(appelOffreDTO);
    }

    @PostMapping("/{id}/evaluer-tout")
    public ResponseEntity<Void> lancerEvaluationGlobale(@PathVariable Long id) {
        LOG.debug("Lancement de l'évaluation globale pour l'Appel d'Offre : {}", id);

        // On délègue au service le traitement lourd
        evaluationService.evaluerToutAppel(id);

        return ResponseEntity.accepted().build(); // 202 Accepted car le traitement peut être long
    }

    /**
     * {@code PUT  /appel-offres/:id} : Updates an existing appelOffre.
     *
     * @param id the id of the appelOffreDTO to save.
     * @param appelOffreDTO the appelOffreDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appelOffreDTO,
     * or with status {@code 400 (Bad Request)} if the appelOffreDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appelOffreDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AppelOffreDTO> updateAppelOffre(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AppelOffreDTO appelOffreDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AppelOffre : {}, {}", id, appelOffreDTO);
        if (appelOffreDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appelOffreDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!appelOffreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        appelOffreDTO = appelOffreService.update(appelOffreDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appelOffreDTO.getId().toString()))
            .body(appelOffreDTO);
    }

    /**
     * {@code PATCH  /appel-offres/:id} : Partial updates given fields of an existing appelOffre, field will ignore if it is null
     *
     * @param id the id of the appelOffreDTO to save.
     * @param appelOffreDTO the appelOffreDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appelOffreDTO,
     * or with status {@code 400 (Bad Request)} if the appelOffreDTO is not valid,
     * or with status {@code 404 (Not Found)} if the appelOffreDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the appelOffreDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AppelOffreDTO> partialUpdateAppelOffre(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AppelOffreDTO appelOffreDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AppelOffre partially : {}, {}", id, appelOffreDTO);
        if (appelOffreDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appelOffreDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!appelOffreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AppelOffreDTO> result = appelOffreService.partialUpdate(appelOffreDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appelOffreDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /appel-offres} : get all the appelOffres.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appelOffres in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AppelOffreDTO>> getAllAppelOffres(
        AppelOffreCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get AppelOffres by criteria: {}", criteria);

        Page<AppelOffreDTO> page = appelOffreQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /appel-offres/count} : count all the appelOffres.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAppelOffres(AppelOffreCriteria criteria) {
        LOG.debug("REST request to count AppelOffres by criteria: {}", criteria);
        return ResponseEntity.ok().body(appelOffreQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /appel-offres/:id} : get the "id" appelOffre.
     *
     * @param id the id of the appelOffreDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appelOffreDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppelOffreDTO> getAppelOffre(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AppelOffre : {}", id);
        Optional<AppelOffreDTO> appelOffreDTO = appelOffreService.findOne(id);
        return ResponseUtil.wrapOrNotFound(appelOffreDTO);
    }

    /**
     * {@code DELETE  /appel-offres/:id} : delete the "id" appelOffre.
     *
     * @param id the id of the appelOffreDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppelOffre(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AppelOffre : {}", id);
        appelOffreService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
