package com.conexaosolidaria.app.service;

import com.conexaosolidaria.app.domain.Inscricao;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.conexaosolidaria.app.domain.Inscricao}.
 */
public interface InscricaoService {
    /**
     * Save a inscricao.
     *
     * @param inscricao the entity to save.
     * @return the persisted entity.
     */
    Inscricao save(Inscricao inscricao);

    /**
     * Updates a inscricao.
     *
     * @param inscricao the entity to update.
     * @return the persisted entity.
     */
    Inscricao update(Inscricao inscricao);

    /**
     * Partially updates a inscricao.
     *
     * @param inscricao the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Inscricao> partialUpdate(Inscricao inscricao);

    /**
     * Get all the inscricaos with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Inscricao> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" inscricao.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Inscricao> findOne(Long id);

    /**
     * Delete the "id" inscricao.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
