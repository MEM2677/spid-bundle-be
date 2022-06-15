package com.entando.spid.service;

import com.entando.spid.domain.Spid;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Spid}.
 */
public interface SpidService {
    /**
     * Save a spid.
     *
     * @param spid the entity to save.
     * @return the persisted entity.
     */
    Spid save(Spid spid);

    /**
     * Partially updates a spid.
     *
     * @param spid the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Spid> partialUpdate(Spid spid);

    /**
     * Get all the spids.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Spid> findAll(Pageable pageable);

    /**
     * Get the "id" spid.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Spid> findOne(Long id);

    /**
     * Delete the "id" spid.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
