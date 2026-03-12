package com.mycompany.iaeval.repository;

import com.mycompany.iaeval.domain.AppelOffre;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AppelOffre entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppelOffreRepository extends JpaRepository<AppelOffre, Long>, JpaSpecificationExecutor<AppelOffre> {}
