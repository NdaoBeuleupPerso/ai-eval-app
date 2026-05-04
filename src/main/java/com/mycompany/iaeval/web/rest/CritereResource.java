package com.mycompany.iaeval.web.rest;

import com.mycompany.iaeval.repository.CritereRepository;
import com.mycompany.iaeval.service.CritereService;
import com.mycompany.iaeval.service.dto.CritereDTO;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.iaeval.domain.Critere}.
 */
@RestController
@RequestMapping("/api/criteres")
public class CritereResource {

    private static final Logger LOG = LoggerFactory.getLogger(CritereResource.class);

    private static final String ENTITY_NAME = "critere";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CritereService critereService;

    private final CritereRepository critereRepository;

    public CritereResource(CritereService critereService, CritereRepository critereRepository) {
        this.critereService = critereService;
        this.critereRepository = critereRepository;
    }

    /**
     * {@code POST  /criteres} : Create a new critere.
     *
     * @param critereDTO the critereDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new
     *         critereDTO, or with status {@code 400 (Bad Request)} if the critere has already an
     *         ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CritereDTO> createCritere(@Valid @RequestBody CritereDTO critereDTO) throws URISyntaxException {
        LOG.debug("REST request to save Critere : {}", critereDTO);
        if (critereDTO.getId() != null) {
            throw new BadRequestAlertException("A new critere cannot already have an ID", ENTITY_NAME, "idexists");
        }
        critereDTO = critereService.save(critereDTO);
        return ResponseEntity.created(new URI("/api/criteres/" + critereDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, critereDTO.getId().toString()))
            .body(critereDTO);
    }

    @PostMapping("/suggestions/{appelOffreId}")
    public ResponseEntity<List<CritereDTO>> getSuggestions(@PathVariable Long appelOffreId) {
        LOG.debug("REST request to generate criteria suggestions for AppelOffre : {}", appelOffreId);
        List<CritereDTO> result = critereService.genererCriteresSuggestions(appelOffreId);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code PUT  /criteres/:id} : Updates an existing critere.
     *
     * @param id the id of the critereDTO to save.
     * @param critereDTO the critereDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated
     *         critereDTO, or with status {@code 400 (Bad Request)} if the critereDTO is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the critereDTO couldn't be
     *         updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CritereDTO> updateCritere(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CritereDTO critereDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Critere : {}, {}", id, critereDTO);
        if (critereDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, critereDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!critereRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        critereDTO = critereService.update(critereDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, critereDTO.getId().toString()))
            .body(critereDTO);
    }

    /**
     * {@code PATCH  /criteres/:id} : Partial updates given fields of an existing critere, field
     * will ignore if it is null
     *
     * @param id the id of the critereDTO to save.
     * @param critereDTO the critereDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated
     *         critereDTO, or with status {@code 400 (Bad Request)} if the critereDTO is not valid,
     *         or with status {@code 404 (Not Found)} if the critereDTO is not found, or with status
     *         {@code 500 (Internal Server Error)} if the critereDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CritereDTO> partialUpdateCritere(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CritereDTO critereDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Critere partially : {}, {}", id, critereDTO);
        if (critereDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, critereDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!critereRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CritereDTO> result = critereService.partialUpdate(critereDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, critereDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /criteres} : get all the criteres.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for
     *        many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of criteres in
     *         body.
     */
    @GetMapping("")
    public ResponseEntity<List<CritereDTO>> getAllCriteres(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Criteres");
        Page<CritereDTO> page;
        if (eagerload) {
            page = critereService.findAllWithEagerRelationships(pageable);
        } else {
            page = critereService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /criteres/:id} : get the "id" critere.
     *
     * @param id the id of the critereDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the critereDTO,
     *         or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CritereDTO> getCritere(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Critere : {}", id);
        Optional<CritereDTO> critereDTO = critereService.findOne(id);
        return ResponseUtil.wrapOrNotFound(critereDTO);
    }

    /**
     * {@code DELETE  /criteres/:id} : delete the "id" critere.
     *
     * @param id the id of the critereDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCritere(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Critere : {}", id);
        critereService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
