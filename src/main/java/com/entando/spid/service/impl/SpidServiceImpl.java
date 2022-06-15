package com.entando.spid.service.impl;

import com.entando.spid.domain.Spid;
import com.entando.spid.repository.SpidRepository;
import com.entando.spid.service.SpidService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Spid}.
 */
@Service
@Transactional
public class SpidServiceImpl implements SpidService {

    private final Logger log = LoggerFactory.getLogger(SpidServiceImpl.class);

    private final SpidRepository spidRepository;

    public SpidServiceImpl(SpidRepository spidRepository) {
        this.spidRepository = spidRepository;
    }

    @Override
    public Spid save(Spid spid) {
        log.debug("Request to save Spid : {}", spid);
        return spidRepository.save(spid);
    }

    @Override
    public Optional<Spid> partialUpdate(Spid spid) {
        log.debug("Request to partially update Spid : {}", spid);

        return spidRepository
            .findById(spid.getId())
            .map(existingSpid -> {
                if (spid.getConfig() != null) {
                    existingSpid.setConfig(spid.getConfig());
                }

                return existingSpid;
            })
            .map(spidRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Spid> findAll(Pageable pageable) {
        log.debug("Request to get all Spids");
        return spidRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Spid> findOne(Long id) {
        log.debug("Request to get Spid : {}", id);
        return spidRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Spid : {}", id);
        spidRepository.deleteById(id);
    }
}
