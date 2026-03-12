package com.mycompany.iaeval.web.rest;

import com.mycompany.iaeval.repository.TraceAuditRepository;
import com.mycompany.iaeval.service.TraceAuditQueryService;
import com.mycompany.iaeval.service.TraceAuditService;
import com.mycompany.iaeval.service.criteria.TraceAuditCriteria;
import com.mycompany.iaeval.service.dto.TraceAuditDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.iaeval.domain.TraceAudit}.
 */
@RestController
@RequestMapping("/api/trace-audits")
public class TraceAuditResource {

    private static final Logger LOG = LoggerFactory.getLogger(TraceAuditResource.class);

    private static final String ENTITY_NAME = "traceAudit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TraceAuditService traceAuditService;

    private final TraceAuditRepository traceAuditRepository;

    private final TraceAuditQueryService traceAuditQueryService;

    public TraceAuditResource(
        TraceAuditService traceAuditService,
        TraceAuditRepository traceAuditRepository,
        TraceAuditQueryService traceAuditQueryService
    ) {
        this.traceAuditService = traceAuditService;
        this.traceAuditRepository = traceAuditRepository;
        this.traceAuditQueryService = traceAuditQueryService;
    }

    /**
     * {@code POST  /trace-audits} : Create a new traceAudit.
     *
     * @param traceAuditDTO the traceAuditDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new traceAuditDTO, or with status {@code 400 (Bad Request)} if the traceAudit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TraceAuditDTO> createTraceAudit(@Valid @RequestBody TraceAuditDTO traceAuditDTO) throws URISyntaxException {
        LOG.debug("REST request to save TraceAudit : {}", traceAuditDTO);
        if (traceAuditDTO.getId() != null) {
            throw new BadRequestAlertException("A new traceAudit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        traceAuditDTO = traceAuditService.save(traceAuditDTO);
        return ResponseEntity.created(new URI("/api/trace-audits/" + traceAuditDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, traceAuditDTO.getId().toString()))
            .body(traceAuditDTO);
    }

    /**
     * {@code PUT  /trace-audits/:id} : Updates an existing traceAudit.
     *
     * @param id the id of the traceAuditDTO to save.
     * @param traceAuditDTO the traceAuditDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated traceAuditDTO,
     * or with status {@code 400 (Bad Request)} if the traceAuditDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the traceAuditDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TraceAuditDTO> updateTraceAudit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TraceAuditDTO traceAuditDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TraceAudit : {}, {}", id, traceAuditDTO);
        if (traceAuditDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, traceAuditDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!traceAuditRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        traceAuditDTO = traceAuditService.update(traceAuditDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, traceAuditDTO.getId().toString()))
            .body(traceAuditDTO);
    }

    /**
     * {@code PATCH  /trace-audits/:id} : Partial updates given fields of an existing traceAudit, field will ignore if it is null
     *
     * @param id the id of the traceAuditDTO to save.
     * @param traceAuditDTO the traceAuditDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated traceAuditDTO,
     * or with status {@code 400 (Bad Request)} if the traceAuditDTO is not valid,
     * or with status {@code 404 (Not Found)} if the traceAuditDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the traceAuditDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TraceAuditDTO> partialUpdateTraceAudit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TraceAuditDTO traceAuditDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TraceAudit partially : {}, {}", id, traceAuditDTO);
        if (traceAuditDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, traceAuditDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!traceAuditRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TraceAuditDTO> result = traceAuditService.partialUpdate(traceAuditDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, traceAuditDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /trace-audits} : get all the traceAudits.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of traceAudits in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TraceAuditDTO>> getAllTraceAudits(
        TraceAuditCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TraceAudits by criteria: {}", criteria);

        Page<TraceAuditDTO> page = traceAuditQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /trace-audits/count} : count all the traceAudits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTraceAudits(TraceAuditCriteria criteria) {
        LOG.debug("REST request to count TraceAudits by criteria: {}", criteria);
        return ResponseEntity.ok().body(traceAuditQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /trace-audits/:id} : get the "id" traceAudit.
     *
     * @param id the id of the traceAuditDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the traceAuditDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TraceAuditDTO> getTraceAudit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TraceAudit : {}", id);
        Optional<TraceAuditDTO> traceAuditDTO = traceAuditService.findOne(id);
        return ResponseUtil.wrapOrNotFound(traceAuditDTO);
    }

    /**
     * {@code DELETE  /trace-audits/:id} : delete the "id" traceAudit.
     *
     * @param id the id of the traceAuditDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraceAudit(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TraceAudit : {}", id);
        traceAuditService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
