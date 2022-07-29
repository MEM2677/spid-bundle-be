package com.entando.spid.service;

import com.entando.spid.domain.*; // for static metamodels
import com.entando.spid.domain.Idp;
import com.entando.spid.repository.IdpRepository;
import com.entando.spid.service.criteria.SpidCriteria;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Idp} entities in the database.
 * The main input is a {@link SpidCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Idp} or a {@link Page} of {@link Idp} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SpidQueryService extends QueryService<Idp> {

    private final Logger log = LoggerFactory.getLogger(SpidQueryService.class);

    private final IdpRepository idpRepository;

    public SpidQueryService(IdpRepository idpRepository) {
        this.idpRepository = idpRepository;
    }

    /**
     * Return a {@link List} of {@link Idp} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Idp> findByCriteria(SpidCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Idp> specification = createSpecification(criteria);
        return idpRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Idp} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Idp> findByCriteria(SpidCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Idp> specification = createSpecification(criteria);
        return idpRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SpidCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Idp> specification = createSpecification(criteria);
        return idpRepository.count(specification);
    }

    /**
     * Function to convert {@link SpidCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Idp> createSpecification(SpidCriteria criteria) {
        Specification<Idp> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Idp_.id));
            }
            if (criteria.getConfig() != null) {
                specification = specification.and(buildStringSpecification(criteria.getConfig(), Idp_.config));
            }
        }
        return specification;
    }
}
