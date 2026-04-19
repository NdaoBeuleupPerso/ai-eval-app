package com.mycompany.iaeval.repository;

import com.mycompany.iaeval.domain.Soumission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Soumission entity.
 */
@Repository
public interface SoumissionRepository extends JpaRepository<Soumission, Long>, JpaSpecificationExecutor<Soumission> {
    default Optional<Soumission> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Soumission> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Soumission> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select soumission from Soumission soumission left join fetch soumission.appelOffre left join fetch soumission.candidat",
        countQuery = "select count(soumission) from Soumission soumission"
    )
    Page<Soumission> findAllWithToOneRelationships(Pageable pageable);

    @Query("select soumission from Soumission soumission left join fetch soumission.appelOffre left join fetch soumission.candidat")
    List<Soumission> findAllWithToOneRelationships();

    @Query(
        "select soumission from Soumission soumission left join fetch soumission.appelOffre left join fetch soumission.candidat where soumission.id =:id"
    )
    Optional<Soumission> findOneWithToOneRelationships(@Param("id") Long id);

    Optional<Soumission> findOneByAppelOffreIdAndCandidatId(Long appelOffreId, Long candidatId);
    List<Soumission> findAllByAppelOffreId(Long appelOffreId);

    List<Soumission> findByAppelOffreId(Long appelOffreId);
}
