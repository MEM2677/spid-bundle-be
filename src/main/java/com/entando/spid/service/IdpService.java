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
     * Return templates for identity providers
     * @return
     */
    List<Idp> getTemplates();

    /**
     * Read a file from the resources as inputStream
     *
     * @param fileName the path of teh file to read
     */
  void prepareConfigurationMap(String fileName);

}
