package com.mycompany.iaeval.web.rest;

import com.mycompany.iaeval.repository.ReferenceLegaleRepository;
import com.mycompany.iaeval.service.ReferenceLegaleService;
import com.mycompany.iaeval.service.dto.ReferenceLegaleDTO;
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
 * REST controller for managing {@link com.mycompany.iaeval.domain.ReferenceLegale}.
 */
@RestController
@RequestMapping("/api/reference-legales")
public class ReferenceLegaleResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReferenceLegaleResource.class);

    private static final String ENTITY_NAME = "referenceLegale";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReferenceLegaleService referenceLegaleService;

    private final ReferenceLegaleRepository referenceLegaleRepository;

    public ReferenceLegaleResource(ReferenceLegaleService referenceLegaleService, ReferenceLegaleRepository referenceLegaleRepository) {
        this.referenceLegaleService = referenceLegaleService;
        this.referenceLegaleRepository = referenceLegaleRepository;
    }

    /**
     * {@code POST  /reference-legales} : Create a new referenceLegale.
     *
     * @param referenceLegaleDTO the referenceLegaleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new referenceLegaleDTO, or with status {@code 400 (Bad Request)} if the referenceLegale has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReferenceLegaleDTO> createReferenceLegale(@Valid @RequestBody ReferenceLegaleDTO referenceLegaleDTO)
        throws URISyntaxException {
        if (referenceLegaleDTO.getId() != null) {
            throw new BadRequestAlertException("A new referenceLegale cannot already have an ID", ENTITY_NAME, "idexists");
        }
        int docLen = referenceLegaleDTO.getDocument() != null ? referenceLegaleDTO.getDocument().length : 0;
        int contenuLen = referenceLegaleDTO.getContenu() != null ? referenceLegaleDTO.getContenu().length() : 0;
        LOG.warn(
            "[reference-legale] POST titre={} typeSource={} documentBytes={} contenuChars={}",
            referenceLegaleDTO.getTitre(),
            referenceLegaleDTO.getTypeSource(),
            docLen,
            contenuLen
        );
        referenceLegaleDTO = referenceLegaleService.save(referenceLegaleDTO);
        return ResponseEntity.created(new URI("/api/reference-legales/" + referenceLegaleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, referenceLegaleDTO.getId().toString()))
            .body(referenceLegaleDTO);
    }

    /**
     * {@code PUT  /reference-legales/:id} : Updates an existing referenceLegale.
     *
     * @param id the id of the referenceLegaleDTO to save.
     * @param referenceLegaleDTO the referenceLegaleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated referenceLegaleDTO,
     * or with status {@code 400 (Bad Request)} if the referenceLegaleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the referenceLegaleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReferenceLegaleDTO> updateReferenceLegale(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReferenceLegaleDTO referenceLegaleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ReferenceLegale : {}, {}", id, referenceLegaleDTO);
        if (referenceLegaleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, referenceLegaleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!referenceLegaleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        referenceLegaleDTO = referenceLegaleService.update(referenceLegaleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, referenceLegaleDTO.getId().toString()))
            .body(referenceLegaleDTO);
    }

    /**
     * {@code PATCH  /reference-legales/:id} : Partial updates given fields of an existing referenceLegale, field will ignore if it is null
     *
     * @param id the id of the referenceLegaleDTO to save.
     * @param referenceLegaleDTO the referenceLegaleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated referenceLegaleDTO,
     * or with status {@code 400 (Bad Request)} if the referenceLegaleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the referenceLegaleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the referenceLegaleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReferenceLegaleDTO> partialUpdateReferenceLegale(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReferenceLegaleDTO referenceLegaleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ReferenceLegale partially : {}, {}", id, referenceLegaleDTO);
        if (referenceLegaleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, referenceLegaleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!referenceLegaleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReferenceLegaleDTO> result = referenceLegaleService.partialUpdate(referenceLegaleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, referenceLegaleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /reference-legales} : get all the referenceLegales.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of referenceLegales in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ReferenceLegaleDTO>> getAllReferenceLegales(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ReferenceLegales");
        Page<ReferenceLegaleDTO> page = referenceLegaleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /reference-legales/:id} : get the "id" referenceLegale.
     *
     * @param id the id of the referenceLegaleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the referenceLegaleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReferenceLegaleDTO> getReferenceLegale(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ReferenceLegale : {}", id);
        Optional<ReferenceLegaleDTO> referenceLegaleDTO = referenceLegaleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(referenceLegaleDTO);
    }

    /**
     * {@code DELETE  /reference-legales/:id} : delete the "id" referenceLegale.
     *
     * @param id the id of the referenceLegaleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReferenceLegale(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ReferenceLegale : {}", id);
        referenceLegaleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
