package com.mycompany.iaeval.repository;

import com.mycompany.iaeval.domain.TraceAudit;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TraceAudit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TraceAuditRepository extends JpaRepository<TraceAudit, Long>, JpaSpecificationExecutor<TraceAudit> {}
