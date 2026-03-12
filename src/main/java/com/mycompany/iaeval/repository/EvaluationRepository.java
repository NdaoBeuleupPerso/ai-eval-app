package com.mycompany.iaeval.repository;

import com.mycompany.iaeval.domain.Evaluation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Evaluation entity.
 */
@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    @Query("select evaluation from Evaluation evaluation where evaluation.evaluateur.login = ?#{authentication.name}")
    List<Evaluation> findByEvaluateurIsCurrentUser();

    default Optional<Evaluation> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Evaluation> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Evaluation> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select evaluation from Evaluation evaluation left join fetch evaluation.evaluateur",
        countQuery = "select count(evaluation) from Evaluation evaluation"
    )
    Page<Evaluation> findAllWithToOneRelationships(Pageable pageable);

    @Query("select evaluation from Evaluation evaluation left join fetch evaluation.evaluateur")
    List<Evaluation> findAllWithToOneRelationships();

    @Query("select evaluation from Evaluation evaluation left join fetch evaluation.evaluateur where evaluation.id =:id")
    Optional<Evaluation> findOneWithToOneRelationships(@Param("id") Long id);
}
