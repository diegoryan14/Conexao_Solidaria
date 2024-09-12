package com.conexaosolidaria.app.web.rest;

import com.conexaosolidaria.app.domain.Eventos;
import com.conexaosolidaria.app.repository.EventosRepository;
import com.conexaosolidaria.app.service.EventosQueryService;
import com.conexaosolidaria.app.service.EventosService;
import com.conexaosolidaria.app.service.criteria.EventosCriteria;
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
 * REST controller for managing {@link com.conexaosolidaria.app.domain.Eventos}.
 */
@RestController
@RequestMapping("/api/eventos")
public class EventosResource {

    private final Logger log = LoggerFactory.getLogger(EventosResource.class);

    private static final String ENTITY_NAME = "eventos";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventosService eventosService;

    private final EventosRepository eventosRepository;

    private final EventosQueryService eventosQueryService;

    public EventosResource(EventosService eventosService, EventosRepository eventosRepository, EventosQueryService eventosQueryService) {
        this.eventosService = eventosService;
        this.eventosRepository = eventosRepository;
        this.eventosQueryService = eventosQueryService;
    }

    /**
     * {@code POST  /eventos} : Create a new eventos.
     *
     * @param eventos the eventos to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventos, or with status {@code 400 (Bad Request)} if the eventos has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Eventos> createEventos(@RequestBody Eventos eventos) throws URISyntaxException {
        log.debug("REST request to save Eventos : {}", eventos);
        if (eventos.getId() != null) {
            throw new BadRequestAlertException("A new eventos cannot already have an ID", ENTITY_NAME, "idexists");
        }
        eventos = eventosService.save(eventos);
        return ResponseEntity.created(new URI("/api/eventos/" + eventos.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, eventos.getId().toString()))
            .body(eventos);
    }

    /**
     * {@code PUT  /eventos/:id} : Updates an existing eventos.
     *
     * @param id the id of the eventos to save.
     * @param eventos the eventos to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventos,
     * or with status {@code 400 (Bad Request)} if the eventos is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventos couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Eventos> updateEventos(@PathVariable(value = "id", required = false) final Long id, @RequestBody Eventos eventos)
        throws URISyntaxException {
        log.debug("REST request to update Eventos : {}, {}", id, eventos);
        if (eventos.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventos.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventosRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        eventos = eventosService.update(eventos);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eventos.getId().toString()))
            .body(eventos);
    }

    /**
     * {@code PATCH  /eventos/:id} : Partial updates given fields of an existing eventos, field will ignore if it is null
     *
     * @param id the id of the eventos to save.
     * @param eventos the eventos to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventos,
     * or with status {@code 400 (Bad Request)} if the eventos is not valid,
     * or with status {@code 404 (Not Found)} if the eventos is not found,
     * or with status {@code 500 (Internal Server Error)} if the eventos couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Eventos> partialUpdateEventos(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Eventos eventos
    ) throws URISyntaxException {
        log.debug("REST request to partial update Eventos partially : {}, {}", id, eventos);
        if (eventos.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventos.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventosRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Eventos> result = eventosService.partialUpdate(eventos);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eventos.getId().toString())
        );
    }

    /**
     * {@code GET  /eventos} : get all the eventos.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eventos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Eventos>> getAllEventos(
        EventosCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Eventos by criteria: {}", criteria);

        Page<Eventos> page = eventosQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /eventos/count} : count all the eventos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countEventos(EventosCriteria criteria) {
        log.debug("REST request to count Eventos by criteria: {}", criteria);
        return ResponseEntity.ok().body(eventosQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /eventos/:id} : get the "id" eventos.
     *
     * @param id the id of the eventos to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventos, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Eventos> getEventos(@PathVariable("id") Long id) {
        log.debug("REST request to get Eventos : {}", id);
        Optional<Eventos> eventos = eventosService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventos);
    }

    /**
     * {@code DELETE  /eventos/:id} : delete the "id" eventos.
     *
     * @param id the id of the eventos to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventos(@PathVariable("id") Long id) {
        log.debug("REST request to delete Eventos : {}", id);
        eventosService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
