package com.mycompany.iaeval.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Types;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * A Evaluation.
 */
@Entity
@Table(name = "evaluation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Evaluation implements Serializable {

    //private static final long serialVersionUID = 1L;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "evaluation", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "evaluation" }, allowSetters = true)
    private Set<TraceAudit> traces = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private User evaluateur;

    @JsonIgnoreProperties(value = { "evaluation", "documents", "appelOffre", "candidat" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "evaluation")
    private Soumission soumission;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Evaluation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getScoreGlobal() {
        return this.scoreGlobal;
    }

    public Evaluation scoreGlobal(Double scoreGlobal) {
        this.setScoreGlobal(scoreGlobal);
        return this;
    }

    public void setScoreGlobal(Double scoreGlobal) {
        this.scoreGlobal = scoreGlobal;
    }

    public Double getScoreAdmin() {
        return this.scoreAdmin;
    }

    public Evaluation scoreAdmin(Double scoreAdmin) {
        this.setScoreAdmin(scoreAdmin);
        return this;
    }

    public void setScoreAdmin(Double scoreAdmin) {
        this.scoreAdmin = scoreAdmin;
    }

    public Double getScoreTech() {
        return this.scoreTech;
    }

    public Evaluation scoreTech(Double scoreTech) {
        this.setScoreTech(scoreTech);
        return this;
    }

    public void setScoreTech(Double scoreTech) {
        this.scoreTech = scoreTech;
    }

    public Double getScoreFin() {
        return this.scoreFin;
    }

    public Evaluation scoreFin(Double scoreFin) {
        this.setScoreFin(scoreFin);
        return this;
    }

    public void setScoreFin(Double scoreFin) {
        this.scoreFin = scoreFin;
    }

    public String getRapportAnalyse() {
        return this.rapportAnalyse;
    }

    public Evaluation rapportAnalyse(String rapportAnalyse) {
        this.setRapportAnalyse(rapportAnalyse);
        return this;
    }

    public void setRapportAnalyse(String rapportAnalyse) {
        this.rapportAnalyse = rapportAnalyse;
    }

    public String getDocumentPv() {
        return this.documentPv;
    }

    public Evaluation documentPv(String documentPv) {
        this.setDocumentPv(documentPv);
        return this;
    }

    public void setDocumentPv(String documentPv) {
        this.documentPv = documentPv;
    }

    public String getDocumentPvContentType() {
        return this.documentPvContentType;
    }

    public Evaluation documentPvContentType(String documentPvContentType) {
        this.documentPvContentType = documentPvContentType;
        return this;
    }

    public void setDocumentPvContentType(String documentPvContentType) {
        this.documentPvContentType = documentPvContentType;
    }

    public Instant getDateEvaluation() {
        return this.dateEvaluation;
    }

    public Evaluation dateEvaluation(Instant dateEvaluation) {
        this.setDateEvaluation(dateEvaluation);
        return this;
    }

    public void setDateEvaluation(Instant dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }

    public Boolean getEstValidee() {
        return this.estValidee;
    }

    public Evaluation estValidee(Boolean estValidee) {
        this.setEstValidee(estValidee);
        return this;
    }

    public void setEstValidee(Boolean estValidee) {
        this.estValidee = estValidee;
    }

    public String getCommentaireEvaluateur() {
        return this.commentaireEvaluateur;
    }

    public Evaluation commentaireEvaluateur(String commentaireEvaluateur) {
        this.setCommentaireEvaluateur(commentaireEvaluateur);
        return this;
    }

    public void setCommentaireEvaluateur(String commentaireEvaluateur) {
        this.commentaireEvaluateur = commentaireEvaluateur;
    }

    public Set<TraceAudit> getTraces() {
        return this.traces;
    }

    public void setTraces(Set<TraceAudit> traceAudits) {
        if (this.traces != null) {
            this.traces.forEach(i -> i.setEvaluation(null));
        }
        if (traceAudits != null) {
            traceAudits.forEach(i -> i.setEvaluation(this));
        }
        this.traces = traceAudits;
    }

    public Evaluation traces(Set<TraceAudit> traceAudits) {
        this.setTraces(traceAudits);
        return this;
    }

    public Evaluation addTraces(TraceAudit traceAudit) {
        this.traces.add(traceAudit);
        traceAudit.setEvaluation(this);
        return this;
    }

    public Evaluation removeTraces(TraceAudit traceAudit) {
        this.traces.remove(traceAudit);
        traceAudit.setEvaluation(null);
        return this;
    }

    public User getEvaluateur() {
        return this.evaluateur;
    }

    public void setEvaluateur(User user) {
        this.evaluateur = user;
    }

    public Evaluation evaluateur(User user) {
        this.setEvaluateur(user);
        return this;
    }

    public Soumission getSoumission() {
        return this.soumission;
    }

    public void setSoumission(Soumission soumission) {
        if (this.soumission != null) {
            this.soumission.setEvaluation(null);
        }
        if (soumission != null) {
            soumission.setEvaluation(this);
        }
        this.soumission = soumission;
    }

    public Evaluation soumission(Soumission soumission) {
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
