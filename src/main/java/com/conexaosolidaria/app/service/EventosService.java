package com.conexaosolidaria.app.service;

import com.conexaosolidaria.app.domain.Eventos;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.conexaosolidaria.app.domain.Eventos}.
 */
public interface EventosService {
    /**
     * Save a eventos.
     *
     * @param eventos the entity to save.
     * @return the persisted entity.
     */
    Eventos save(Eventos eventos);

    /**
     * Updates a eventos.
     *
     * @param eventos the entity to update.
     * @return the persisted entity.
     */
    Eventos update(Eventos eventos);

    /**
     * Partially updates a eventos.
     *
     * @param eventos the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Eventos> partialUpdate(Eventos eventos);

    /**
     * Get all the eventos with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Eventos> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" eventos.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Eventos> findOne(Long id);

    /**
     * Delete the "id" eventos.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
