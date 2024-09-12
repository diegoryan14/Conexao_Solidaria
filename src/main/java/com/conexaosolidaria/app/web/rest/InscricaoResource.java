package com.conexaosolidaria.app.web.rest;

import com.conexaosolidaria.app.domain.Inscricao;
import com.conexaosolidaria.app.repository.InscricaoRepository;
import com.conexaosolidaria.app.service.InscricaoQueryService;
import com.conexaosolidaria.app.service.InscricaoService;
import com.conexaosolidaria.app.service.criteria.InscricaoCriteria;
import com.conexaosolidaria.app.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.conexaosolidaria.app.domain.Inscricao}.
 */
@RestController
@RequestMapping("/api/inscricaos")
public class InscricaoResource {

    private final Logger log = LoggerFactory.getLogger(InscricaoResource.class);

    private static final String ENTITY_NAME = "inscricao";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InscricaoService inscricaoService;

    private final InscricaoRepository inscricaoRepository;

    private final InscricaoQueryService inscricaoQueryService;

    public InscricaoResource(
        InscricaoService inscricaoService,
        InscricaoRepository inscricaoRepository,
        InscricaoQueryService inscricaoQueryService
    ) {
        this.inscricaoService = inscricaoService;
        this.inscricaoRepository = inscricaoRepository;
        this.inscricaoQueryService = inscricaoQueryService;
    }

    /**
     * {@code POST  /inscricaos} : Create a new inscricao.
     *
     * @param inscricao the inscricao to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new inscricao, or with status {@code 400 (Bad Request)} if the inscricao has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Inscricao> createInscricao(@RequestBody Inscricao inscricao) throws URISyntaxException {
        log.debug("REST request to save Inscricao : {}", inscricao);
        if (inscricao.getId() != null) {
            throw new BadRequestAlertException("A new inscricao cannot already have an ID", ENTITY_NAME, "idexists");
        }
        inscricao = inscricaoService.save(inscricao);
        return ResponseEntity.created(new URI("/api/inscricaos/" + inscricao.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, inscricao.getId().toString()))
            .body(inscricao);
    }

    /**
     * {@code PUT  /inscricaos/:id} : Updates an existing inscricao.
     *
     * @param id the id of the inscricao to save.
     * @param inscricao the inscricao to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inscricao,
     * or with status {@code 400 (Bad Request)} if the inscricao is not valid,
     * or with status {@code 500 (Internal Server Error)} if the inscricao couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Inscricao> updateInscricao(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Inscricao inscricao
    ) throws URISyntaxException {
        log.debug("REST request to update Inscricao : {}, {}", id, inscricao);
        if (inscricao.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inscricao.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!inscricaoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        inscricao = inscricaoService.update(inscricao);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, inscricao.getId().toString()))
            .body(inscricao);
    }

    /**
     * {@code PATCH  /inscricaos/:id} : Partial updates given fields of an existing inscricao, field will ignore if it is null
     *
     * @param id the id of the inscricao to save.
     * @param inscricao the inscricao to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inscricao,
     * or with status {@code 400 (Bad Request)} if the inscricao is not valid,
     * or with status {@code 404 (Not Found)} if the inscricao is not found,
     * or with status {@code 500 (Internal Server Error)} if the inscricao couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Inscricao> partialUpdateInscricao(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Inscricao inscricao
    ) throws URISyntaxException {
        log.debug("REST request to partial update Inscricao partially : {}, {}", id, inscricao);
        if (inscricao.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inscricao.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!inscricaoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Inscricao> result = inscricaoService.partialUpdate(inscricao);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, inscricao.getId().toString())
        );
    }

    /**
     * {@code GET  /inscricaos} : get all the inscricaos.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of inscricaos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Inscricao>> getAllInscricaos(
        InscricaoCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Inscricaos by criteria: {}", criteria);

        Page<Inscricao> page = inscricaoQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /inscricaos/count} : count all the inscricaos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countInscricaos(InscricaoCriteria criteria) {
        log.debug("REST request to count Inscricaos by criteria: {}", criteria);
        return ResponseEntity.ok().body(inscricaoQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /inscricaos/:id} : get the "id" inscricao.
     *
     * @param id the id of the inscricao to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the inscricao, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Inscricao> getInscricao(@PathVariable("id") Long id) {
        log.debug("REST request to get Inscricao : {}", id);
        Optional<Inscricao> inscricao = inscricaoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(inscricao);
    }

    /**
     * {@code DELETE  /inscricaos/:id} : delete the "id" inscricao.
     *
     * @param id the id of the inscricao to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInscricao(@PathVariable("id") Long id) {
        log.debug("REST request to delete Inscricao : {}", id);
        inscricaoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
