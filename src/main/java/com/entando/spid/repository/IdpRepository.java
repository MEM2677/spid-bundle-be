package com.entando.spid.repository;

import com.entando.spid.domain.Idp;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Idp entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IdpRepository extends JpaRepository<Idp, Long>, JpaSpecificationExecutor<Idp> {}
