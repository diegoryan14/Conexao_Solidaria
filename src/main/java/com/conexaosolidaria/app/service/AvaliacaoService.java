package com.conexaosolidaria.app.service;

import com.conexaosolidaria.app.domain.Avaliacao;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.conexaosolidaria.app.domain.Avaliacao}.
 */
public interface AvaliacaoService {
    /**
     * Save a avaliacao.
     *
     * @param avaliacao the entity to save.
     * @return the persisted entity.
     */
    Avaliacao save(Avaliacao avaliacao);

    /**
     * Updates a avaliacao.
     *
     * @param avaliacao the entity to update.
     * @return the persisted entity.
     */
    Avaliacao update(Avaliacao avaliacao);

    /**
     * Partially updates a avaliacao.
     *
     * @param avaliacao the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Avaliacao> partialUpdate(Avaliacao avaliacao);

    /**
     * Get all the avaliacaos with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Avaliacao> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" avaliacao.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Avaliacao> findOne(Long id);

    /**
     * Delete the "id" avaliacao.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
