package com.entando.spid.service.impl;

import com.entando.spid.domain.Idp;
import com.entando.spid.repository.IdpRepository;
import com.entando.spid.service.IdpService;

import java.util.List;
import java.util.Optional;
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

    private final IdpRepository idpRepository;

    public IdpServiceImpl(IdpRepository idpRepository) {
        this.idpRepository = idpRepository;
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
