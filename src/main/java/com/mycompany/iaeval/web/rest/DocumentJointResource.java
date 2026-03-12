package com.mycompany.iaeval.web.rest;

import com.mycompany.iaeval.repository.DocumentJointRepository;
import com.mycompany.iaeval.service.DocumentJointService;
import com.mycompany.iaeval.service.dto.DocumentJointDTO;
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
 * REST controller for managing {@link com.mycompany.iaeval.domain.DocumentJoint}.
 */
@RestController
@RequestMapping("/api/document-joints")
public class DocumentJointResource {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentJointResource.class);

    private static final String ENTITY_NAME = "documentJoint";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DocumentJointService documentJointService;

    private final DocumentJointRepository documentJointRepository;

    public DocumentJointResource(DocumentJointService documentJointService, DocumentJointRepository documentJointRepository) {
        this.documentJointService = documentJointService;
        this.documentJointRepository = documentJointRepository;
    }

    /**
     * {@code POST  /document-joints} : Create a new documentJoint.
     *
     * @param documentJointDTO the documentJointDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new documentJointDTO, or with status {@code 400 (Bad Request)} if the documentJoint has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DocumentJointDTO> createDocumentJoint(@Valid @RequestBody DocumentJointDTO documentJointDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DocumentJoint : {}", documentJointDTO);
        if (documentJointDTO.getId() != null) {
            throw new BadRequestAlertException("A new documentJoint cannot already have an ID", ENTITY_NAME, "idexists");
        }
        documentJointDTO = documentJointService.save(documentJointDTO);
        return ResponseEntity.created(new URI("/api/document-joints/" + documentJointDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, documentJointDTO.getId().toString()))
            .body(documentJointDTO);
    }

    /**
     * {@code PUT  /document-joints/:id} : Updates an existing documentJoint.
     *
     * @param id the id of the documentJointDTO to save.
     * @param documentJointDTO the documentJointDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated documentJointDTO,
     * or with status {@code 400 (Bad Request)} if the documentJointDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the documentJointDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DocumentJointDTO> updateDocumentJoint(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DocumentJointDTO documentJointDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DocumentJoint : {}, {}", id, documentJointDTO);
        if (documentJointDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, documentJointDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!documentJointRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        documentJointDTO = documentJointService.update(documentJointDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, documentJointDTO.getId().toString()))
            .body(documentJointDTO);
    }

    /**
     * {@code PATCH  /document-joints/:id} : Partial updates given fields of an existing documentJoint, field will ignore if it is null
     *
     * @param id the id of the documentJointDTO to save.
     * @param documentJointDTO the documentJointDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated documentJointDTO,
     * or with status {@code 400 (Bad Request)} if the documentJointDTO is not valid,
     * or with status {@code 404 (Not Found)} if the documentJointDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the documentJointDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DocumentJointDTO> partialUpdateDocumentJoint(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DocumentJointDTO documentJointDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DocumentJoint partially : {}, {}", id, documentJointDTO);
        if (documentJointDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, documentJointDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!documentJointRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DocumentJointDTO> result = documentJointService.partialUpdate(documentJointDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, documentJointDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /document-joints} : get all the documentJoints.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of documentJoints in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DocumentJointDTO>> getAllDocumentJoints(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of DocumentJoints");
        Page<DocumentJointDTO> page = documentJointService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /document-joints/:id} : get the "id" documentJoint.
     *
     * @param id the id of the documentJointDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the documentJointDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentJointDTO> getDocumentJoint(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DocumentJoint : {}", id);
        Optional<DocumentJointDTO> documentJointDTO = documentJointService.findOne(id);
        return ResponseUtil.wrapOrNotFound(documentJointDTO);
    }

    /**
     * {@code DELETE  /document-joints/:id} : delete the "id" documentJoint.
     *
     * @param id the id of the documentJointDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentJoint(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DocumentJoint : {}", id);
        documentJointService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
