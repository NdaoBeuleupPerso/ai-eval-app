package com.mycompany.iaeval.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "evaluation_candidat")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
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

    @Lob
    @Column(name = "rapport_analyse")
    private String rapportAnalyse;

    @Lob
    @Column(name = "document_pv")
    private byte[] documentPv;

    @Column(name = "document_pv_content_type")
    private String documentPvContentType;

    @Column(name = "date_evaluation")
    private Instant dateEvaluation;

    @Column(name = "est_validee")
    private Boolean estValidee;

    @Lob
    @Column(name = "commentaire_evaluateur")
    private String commentaireEvaluateur;

    @JsonIgnoreProperties(value = { "evaluation", "documents", "appelOffre", "candidat" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "evaluation")
    private Soumission soumission;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EvaluationCandidat id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getScoreGlobal() {
        return this.scoreGlobal;
    }

    public EvaluationCandidat scoreGlobal(Double scoreGlobal) {
        this.setScoreGlobal(scoreGlobal);
        return this;
    }

    public void setScoreGlobal(Double scoreGlobal) {
        this.scoreGlobal = scoreGlobal;
    }

    public Double getScoreAdmin() {
        return this.scoreAdmin;
    }

    public EvaluationCandidat scoreAdmin(Double scoreAdmin) {
        this.setScoreAdmin(scoreAdmin);
        return this;
    }

    public void setScoreAdmin(Double scoreAdmin) {
        this.scoreAdmin = scoreAdmin;
    }

    public Double getScoreTech() {
        return this.scoreTech;
    }

    public EvaluationCandidat scoreTech(Double scoreTech) {
        this.setScoreTech(scoreTech);
        return this;
    }

    public void setScoreTech(Double scoreTech) {
        this.scoreTech = scoreTech;
    }

    public Double getScoreFin() {
        return this.scoreFin;
    }

    public EvaluationCandidat scoreFin(Double scoreFin) {
        this.setScoreFin(scoreFin);
        return this;
    }

    public void setScoreFin(Double scoreFin) {
        this.scoreFin = scoreFin;
    }

    public String getRapportAnalyse() {
        return this.rapportAnalyse;
    }

    public EvaluationCandidat rapportAnalyse(String rapportAnalyse) {
        this.setRapportAnalyse(rapportAnalyse);
        return this;
    }

    public void setRapportAnalyse(String rapportAnalyse) {
        this.rapportAnalyse = rapportAnalyse;
    }

    public byte[] getDocumentPv() {
        return this.documentPv;
    }

    public EvaluationCandidat documentPv(byte[] documentPv) {
        this.setDocumentPv(documentPv);
        return this;
    }

    public void setDocumentPv(byte[] documentPv) {
        this.documentPv = documentPv;
    }

    public String getDocumentPvContentType() {
        return this.documentPvContentType;
    }

    public EvaluationCandidat documentPvContentType(String documentPvContentType) {
        this.documentPvContentType = documentPvContentType;
        return this;
    }

    public void setDocumentPvContentType(String documentPvContentType) {
        this.documentPvContentType = documentPvContentType;
    }

    public Instant getDateEvaluation() {
        return this.dateEvaluation;
    }

    public EvaluationCandidat dateEvaluation(Instant dateEvaluation) {
        this.setDateEvaluation(dateEvaluation);
        return this;
    }

    public void setDateEvaluation(Instant dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }

    public Boolean getEstValidee() {
        return this.estValidee;
    }

    public EvaluationCandidat estValidee(Boolean estValidee) {
        this.setEstValidee(estValidee);
        return this;
    }

    public void setEstValidee(Boolean estValidee) {
        this.estValidee = estValidee;
    }

    public String getCommentaireEvaluateur() {
        return this.commentaireEvaluateur;
    }

    public EvaluationCandidat commentaireEvaluateur(String commentaireEvaluateur) {
        this.setCommentaireEvaluateur(commentaireEvaluateur);
        return this;
    }

    public void setCommentaireEvaluateur(String commentaireEvaluateur) {
        this.commentaireEvaluateur = commentaireEvaluateur;
    }

    public Soumission getSoumission() {
        return this.soumission;
    }

    public void setSoumission(Soumission soumission) {
        if (this.soumission != null) {
            this.soumission.setEvaluation(null);
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

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Evaluation)) {
            return false;
        }
        return getId() != null && getId().equals(((Evaluation) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Evaluation{" +
            "id=" + getId() +
            ", scoreGlobal=" + getScoreGlobal() +
            ", scoreAdmin=" + getScoreAdmin() +
            ", scoreTech=" + getScoreTech() +
            ", scoreFin=" + getScoreFin() +
            ", rapportAnalyse='" + getRapportAnalyse() + "'" +
            ", documentPv='" + getDocumentPv() + "'" +
            ", documentPvContentType='" + getDocumentPvContentType() + "'" +
            ", dateEvaluation='" + getDateEvaluation() + "'" +
            ", estValidee='" + getEstValidee() + "'" +
            ", commentaireEvaluateur='" + getCommentaireEvaluateur() + "'" +
            "}";
    }
}
