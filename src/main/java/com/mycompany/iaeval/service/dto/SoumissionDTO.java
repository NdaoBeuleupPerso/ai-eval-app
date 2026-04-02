package com.mycompany.iaeval.service.dto;

import com.mycompany.iaeval.domain.enumeration.StatutEvaluation;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.iaeval.domain.Soumission} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SoumissionDTO implements Serializable {

    private Long id;

    private Instant dateSoumission;

    private StatutEvaluation statut;

    private EvaluationDTO evaluation;

    private AppelOffreDTO appelOffre;

    private CandidatDTO candidat;

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

    // prettier-ignore
    @Override
    public String toString() {
        return "SoumissionDTO{" +
            "id=" + getId() +
            ", dateSoumission='" + getDateSoumission() + "'" +
            ", statut='" + getStatut() + "'" +
            ", evaluation=" + getEvaluation() +
            ", appelOffre=" + getAppelOffre() +
            ", candidat=" + getCandidat() +
            "}";
    }
}
