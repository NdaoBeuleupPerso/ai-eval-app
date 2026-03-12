package com.mycompany.iaeval.repository;

import com.mycompany.iaeval.domain.ReferenceLegale;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReferenceLegale entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReferenceLegaleRepository extends JpaRepository<ReferenceLegale, Long> {}
