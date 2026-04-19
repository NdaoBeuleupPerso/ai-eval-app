package com.mycompany.iaeval.repository;

import com.mycompany.iaeval.domain.AppelOffre;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AppelOffre entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppelOffreRepository extends JpaRepository<AppelOffre, Long>, JpaSpecificationExecutor<AppelOffre> {
    Optional<AppelOffre> findOneByReference(String reference);
    Optional<AppelOffre> findOneByTitre(String titre);
    Optional<AppelOffre> findById(Long id);
}
