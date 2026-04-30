package com.mycompany.iaeval.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Types;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "evaluation_candidat")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class EvaluationCandidat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "score_global")
    private Double scoreGlobal;

    @Column(name = "score_admin")
    private Double scoreAdmin;

    @Column(name = "score_tech")
    private Double scoreTech;

    @Column(name = "score_fin")
    private Double scoreFin;

    //@L
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "rapport_analyse")
    private String rapportAnalyse;

    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "document_pv")
    private String documentPv;

    @Column(name = "document_pv_content_type")
    private String documentPvContentType;

    @Column(name = "date_evaluation")
    private Instant dateEvaluation;

    @Column(name = "est_validee")
    private Boolean estValidee;

    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "commentaire_evaluateur")
    private String commentaireEvaluateur;

    // CORRECTION ICI : mappedBy doit pointer vers "evaluation_candidat"
    @JsonIgnoreProperties(value = { "evaluation", "evaluation_candidat", "documents", "appelOffre", "candidat" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "evaluation_candidat")
    private Soumission soumission;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getScoreGlobal() {
        return this.scoreGlobal;
    }

    public void setDateEvaluation(Instant dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }

    public Instant getDateEvaluation() {
        return this.dateEvaluation;
    }

    public Boolean getEstValidee() {
        return this.estValidee;
    }

    public void setEstValidee(Boolean estValidee) {
        this.estValidee = estValidee;
    }

    public void setScoreGlobal(Double scoreGlobal) {
        this.scoreGlobal = scoreGlobal;
    }

    public Double getScoreAdmin() {
        return this.scoreAdmin;
    }

    public void setScoreAdmin(Double scoreAdmin) {
        this.scoreAdmin = scoreAdmin;
    }

    public Double getScoreTech() {
        return this.scoreTech;
    }

    public void setScoreTech(Double scoreTech) {
        this.scoreTech = scoreTech;
    }

    public Double getScoreFin() {
        return this.scoreFin;
    }

    public void setScoreFin(Double scoreFin) {
        this.scoreFin = scoreFin;
    }

    public String getRapportAnalyse() {
        return this.rapportAnalyse;
    }

    public void setRapportAnalyse(String rapportAnalyse) {
        this.rapportAnalyse = rapportAnalyse;
    }

    public String getDocumentPv() {
        return this.documentPv;
    }

    public void setDocumentPv(String documentPv) {
        this.documentPv = documentPv;
    }

    public String getDocumentPvContentType() {
        return this.documentPvContentType;
    }

    public void setDocumentPvContentType(String documentPvContentType) {
        this.documentPvContentType = documentPvContentType;
    }

    public Soumission getSoumission() {
        return this.soumission;
    }

    public void setSoumission(Soumission soumission) {
        // CORRECTION ICI : On gère la relation avec le bon champ
        if (this.soumission != null) {
            this.soumission.setEvaluation_candidat(null);
        }
        if (soumission != null) {
            soumission.setEvaluation_candidat(this);
        }
        this.soumission = soumission;
    }

    public EvaluationCandidat soumission(Soumission soumission) {
        this.setSoumission(soumission);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EvaluationCandidat)) { // CORRECTION : Type de classe correct
            return false;
        }
        return getId() != null && getId().equals(((EvaluationCandidat) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "EvaluationCandidat{" + // CORRECTION : Nom de classe correct
            "id=" +
            getId() +
            ", scoreGlobal=" +
            getScoreGlobal() +
            ", rapportAnalyse='" +
            getRapportAnalyse() +
            "'" +
            "}"
        );
    }
}
