package com.entando.spid.repository;

import com.entando.spid.domain.Spid;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Spid entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SpidRepository extends JpaRepository<Spid, Long>, JpaSpecificationExecutor<Spid> {}
