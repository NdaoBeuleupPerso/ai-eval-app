package com.mycompany.iaeval.repository;

import com.mycompany.iaeval.domain.DocumentJoint;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DocumentJoint entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DocumentJointRepository extends JpaRepository<DocumentJoint, Long> {}
