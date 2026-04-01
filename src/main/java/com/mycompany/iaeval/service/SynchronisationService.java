package com.mycompany.iaeval.service;

import static tech.jhipster.config.JHipsterDefaults.Mail.baseUrl;

import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.domain.Candidat;
import com.mycompany.iaeval.domain.DocumentJoint;
import com.mycompany.iaeval.domain.Soumission;
import com.mycompany.iaeval.domain.enumeration.FormatDocument;
import com.mycompany.iaeval.domain.enumeration.StatutAppel;
import com.mycompany.iaeval.domain.enumeration.StatutEvaluation;
import com.mycompany.iaeval.repository.AppelOffreRepository;
import com.mycompany.iaeval.repository.CandidatRepository;
import com.mycompany.iaeval.repository.DocumentJointRepository;
import com.mycompany.iaeval.repository.SoumissionRepository;
import com.mycompany.iaeval.service.dto.AppelOffreDTO;
import com.mycompany.iaeval.service.dto.CandidatDTO;
import com.mycompany.iaeval.service.dto.DocumentJointDTO;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Transactional
public class SynchronisationService {

    private final Logger log = LoggerFactory.getLogger(SynchronisationService.class);

    private final RestClient restClient;
    private final AppelOffreRepository appelOffreRepository;
    private final CandidatRepository candidatRepository;
    private final DocumentJointRepository documentJointRepository;
    private final SoumissionRepository soumissionRepository;

    public SynchronisationService(
        RestClient.Builder restClientBuilder,
        AppelOffreRepository appelOffreRepository,
        CandidatRepository candidatRepository,
        DocumentJointRepository documentJointRepository,
        SoumissionRepository soumissionRepository,
        @Value("${external-api.base-url}") String baseUrl
    ) {
        // Configuration de l'URL de base de l'API Métier
        //this.restClient = restClientBuilder.baseUrl("http://api-metier-existant.com").build();
        this.appelOffreRepository = appelOffreRepository;
        this.candidatRepository = candidatRepository;
        this.documentJointRepository = documentJointRepository;
        this.soumissionRepository = soumissionRepository;
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * Pipeline complet de récupération (Flux du Cahier des Charges)
     */
    public void synchroniserTout() {
        log.info("Début de la synchronisation avec les Endpoints Métiers");

        // 1. GET /appels-offres : Récupération des offres
        List<AppelOffreDTO> offresExternes = restClient
            .get()
            .uri("/appels-offres")
            .retrieve()
            .body(new ParameterizedTypeReference<List<AppelOffreDTO>>() {});

        for (AppelOffreDTO offreExt : offresExternes) {
            AppelOffre appelOffre = enregistrerOuMettreAJourAppel(offreExt);

            // 2. GET /appels-offres/{id}/soumissionnaires : Récupération des candidats
            List<CandidatDTO> candidatsExternes = restClient
                .get()
                .uri("/appels-offres/{id}/soumissionnaires", offreExt.getId())
                .retrieve()
                .body(new ParameterizedTypeReference<List<CandidatDTO>>() {});

            for (CandidatDTO candExt : candidatsExternes) {
                Candidat candidat = enregistrerOuMettreAJourCandidat(candExt);
                Soumission soumission = creerSoumission(appelOffre, candidat);

                // 3. GET /candidats/{id}/documents : Récupération des PDF
                List<DocumentJointDTO> docsExternes = restClient
                    .get()
                    .uri("/candidats/{id}/documents", candExt.getId())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<DocumentJointDTO>>() {});

                for (DocumentJointDTO docExt : docsExternes) {
                    telechargerEtAnalyserDocument(soumission, docExt);
                }
            }
        }
    }

    /**
     * Méthode de simulation pour tester l'IA sans API externe.
     */
    public void simulerSynchronisation() {
        log.info("Simulation : Création de données de test...");

        // 1. On simule un Appel d'Offre reçu
        AppelOffre ao = new AppelOffre()
            .reference("AO-2024-TEST")
            .titre("Construction d'un centre informatique")
            .statut(StatutAppel.OUVERT)
            .dateCloture(Instant.now());
        ao = appelOffreRepository.save(ao);

        // 2. On simule un Candidat
        Candidat candidat = new Candidat().nom("Souverain Tech").siret("123456789").email("contact@souverain.fr");
        candidat = candidatRepository.save(candidat);

        // 3. On crée la Soumission (Le lien)
        Soumission soumission = new Soumission()
            .dateSoumission(Instant.now())
            .statut(StatutEvaluation.EN_ATTENTE)
            .appelOffre(ao)
            .candidat(candidat);
        soumission = soumissionRepository.save(soumission);

        // 4. On simule l'OCR d'un document PDF reçu
        DocumentJoint doc = new DocumentJoint()
            .nom("OffreTechnique.pdf")
            .format(FormatDocument.OFFRE_TECHNIQUE)
            .contenuOcr("Le candidat s'engage à utiliser des serveurs refroidis par immersion.")
            .soumission(soumission);
        documentJointRepository.save(doc);

        log.info("Simulation terminée. Vous pouvez maintenant évaluer l'offre AO-2024-TEST dans l'interface.");
    }

    private void telechargerEtAnalyserDocument(Soumission soumission, DocumentJointDTO dto) {
        // Logique de téléchargement du PDF et appel à un service d'OCR
        // pour remplir le champ 'contenuOcr' nécessaire au RAG
        log.debug("Téléchargement du document : {}", dto.getNom());
        DocumentJoint doc = new DocumentJoint()
            .nom(dto.getNom())
            .url(dto.getUrl())
            .format(FormatDocument.OFFRE_TECHNIQUE)
            .soumission(soumission);

        // Ici, on pourrait appeler une librairie type Tika ou un service OCR
        doc.setContenuOcr("Texte extrait du PDF...");

        documentJointRepository.save(doc);
    }

    // Méthodes utilitaires pour save/update...

    /**
     * Enregistre un appel d'offre s'il est nouveau, ou le met à jour s'il existe déjà.
     */
    private AppelOffre enregistrerOuMettreAJourAppel(AppelOffreDTO dto) {
        return appelOffreRepository
            .findOneByReference(dto.getReference())
            .map(existant -> {
                log.debug("Mise à jour de l'appel d'offre : {}", dto.getReference());
                existant.setTitre(dto.getTitre());
                existant.setDescription(dto.getDescription());
                existant.setDateCloture(dto.getDateCloture());
                existant.setStatut(StatutAppel.OUVERT);
                return appelOffreRepository.save(existant);
            })
            .orElseGet(() -> {
                log.debug("Création d'un nouvel appel d'offre : {}", dto.getReference());
                AppelOffre nouveau = new AppelOffre()
                    .reference(dto.getReference())
                    .titre(dto.getTitre())
                    .description(dto.getDescription())
                    .dateCloture(dto.getDateCloture())
                    .statut(StatutAppel.OUVERT);
                return appelOffreRepository.save(nouveau);
            });
    }

    /**
     * Enregistre un candidat ou met à jour ses informations (email, nom).
     */
    private Candidat enregistrerOuMettreAJourCandidat(CandidatDTO dto) {
        return candidatRepository
            .findOneBySiret(dto.getSiret())
            .map(existant -> {
                log.debug("Mise à jour du candidat : {}", dto.getSiret());
                existant.setNom(dto.getNom());
                existant.setEmail(dto.getEmail());
                return candidatRepository.save(existant);
            })
            .orElseGet(() -> {
                log.debug("Nouveau candidat détecté : {}", dto.getSiret());
                Candidat nouveau = new Candidat().nom(dto.getNom()).siret(dto.getSiret()).email(dto.getEmail());
                return candidatRepository.save(nouveau);
            });
    }

    /**
     * Crée le lien entre un Appel d'Offre et un Candidat.
     */
    private Soumission creerSoumission(AppelOffre appelOffre, Candidat candidat) {
        return soumissionRepository
            .findOneByAppelOffreIdAndCandidatId(appelOffre.getId(), candidat.getId())
            .orElseGet(() -> {
                log.debug("Création de la soumission pour le candidat {} sur l'appel {}", candidat.getNom(), appelOffre.getReference());
                Soumission nouvelle = new Soumission()
                    .dateSoumission(Instant.now())
                    .statut(StatutEvaluation.EN_ATTENTE)
                    .appelOffre(appelOffre)
                    .candidat(candidat);
                return soumissionRepository.save(nouvelle);
            });
    }
}
