package com.conexaosolidaria.app.service;

import com.conexaosolidaria.app.domain.*; // for static metamodels
import com.conexaosolidaria.app.domain.Inscricao;
import com.conexaosolidaria.app.repository.InscricaoRepository;
import com.conexaosolidaria.app.service.criteria.InscricaoCriteria;
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
 * Service for executing complex queries for {@link Inscricao} entities in the database.
 * The main input is a {@link InscricaoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Inscricao} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InscricaoQueryService extends QueryService<Inscricao> {

    private final Logger log = LoggerFactory.getLogger(InscricaoQueryService.class);

    private final InscricaoRepository inscricaoRepository;

    public InscricaoQueryService(InscricaoRepository inscricaoRepository) {
        this.inscricaoRepository = inscricaoRepository;
    }

    /**
     * Return a {@link Page} of {@link Inscricao} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Inscricao> findByCriteria(InscricaoCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Inscricao> specification = createSpecification(criteria);
        return inscricaoRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InscricaoCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Inscricao> specification = createSpecification(criteria);
        return inscricaoRepository.count(specification);
    }

    /**
     * Function to convert {@link InscricaoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Inscricao> createSpecification(InscricaoCriteria criteria) {
        Specification<Inscricao> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Inscricao_.id));
            }
            if (criteria.getData() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getData(), Inscricao_.data));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getUserId(), root -> root.join(Inscricao_.user, JoinType.LEFT).get(User_.id))
                );
            }
            if (criteria.getEventoId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getEventoId(), root -> root.join(Inscricao_.evento, JoinType.LEFT).get(Eventos_.id))
                );
            }
        }
        return specification;
    }
}
