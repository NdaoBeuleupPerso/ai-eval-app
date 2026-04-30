package com.mycompany.iaeval.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Types;
import java.time.Instant;
import java.util.Objects;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * A DTO for the {@link com.mycompany.iaeval.domain.TraceAudit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TraceAuditDTO implements Serializable {

    private Long id;

    @NotNull
    private String action;

    @NotNull
    private Instant horodatage;

    @JdbcTypeCode(Types.LONGVARCHAR)
    private String details;

    private String identifiantUtilisateur;

    @JdbcTypeCode(Types.LONGVARCHAR) // Lob
    private String promptUtilise;

    private EvaluationDTO evaluation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Instant getHorodatage() {
        return horodatage;
    }

    public void setHorodatage(Instant horodatage) {
        this.horodatage = horodatage;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIdentifiantUtilisateur() {
        return identifiantUtilisateur;
    }

    public void setIdentifiantUtilisateur(String identifiantUtilisateur) {
        this.identifiantUtilisateur = identifiantUtilisateur;
    }

    public String getPromptUtilise() {
        return promptUtilise;
    }

    public void setPromptUtilise(String promptUtilise) {
        this.promptUtilise = promptUtilise;
    }

    public EvaluationDTO getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationDTO evaluation) {
        this.evaluation = evaluation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TraceAuditDTO traceAuditDTO)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, traceAuditDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TraceAuditDTO{" +
            "id=" + getId() +
            ", action='" + getAction() + "'" +
            ", horodatage='" + getHorodatage() + "'" +
            ", details='" + getDetails() + "'" +
            ", identifiantUtilisateur='" + getIdentifiantUtilisateur() + "'" +
            ", promptUtilise='" + getPromptUtilise() + "'" +
            ", evaluation=" + getEvaluation() +
            "}";
    }
}
