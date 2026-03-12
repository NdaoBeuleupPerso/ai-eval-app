package com.mycompany.iaeval.repository;

import com.mycompany.iaeval.domain.Critere;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Critere entity.
 */
@Repository
public interface CritereRepository extends JpaRepository<Critere, Long> {
    default Optional<Critere> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Critere> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Critere> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select critere from Critere critere left join fetch critere.appelOffre",
        countQuery = "select count(critere) from Critere critere"
    )
    Page<Critere> findAllWithToOneRelationships(Pageable pageable);

    @Query("select critere from Critere critere left join fetch critere.appelOffre")
    List<Critere> findAllWithToOneRelationships();

    @Query("select critere from Critere critere left join fetch critere.appelOffre where critere.id =:id")
    Optional<Critere> findOneWithToOneRelationships(@Param("id") Long id);
}
