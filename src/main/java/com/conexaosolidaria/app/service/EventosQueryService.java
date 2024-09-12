package com.conexaosolidaria.app.service;

import com.conexaosolidaria.app.domain.*; // for static metamodels
import com.conexaosolidaria.app.domain.Eventos;
import com.conexaosolidaria.app.repository.EventosRepository;
import com.conexaosolidaria.app.service.criteria.EventosCriteria;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Eventos} entities in the database.
 * The main input is a {@link EventosCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Eventos} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EventosQueryService extends QueryService<Eventos> {

    private final Logger log = LoggerFactory.getLogger(EventosQueryService.class);

    private final EventosRepository eventosRepository;

    public EventosQueryService(EventosRepository eventosRepository) {
        this.eventosRepository = eventosRepository;
    }

    /**
     * Return a {@link Page} of {@link Eventos} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Eventos> findByCriteria(EventosCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Eventos> specification = createSpecification(criteria);
        return eventosRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EventosCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Eventos> specification = createSpecification(criteria);
        return eventosRepository.count(specification);
    }

    /**
     * Function to convert {@link EventosCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Eventos> createSpecification(EventosCriteria criteria) {
        Specification<Eventos> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Eventos_.id));
            }
            if (criteria.getNome() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNome(), Eventos_.nome));
            }
            if (criteria.getDataCadastro() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataCadastro(), Eventos_.dataCadastro));
            }
            if (criteria.getDataEvento() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataEvento(), Eventos_.dataEvento));
            }
            if (criteria.getHoraInicio() != null) {
                specification = specification.and(buildStringSpecification(criteria.getHoraInicio(), Eventos_.horaInicio));
            }
            if (criteria.getHoraTermino() != null) {
                specification = specification.and(buildStringSpecification(criteria.getHoraTermino(), Eventos_.horaTermino));
            }
            if (criteria.getObservacao() != null) {
                specification = specification.and(buildStringSpecification(criteria.getObservacao(), Eventos_.observacao));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getUserId(), root -> root.join(Eventos_.user, JoinType.LEFT).get(User_.id))
                );
            }
        }
        return specification;
    }
}
