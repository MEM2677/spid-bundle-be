package com.entando.spid.service.impl;

import com.entando.spid.domain.Idp;
import com.entando.spid.repository.IdpRepository;
import com.entando.spid.service.IdpService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Idp}.
 */
@Service
@Transactional
public class IdpServiceImpl implements IdpService {

    private final Logger log = LoggerFactory.getLogger(IdpServiceImpl.class);

    private final Map<Long, Idp> templates = new ConcurrentHashMap<>();

    @Deprecated
    private final IdpRepository idpRepository;

    public IdpServiceImpl(IdpRepository idpRepository) {

        this.idpRepository = idpRepository;
        try {
            prepareConfigurationMap("config/template/idpTemplates.csv");
        } catch (Throwable t) {
            templates.clear();
            log.error("Error on service startup ", t);
        }
    }

    @Override
    public List<Idp> getTemplates() {
        return templates.values()
            .stream()
            .collect(Collectors.toList());
    }

    @Override
    public void prepareConfigurationMap(String fileName) {
        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("Configuration file not found! " + fileName);
        }
        // create configuration map from static file
        try (InputStreamReader streamReader =
                 new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                // ignore empty lines
                if (StringUtils.isBlank(line)) {
                    continue;
                }
//                log.debug("Input configuration line: {}", line);
                // convert to IdP
                String[] token = line.split(";");
                Idp config = new Idp();

                if (token.length != 3) {
                    throw new RuntimeException("Unexpected CSV format! Expected semicolon ';' as delimiter");
                }

                config.setId(Long.parseLong(token[0]));
                config.setName(token[1]);
                config.setConfig(token[2]);

                templates.put(config.getId(), config);
                log.debug("Template id {} for provider {} successfully imported", config.getId(), config.getName());
            }
            log.info("{} providers configuration imported", templates.size());
        } catch (IOException t) {
            throw new RuntimeException("Error loading configuration file!", t);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error("Error closing the stream", e);
            }
        }
    }


    @Override
    public Idp save(Idp idp) {
        log.debug("Request to save Idp : {}", idp);
        return idpRepository.save(idp);
    }

    @Override
    public Optional<Idp> partialUpdate(Idp idp) {
        log.debug("Request to partially update Idp : {}", idp);

        return idpRepository
            .findById(idp.getId())
            .map(existingSpid -> {
                if (idp.getConfig() != null) {
                    existingSpid.setConfig(idp.getConfig());
                }

                return existingSpid;
            })
            .map(idpRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Idp> findAll(Pageable pageable) {
        log.debug("Request to get all IdP");
        return idpRepository.findAll(pageable);
    }

    @Override
    public List<Idp> findAll() {
        return idpRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Idp> findOne(Long id) {
        log.debug("Request to get Idp : {}", id);
        return idpRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Idp : {}", id);
        idpRepository.deleteById(id);
    }
}
