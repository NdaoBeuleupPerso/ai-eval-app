package com.mycompany.iaeval.web.rest;

import com.mycompany.iaeval.repository.CandidatRepository;
import com.mycompany.iaeval.service.CandidatQueryService;
import com.mycompany.iaeval.service.CandidatService;
import com.mycompany.iaeval.service.criteria.CandidatCriteria;
import com.mycompany.iaeval.service.dto.CandidatDTO;
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
 * REST controller for managing {@link com.mycompany.iaeval.domain.Candidat}.
 */
@RestController
@RequestMapping("/api/candidats")
public class CandidatResource {

    private static final Logger LOG = LoggerFactory.getLogger(CandidatResource.class);

    private static final String ENTITY_NAME = "candidat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CandidatService candidatService;

    private final CandidatRepository candidatRepository;

    private final CandidatQueryService candidatQueryService;

    public CandidatResource(
        CandidatService candidatService,
        CandidatRepository candidatRepository,
        CandidatQueryService candidatQueryService
    ) {
        this.candidatService = candidatService;
        this.candidatRepository = candidatRepository;
        this.candidatQueryService = candidatQueryService;
    }

    /**
     * {@code POST  /candidats} : Create a new candidat.
     *
     * @param candidatDTO the candidatDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new candidatDTO, or with status {@code 400 (Bad Request)} if the candidat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CandidatDTO> createCandidat(@Valid @RequestBody CandidatDTO candidatDTO) throws URISyntaxException {
        LOG.debug("REST request to save Candidat : {}", candidatDTO);
        if (candidatDTO.getId() != null) {
            throw new BadRequestAlertException("A new candidat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        candidatDTO = candidatService.save(candidatDTO);
        return ResponseEntity.created(new URI("/api/candidats/" + candidatDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, candidatDTO.getId().toString()))
            .body(candidatDTO);
    }

    /**
     * {@code PUT  /candidats/:id} : Updates an existing candidat.
     *
     * @param id the id of the candidatDTO to save.
     * @param candidatDTO the candidatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated candidatDTO,
     * or with status {@code 400 (Bad Request)} if the candidatDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the candidatDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CandidatDTO> updateCandidat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CandidatDTO candidatDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Candidat : {}, {}", id, candidatDTO);
        if (candidatDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, candidatDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!candidatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        candidatDTO = candidatService.update(candidatDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, candidatDTO.getId().toString()))
            .body(candidatDTO);
    }

    /**
     * {@code PATCH  /candidats/:id} : Partial updates given fields of an existing candidat, field will ignore if it is null
     *
     * @param id the id of the candidatDTO to save.
     * @param candidatDTO the candidatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated candidatDTO,
     * or with status {@code 400 (Bad Request)} if the candidatDTO is not valid,
     * or with status {@code 404 (Not Found)} if the candidatDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the candidatDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CandidatDTO> partialUpdateCandidat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CandidatDTO candidatDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Candidat partially : {}, {}", id, candidatDTO);
        if (candidatDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, candidatDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!candidatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CandidatDTO> result = candidatService.partialUpdate(candidatDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, candidatDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /candidats} : get all the candidats.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of candidats in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CandidatDTO>> getAllCandidats(
        CandidatCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Candidats by criteria: {}", criteria);

        Page<CandidatDTO> page = candidatQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /candidats/count} : count all the candidats.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCandidats(CandidatCriteria criteria) {
        LOG.debug("REST request to count Candidats by criteria: {}", criteria);
        return ResponseEntity.ok().body(candidatQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /candidats/:id} : get the "id" candidat.
     *
     * @param id the id of the candidatDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the candidatDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CandidatDTO> getCandidat(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Candidat : {}", id);
        Optional<CandidatDTO> candidatDTO = candidatService.findOne(id);
        return ResponseUtil.wrapOrNotFound(candidatDTO);
    }

    /**
     * {@code DELETE  /candidats/:id} : delete the "id" candidat.
     *
     * @param id the id of the candidatDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidat(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Candidat : {}", id);
        candidatService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
