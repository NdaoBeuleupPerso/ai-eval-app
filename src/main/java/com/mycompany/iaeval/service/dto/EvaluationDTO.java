package com.mycompany.iaeval.service.dto;

import jakarta.persistence.Lob;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.iaeval.domain.Evaluation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EvaluationDTO implements Serializable {

    private Long id;

    private Double scoreGlobal;

    private Double scoreAdmin;

    private Double scoreTech;

    private Double scoreFin;

    @Lob
    private String rapportAnalyse;

    @Lob
    private byte[] documentPv;

    private String documentPvContentType;

    private Instant dateEvaluation;

    private Boolean estValidee;

    @Lob
    private String commentaireEvaluateur;

    private UserDTO evaluateur;

    private SoumissionDTO soumission; // Ajoutez ceci

    // Ajoutez le Getter et le Setter
    public SoumissionDTO getSoumission() {
        return soumission;
    }

    public void setSoumission(SoumissionDTO soumission) {
        this.soumission = soumission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getScoreGlobal() {
        return scoreGlobal;
    }

    public void setScoreGlobal(Double scoreGlobal) {
        this.scoreGlobal = scoreGlobal;
    }

    public Double getScoreAdmin() {
        return scoreAdmin;
    }

    public void setScoreAdmin(Double scoreAdmin) {
        this.scoreAdmin = scoreAdmin;
    }

    public Double getScoreTech() {
        return scoreTech;
    }

    public void setScoreTech(Double scoreTech) {
        this.scoreTech = scoreTech;
    }

    public Double getScoreFin() {
        return scoreFin;
    }

    public void setScoreFin(Double scoreFin) {
        this.scoreFin = scoreFin;
    }

    public String getRapportAnalyse() {
        return rapportAnalyse;
    }

    public void setRapportAnalyse(String rapportAnalyse) {
        this.rapportAnalyse = rapportAnalyse;
    }

    public byte[] getDocumentPv() {
        return documentPv;
    }

    public void setDocumentPv(byte[] documentPv) {
        this.documentPv = documentPv;
    }

    public String getDocumentPvContentType() {
        return documentPvContentType;
    }

    public void setDocumentPvContentType(String documentPvContentType) {
        this.documentPvContentType = documentPvContentType;
    }

    public Instant getDateEvaluation() {
        return dateEvaluation;
    }

    public void setDateEvaluation(Instant dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }

    public Boolean getEstValidee() {
        return estValidee;
    }

    public void setEstValidee(Boolean estValidee) {
        this.estValidee = estValidee;
    }

    public String getCommentaireEvaluateur() {
        return commentaireEvaluateur;
    }

    public void setCommentaireEvaluateur(String commentaireEvaluateur) {
        this.commentaireEvaluateur = commentaireEvaluateur;
    }

    public UserDTO getEvaluateur() {
        return evaluateur;
    }

    public void setEvaluateur(UserDTO evaluateur) {
        this.evaluateur = evaluateur;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EvaluationDTO evaluationDTO)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, evaluationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EvaluationDTO{" +
            "id=" + getId() +
            ", scoreGlobal=" + getScoreGlobal() +
            ", scoreAdmin=" + getScoreAdmin() +
            ", scoreTech=" + getScoreTech() +
            ", scoreFin=" + getScoreFin() +
            ", rapportAnalyse='" + getRapportAnalyse() + "'" +
            ", documentPv='" + getDocumentPv() + "'" +
            ", dateEvaluation='" + getDateEvaluation() + "'" +
            ", estValidee='" + getEstValidee() + "'" +
            ", commentaireEvaluateur='" + getCommentaireEvaluateur() + "'" +
            ", evaluateur=" + getEvaluateur() +
            "}";
    }
}
