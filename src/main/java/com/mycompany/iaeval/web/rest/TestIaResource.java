package com.mycompany.iaeval.web.rest;

import com.mycompany.iaeval.service.SynchronisationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur utilitaire pour préparer l'environnement de test IA.
 */
@RestController
@RequestMapping("/api/v1")
public class TestIaResource {

    private final Logger log = LoggerFactory.getLogger(TestIaResource.class);
    private final SynchronisationService synchronisationService;

    public TestIaResource(SynchronisationService synchronisationService) {
        this.synchronisationService = synchronisationService;
    }

    /**
     * POST /test-ia/setup : Crée un Appel d'Offre, un Candidat et un Document simulé.
     */
    @GetMapping("/test-ia/setup")
    public ResponseEntity<String> setupTestData() {
        log.info("Requête REST pour générer des données de test IA");
        try {
            synchronisationService.simulerSynchronisation();
            return ResponseEntity.ok("Données de test créées avec succès ! Vous pouvez maintenant aller dans 'Entités > Appel Offre'.");
        } catch (Exception e) {
            log.error("Erreur lors de la création des données de test", e);
            return ResponseEntity.internalServerError().body("Erreur : " + e.getMessage());
        }
    }
}
