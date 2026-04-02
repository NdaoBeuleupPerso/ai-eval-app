package com.mycompany.iaeval.repository;

import com.mycompany.iaeval.domain.EvaluationCandidat;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Evaluation entity.
 */
@Repository
public interface EvaluationCandidatRepository extends JpaRepository<EvaluationCandidat, Long> {
    // Cette méthode récupère uniquement les évaluations qui n'ont pas encore été validées
    Page<EvaluationCandidat> findAllByEstValideeFalse(Pageable pageable);

    // Si vous avez besoin de voir celles validées par un utilisateur spécifique
    Page<EvaluationCandidat> findAllByEvaluateurLogin(String login, Pageable pageable);
    // Récupère les évaluations validées pour un appel d'offre précis
    List<EvaluationCandidat> findAllBySoumissionAppelOffreIdAndEstValideeTrue(Long appelOffreId);
    // Utile pour votre vue : Récupère TOUTES les évaluations d'un appel (validées ou non)
    List<EvaluationCandidat> findAllBySoumissionAppelOffreId(Long appelOffreId);

    default Page<EvaluationCandidat> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    Page<EvaluationCandidat> findAllWithToOneRelationships(Pageable pageable);
}
