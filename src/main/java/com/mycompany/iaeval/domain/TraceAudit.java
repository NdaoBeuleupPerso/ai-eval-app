package com.mycompany.iaeval.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TraceAudit.
 */
@Entity
@Table(name = "trace_audit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TraceAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "action", nullable = false)
    private String action;

    @NotNull
    @Column(name = "horodatage", nullable = false)
    private Instant horodatage;

    @Lob
    @Column(name = "details")
    private String details;

    @Column(name = "identifiant_utilisateur")
    private String identifiantUtilisateur;

    @Lob
    @Column(name = "prompt_utilise")
    private String promptUtilise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "traces", "evaluateur", "soumission" }, allowSetters = true)
    private Evaluation evaluation;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TraceAudit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return this.action;
    }

    public TraceAudit action(String action) {
        this.setAction(action);
        return this;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Instant getHorodatage() {
        return this.horodatage;
    }

    public TraceAudit horodatage(Instant horodatage) {
        this.setHorodatage(horodatage);
        return this;
    }

    public void setHorodatage(Instant horodatage) {
        this.horodatage = horodatage;
    }

    public String getDetails() {
        return this.details;
    }

    public TraceAudit details(String details) {
        this.setDetails(details);
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIdentifiantUtilisateur() {
        return this.identifiantUtilisateur;
    }

    public TraceAudit identifiantUtilisateur(String identifiantUtilisateur) {
        this.setIdentifiantUtilisateur(identifiantUtilisateur);
        return this;
    }

    public void setIdentifiantUtilisateur(String identifiantUtilisateur) {
        this.identifiantUtilisateur = identifiantUtilisateur;
    }

    public String getPromptUtilise() {
        return this.promptUtilise;
    }

    public TraceAudit promptUtilise(String promptUtilise) {
        this.setPromptUtilise(promptUtilise);
        return this;
    }

    public void setPromptUtilise(String promptUtilise) {
        this.promptUtilise = promptUtilise;
    }

    public Evaluation getEvaluation() {
        return this.evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public TraceAudit evaluation(Evaluation evaluation) {
        this.setEvaluation(evaluation);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TraceAudit)) {
            return false;
        }
        return getId() != null && getId().equals(((TraceAudit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TraceAudit{" +
            "id=" + getId() +
            ", action='" + getAction() + "'" +
            ", horodatage='" + getHorodatage() + "'" +
            ", details='" + getDetails() + "'" +
            ", identifiantUtilisateur='" + getIdentifiantUtilisateur() + "'" +
            ", promptUtilise='" + getPromptUtilise() + "'" +
            "}";
    }
}
