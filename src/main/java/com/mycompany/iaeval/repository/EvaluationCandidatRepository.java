package com.mycompany.iaeval.repository;

import com.mycompany.iaeval.domain.EvaluationCandidat;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EvaluationCandidat entity.
 */
@Repository
public interface EvaluationCandidatRepository extends JpaRepository<EvaluationCandidat, Long> {
    // 1. Requêtes simples basées sur les propriétés réelles de l'entité
    Page<EvaluationCandidat> findAllByEstValideeFalse(Pageable pageable);

    List<EvaluationCandidat> findAllBySoumissionAppelOffreIdAndEstValideeTrue(Long appelOffreId);

    List<EvaluationCandidat> findAllBySoumissionAppelOffreId(Long appelOffreId);

    // 2. Gestion des relations "ToOne" pour éviter le problème N+1 (Eager Loading)
    // On ajoute impérativement l'annotation @Query pour corriger votre erreur de démarrage

    default Optional<EvaluationCandidat> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<EvaluationCandidat> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<EvaluationCandidat> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select evaluationCandidat from EvaluationCandidat evaluationCandidat left join fetch evaluationCandidat.soumission",
        countQuery = "select count(evaluationCandidat) from EvaluationCandidat evaluationCandidat"
    )
    Page<EvaluationCandidat> findAllWithToOneRelationships(Pageable pageable);

    @Query("select evaluationCandidat from EvaluationCandidat evaluationCandidat left join fetch evaluationCandidat.soumission")
    List<EvaluationCandidat> findAllWithToOneRelationships();

    @Query(
        "select evaluationCandidat from EvaluationCandidat evaluationCandidat left join fetch evaluationCandidat.soumission where evaluationCandidat.id =:id"
    )
    Optional<EvaluationCandidat> findOneWithToOneRelationships(@Param("id") Long id);
}
