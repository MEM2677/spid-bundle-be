package com.entando.spid.service;

import com.entando.spid.domain.Idp;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Idp}.
 */
public interface IdpService {
    /**
     * Save a idp.
     *
     * @param idp the entity to save.
     * @return the persisted entity.
     */
    Idp save(Idp idp);

    /**
     * Partially updates a idp.
     *
     * @param idp the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Idp> partialUpdate(Idp idp);

    /**
     * Get all the spids.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Idp> findAll(Pageable pageable);

    List<Idp> findAll();

    /**
     * Get the "id" spid.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Idp> findOne(Long id);

    /**
     * Delete the "id" spid.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
