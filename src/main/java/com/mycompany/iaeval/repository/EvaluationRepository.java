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

    // Cette méthode récupère uniquement les évaluations qui n'ont pas encore été validées
    Page<Evaluation> findAllByEstValideeFalse(Pageable pageable);

    // Si vous avez besoin de voir celles validées par un utilisateur spécifique
    Page<Evaluation> findAllByEvaluateurLogin(String login, Pageable pageable);
    // Récupère les évaluations validées pour un appel d'offre précis
    List<Evaluation> findAllBySoumissionAppelOffreIdAndEstValideeTrue(Long appelOffreId);
    // Utile pour votre vue : Récupère TOUTES les évaluations d'un appel (validées ou non)
    List<Evaluation> findAllBySoumissionAppelOffreId(Long appelOffreId);

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
