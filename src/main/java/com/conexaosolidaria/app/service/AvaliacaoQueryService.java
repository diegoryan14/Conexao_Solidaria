package com.conexaosolidaria.app.service;

import com.conexaosolidaria.app.domain.*; // for static metamodels
import com.conexaosolidaria.app.domain.Avaliacao;
import com.conexaosolidaria.app.repository.AvaliacaoRepository;
import com.conexaosolidaria.app.service.criteria.AvaliacaoCriteria;
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
 * Service for executing complex queries for {@link Avaliacao} entities in the database.
 * The main input is a {@link AvaliacaoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Avaliacao} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AvaliacaoQueryService extends QueryService<Avaliacao> {

    private final Logger log = LoggerFactory.getLogger(AvaliacaoQueryService.class);

    private final AvaliacaoRepository avaliacaoRepository;

    public AvaliacaoQueryService(AvaliacaoRepository avaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
    }

    /**
     * Return a {@link Page} of {@link Avaliacao} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Avaliacao> findByCriteria(AvaliacaoCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Avaliacao> specification = createSpecification(criteria);
        return avaliacaoRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AvaliacaoCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Avaliacao> specification = createSpecification(criteria);
        return avaliacaoRepository.count(specification);
    }

    /**
     * Function to convert {@link AvaliacaoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Avaliacao> createSpecification(AvaliacaoCriteria criteria) {
        Specification<Avaliacao> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Avaliacao_.id));
            }
            if (criteria.getEstrelas() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEstrelas(), Avaliacao_.estrelas));
            }
            if (criteria.getObservacao() != null) {
                specification = specification.and(buildStringSpecification(criteria.getObservacao(), Avaliacao_.observacao));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getUserId(), root -> root.join(Avaliacao_.user, JoinType.LEFT).get(User_.id))
                );
            }
            if (criteria.getEventoId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getEventoId(), root -> root.join(Avaliacao_.evento, JoinType.LEFT).get(Eventos_.id))
                );
            }
        }
        return specification;
    }
}
