package com.mycompany.iaeval.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.iaeval.domain.TraceAudit} entity. This class is used
 * in {@link com.mycompany.iaeval.web.rest.TraceAuditResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /trace-audits?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TraceAuditCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter action;

    private InstantFilter horodatage;

    private StringFilter identifiantUtilisateur;

    private LongFilter evaluationId;

    private Boolean distinct;

    public TraceAuditCriteria() {}

    public TraceAuditCriteria(TraceAuditCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.action = other.optionalAction().map(StringFilter::copy).orElse(null);
        this.horodatage = other.optionalHorodatage().map(InstantFilter::copy).orElse(null);
        this.identifiantUtilisateur = other.optionalIdentifiantUtilisateur().map(StringFilter::copy).orElse(null);
        this.evaluationId = other.optionalEvaluationId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TraceAuditCriteria copy() {
        return new TraceAuditCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getAction() {
        return action;
    }

    public Optional<StringFilter> optionalAction() {
        return Optional.ofNullable(action);
    }

    public StringFilter action() {
        if (action == null) {
            setAction(new StringFilter());
        }
        return action;
    }

    public void setAction(StringFilter action) {
        this.action = action;
    }

    public InstantFilter getHorodatage() {
        return horodatage;
    }

    public Optional<InstantFilter> optionalHorodatage() {
        return Optional.ofNullable(horodatage);
    }

    public InstantFilter horodatage() {
        if (horodatage == null) {
            setHorodatage(new InstantFilter());
        }
        return horodatage;
    }

    public void setHorodatage(InstantFilter horodatage) {
        this.horodatage = horodatage;
    }

    public StringFilter getIdentifiantUtilisateur() {
        return identifiantUtilisateur;
    }

    public Optional<StringFilter> optionalIdentifiantUtilisateur() {
        return Optional.ofNullable(identifiantUtilisateur);
    }

    public StringFilter identifiantUtilisateur() {
        if (identifiantUtilisateur == null) {
            setIdentifiantUtilisateur(new StringFilter());
        }
        return identifiantUtilisateur;
    }

    public void setIdentifiantUtilisateur(StringFilter identifiantUtilisateur) {
        this.identifiantUtilisateur = identifiantUtilisateur;
    }

    public LongFilter getEvaluationId() {
        return evaluationId;
    }

    public Optional<LongFilter> optionalEvaluationId() {
        return Optional.ofNullable(evaluationId);
    }

    public LongFilter evaluationId() {
        if (evaluationId == null) {
            setEvaluationId(new LongFilter());
        }
        return evaluationId;
    }

    public void setEvaluationId(LongFilter evaluationId) {
        this.evaluationId = evaluationId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TraceAuditCriteria that = (TraceAuditCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(action, that.action) &&
            Objects.equals(horodatage, that.horodatage) &&
            Objects.equals(identifiantUtilisateur, that.identifiantUtilisateur) &&
            Objects.equals(evaluationId, that.evaluationId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, action, horodatage, identifiantUtilisateur, evaluationId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TraceAuditCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalAction().map(f -> "action=" + f + ", ").orElse("") +
            optionalHorodatage().map(f -> "horodatage=" + f + ", ").orElse("") +
            optionalIdentifiantUtilisateur().map(f -> "identifiantUtilisateur=" + f + ", ").orElse("") +
            optionalEvaluationId().map(f -> "evaluationId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
