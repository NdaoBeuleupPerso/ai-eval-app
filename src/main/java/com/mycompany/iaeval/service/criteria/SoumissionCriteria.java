package com.mycompany.iaeval.service.criteria;

import com.mycompany.iaeval.domain.enumeration.StatutEvaluation;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.iaeval.domain.Soumission} entity. This class is used
 * in {@link com.mycompany.iaeval.web.rest.SoumissionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /soumissions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SoumissionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StatutEvaluation
     */
    public static class StatutEvaluationFilter extends Filter<StatutEvaluation> {

        public StatutEvaluationFilter() {}

        public StatutEvaluationFilter(StatutEvaluationFilter filter) {
            super(filter);
        }

        @Override
        public StatutEvaluationFilter copy() {
            return new StatutEvaluationFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter dateSoumission;

    private StatutEvaluationFilter statut;

    private LongFilter evaluationId;

    private LongFilter documentsId;

    private LongFilter appelOffreId;

    private LongFilter candidatId;

    private Boolean distinct;

    public SoumissionCriteria() {}

    public SoumissionCriteria(SoumissionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.dateSoumission = other.optionalDateSoumission().map(InstantFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutEvaluationFilter::copy).orElse(null);
        this.evaluationId = other.optionalEvaluationId().map(LongFilter::copy).orElse(null);
        this.documentsId = other.optionalDocumentsId().map(LongFilter::copy).orElse(null);
        this.appelOffreId = other.optionalAppelOffreId().map(LongFilter::copy).orElse(null);
        this.candidatId = other.optionalCandidatId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public SoumissionCriteria copy() {
        return new SoumissionCriteria(this);
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

    public InstantFilter getDateSoumission() {
        return dateSoumission;
    }

    public Optional<InstantFilter> optionalDateSoumission() {
        return Optional.ofNullable(dateSoumission);
    }

    public InstantFilter dateSoumission() {
        if (dateSoumission == null) {
            setDateSoumission(new InstantFilter());
        }
        return dateSoumission;
    }

    public void setDateSoumission(InstantFilter dateSoumission) {
        this.dateSoumission = dateSoumission;
    }

    public StatutEvaluationFilter getStatut() {
        return statut;
    }

    public Optional<StatutEvaluationFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutEvaluationFilter statut() {
        if (statut == null) {
            setStatut(new StatutEvaluationFilter());
        }
        return statut;
    }

    public void setStatut(StatutEvaluationFilter statut) {
        this.statut = statut;
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

    public LongFilter getDocumentsId() {
        return documentsId;
    }

    public Optional<LongFilter> optionalDocumentsId() {
        return Optional.ofNullable(documentsId);
    }

    public LongFilter documentsId() {
        if (documentsId == null) {
            setDocumentsId(new LongFilter());
        }
        return documentsId;
    }

    public void setDocumentsId(LongFilter documentsId) {
        this.documentsId = documentsId;
    }

    public LongFilter getAppelOffreId() {
        return appelOffreId;
    }

    public Optional<LongFilter> optionalAppelOffreId() {
        return Optional.ofNullable(appelOffreId);
    }

    public LongFilter appelOffreId() {
        if (appelOffreId == null) {
            setAppelOffreId(new LongFilter());
        }
        return appelOffreId;
    }

    public void setAppelOffreId(LongFilter appelOffreId) {
        this.appelOffreId = appelOffreId;
    }

    public LongFilter getCandidatId() {
        return candidatId;
    }

    public Optional<LongFilter> optionalCandidatId() {
        return Optional.ofNullable(candidatId);
    }

    public LongFilter candidatId() {
        if (candidatId == null) {
            setCandidatId(new LongFilter());
        }
        return candidatId;
    }

    public void setCandidatId(LongFilter candidatId) {
        this.candidatId = candidatId;
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
        final SoumissionCriteria that = (SoumissionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(dateSoumission, that.dateSoumission) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(evaluationId, that.evaluationId) &&
            Objects.equals(documentsId, that.documentsId) &&
            Objects.equals(appelOffreId, that.appelOffreId) &&
            Objects.equals(candidatId, that.candidatId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateSoumission, statut, evaluationId, documentsId, appelOffreId, candidatId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SoumissionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalDateSoumission().map(f -> "dateSoumission=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalEvaluationId().map(f -> "evaluationId=" + f + ", ").orElse("") +
            optionalDocumentsId().map(f -> "documentsId=" + f + ", ").orElse("") +
            optionalAppelOffreId().map(f -> "appelOffreId=" + f + ", ").orElse("") +
            optionalCandidatId().map(f -> "candidatId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
