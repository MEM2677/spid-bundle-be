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

    List<Idp> getTemplates();

    /**
     * Read a file from the resources as inputStream
     *
     * @param fileName the path of teh file to read
     */
  void prepareConfigurationMap(String fileName);

  /**
     * Save a idp.
     *
     * @param idp the entity to save.
     * @return the persisted entity.
    * @deprecated
     */
    Idp save(Idp idp);

    /**
     * Partially updates a idp.
     *
     * @param idp the entity to update partially.
     * @return the persisted entity.
     * @deprecated
     */
    Optional<Idp> partialUpdate(Idp idp);

    /**
     * Get all the spids.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     * @deprecated
     */
    Page<Idp> findAll(Pageable pageable);

    List<Idp> findAll();

    /**
     * Get the "id" spid.
     *
     * @param id the id of the entity.
     * @return the entity.
     * @deprecated
     */
    Optional<Idp> findOne(Long id);

    /**
     * Delete the "id" spid.
     *
     * @param id the id of the entity.
     * @deprecated
     */
    void delete(Long id);
}
