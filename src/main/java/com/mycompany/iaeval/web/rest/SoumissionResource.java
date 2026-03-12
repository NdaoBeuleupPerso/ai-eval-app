package com.mycompany.iaeval.web.rest;

import com.mycompany.iaeval.repository.SoumissionRepository;
import com.mycompany.iaeval.service.SoumissionQueryService;
import com.mycompany.iaeval.service.SoumissionService;
import com.mycompany.iaeval.service.criteria.SoumissionCriteria;
import com.mycompany.iaeval.service.dto.SoumissionDTO;
import com.mycompany.iaeval.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.mycompany.iaeval.domain.Soumission}.
 */
@RestController
@RequestMapping("/api/soumissions")
public class SoumissionResource {

    private static final Logger LOG = LoggerFactory.getLogger(SoumissionResource.class);

    private static final String ENTITY_NAME = "soumission";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SoumissionService soumissionService;

    private final SoumissionRepository soumissionRepository;

    private final SoumissionQueryService soumissionQueryService;

    public SoumissionResource(
        SoumissionService soumissionService,
        SoumissionRepository soumissionRepository,
        SoumissionQueryService soumissionQueryService
    ) {
        this.soumissionService = soumissionService;
        this.soumissionRepository = soumissionRepository;
        this.soumissionQueryService = soumissionQueryService;
    }

    /**
     * {@code POST  /soumissions} : Create a new soumission.
     *
     * @param soumissionDTO the soumissionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new soumissionDTO, or with status {@code 400 (Bad Request)} if the soumission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SoumissionDTO> createSoumission(@RequestBody SoumissionDTO soumissionDTO) throws URISyntaxException {
        LOG.debug("REST request to save Soumission : {}", soumissionDTO);
        if (soumissionDTO.getId() != null) {
            throw new BadRequestAlertException("A new soumission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        soumissionDTO = soumissionService.save(soumissionDTO);
        return ResponseEntity.created(new URI("/api/soumissions/" + soumissionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, soumissionDTO.getId().toString()))
            .body(soumissionDTO);
    }

    /**
     * {@code PUT  /soumissions/:id} : Updates an existing soumission.
     *
     * @param id the id of the soumissionDTO to save.
     * @param soumissionDTO the soumissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated soumissionDTO,
     * or with status {@code 400 (Bad Request)} if the soumissionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the soumissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SoumissionDTO> updateSoumission(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SoumissionDTO soumissionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Soumission : {}, {}", id, soumissionDTO);
        if (soumissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, soumissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!soumissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        soumissionDTO = soumissionService.update(soumissionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, soumissionDTO.getId().toString()))
            .body(soumissionDTO);
    }

    /**
     * {@code PATCH  /soumissions/:id} : Partial updates given fields of an existing soumission, field will ignore if it is null
     *
     * @param id the id of the soumissionDTO to save.
     * @param soumissionDTO the soumissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated soumissionDTO,
     * or with status {@code 400 (Bad Request)} if the soumissionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the soumissionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the soumissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SoumissionDTO> partialUpdateSoumission(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SoumissionDTO soumissionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Soumission partially : {}, {}", id, soumissionDTO);
        if (soumissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, soumissionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!soumissionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SoumissionDTO> result = soumissionService.partialUpdate(soumissionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, soumissionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /soumissions} : get all the soumissions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of soumissions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SoumissionDTO>> getAllSoumissions(
        SoumissionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Soumissions by criteria: {}", criteria);

        Page<SoumissionDTO> page = soumissionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /soumissions/count} : count all the soumissions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSoumissions(SoumissionCriteria criteria) {
        LOG.debug("REST request to count Soumissions by criteria: {}", criteria);
        return ResponseEntity.ok().body(soumissionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /soumissions/:id} : get the "id" soumission.
     *
     * @param id the id of the soumissionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the soumissionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SoumissionDTO> getSoumission(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Soumission : {}", id);
        Optional<SoumissionDTO> soumissionDTO = soumissionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(soumissionDTO);
    }

    /**
     * {@code DELETE  /soumissions/:id} : delete the "id" soumission.
     *
     * @param id the id of the soumissionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSoumission(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Soumission : {}", id);
        soumissionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
