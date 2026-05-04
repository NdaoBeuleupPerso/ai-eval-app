package com.mycompany.iaeval.service.dto;

import com.mycompany.iaeval.domain.enumeration.StatutEvaluation;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.iaeval.domain.Soumission} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SoumissionDTO implements Serializable {

    private Long id;

    private Instant dateSoumission;

    private StatutEvaluation statut;

    private EvaluationDTO evaluation; // Pour l'évaluation ADMIN

    private EvaluationCandidatDTO evaluationCandidat; // Pour l'évaluation SOUMISSIONNAIRE

    private Set<DocumentJointDTO> documents = new HashSet<>();

    private AppelOffreDTO appelOffre;

    private CandidatDTO candidat;

    private List<DocumentJointDTO> nouveauxDocuments = new ArrayList<>();

    // Getters et Setters pour nouveauxDocuments
    public List<DocumentJointDTO> getNouveauxDocuments() {
        return nouveauxDocuments;
    }

    public void setNouveauxDocuments(List<DocumentJointDTO> nouveauxDocuments) {
        this.nouveauxDocuments = nouveauxDocuments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDateSoumission() {
        return dateSoumission;
    }

    public void setDateSoumission(Instant dateSoumission) {
        this.dateSoumission = dateSoumission;
    }

    public StatutEvaluation getStatut() {
        return statut;
    }

    public void setStatut(StatutEvaluation statut) {
        this.statut = statut;
    }

    public EvaluationDTO getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationDTO evaluation) {
        this.evaluation = evaluation;
    }

    public EvaluationCandidatDTO getEvaluationCandidat() {
        return evaluationCandidat;
    }

    public void setEvaluationCandidat(EvaluationCandidatDTO evaluationCandidat) {
        this.evaluationCandidat = evaluationCandidat;
    }

    public Set<DocumentJointDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<DocumentJointDTO> documents) {
        this.documents = documents;
    }

    public AppelOffreDTO getAppelOffre() {
        return appelOffre;
    }

    public void setAppelOffre(AppelOffreDTO appelOffre) {
        this.appelOffre = appelOffre;
    }

    public CandidatDTO getCandidat() {
        return candidat;
    }

    public void setCandidat(CandidatDTO candidat) {
        this.candidat = candidat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SoumissionDTO soumissionDTO)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, soumissionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "SoumissionDTO{" +
            "id=" +
            getId() +
            ", dateSoumission='" +
            getDateSoumission() +
            "'" +
            ", statut='" +
            getStatut() +
            "'" +
            ", documentsCount=" +
            (getDocuments() != null ? getDocuments().size() : 0) +
            "}"
        );
    }
}
