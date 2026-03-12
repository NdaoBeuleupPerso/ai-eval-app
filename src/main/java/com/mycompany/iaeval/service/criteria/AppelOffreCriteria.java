package com.mycompany.iaeval.service.criteria;

import com.mycompany.iaeval.domain.enumeration.StatutAppel;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.iaeval.domain.AppelOffre} entity. This class is used
 * in {@link com.mycompany.iaeval.web.rest.AppelOffreResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /appel-offres?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppelOffreCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StatutAppel
     */
    public static class StatutAppelFilter extends Filter<StatutAppel> {

        public StatutAppelFilter() {}

        public StatutAppelFilter(StatutAppelFilter filter) {
            super(filter);
        }

        @Override
        public StatutAppelFilter copy() {
            return new StatutAppelFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private StringFilter titre;

    private InstantFilter dateCloture;

    private StatutAppelFilter statut;

    private LongFilter criteresId;

    private LongFilter soumissionsId;

    private Boolean distinct;

    public AppelOffreCriteria() {}

    public AppelOffreCriteria(AppelOffreCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.titre = other.optionalTitre().map(StringFilter::copy).orElse(null);
        this.dateCloture = other.optionalDateCloture().map(InstantFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutAppelFilter::copy).orElse(null);
        this.criteresId = other.optionalCriteresId().map(LongFilter::copy).orElse(null);
        this.soumissionsId = other.optionalSoumissionsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AppelOffreCriteria copy() {
        return new AppelOffreCriteria(this);
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

    public StringFilter getReference() {
        return reference;
    }

    public Optional<StringFilter> optionalReference() {
        return Optional.ofNullable(reference);
    }

    public StringFilter reference() {
        if (reference == null) {
            setReference(new StringFilter());
        }
        return reference;
    }

    public void setReference(StringFilter reference) {
        this.reference = reference;
    }

    public StringFilter getTitre() {
        return titre;
    }

    public Optional<StringFilter> optionalTitre() {
        return Optional.ofNullable(titre);
    }

    public StringFilter titre() {
        if (titre == null) {
            setTitre(new StringFilter());
        }
        return titre;
    }

    public void setTitre(StringFilter titre) {
        this.titre = titre;
    }

    public InstantFilter getDateCloture() {
        return dateCloture;
    }

    public Optional<InstantFilter> optionalDateCloture() {
        return Optional.ofNullable(dateCloture);
    }

    public InstantFilter dateCloture() {
        if (dateCloture == null) {
            setDateCloture(new InstantFilter());
        }
        return dateCloture;
    }

    public void setDateCloture(InstantFilter dateCloture) {
        this.dateCloture = dateCloture;
    }

    public StatutAppelFilter getStatut() {
        return statut;
    }

    public Optional<StatutAppelFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutAppelFilter statut() {
        if (statut == null) {
            setStatut(new StatutAppelFilter());
        }
        return statut;
    }

    public void setStatut(StatutAppelFilter statut) {
        this.statut = statut;
    }

    public LongFilter getCriteresId() {
        return criteresId;
    }

    public Optional<LongFilter> optionalCriteresId() {
        return Optional.ofNullable(criteresId);
    }

    public LongFilter criteresId() {
        if (criteresId == null) {
            setCriteresId(new LongFilter());
        }
        return criteresId;
    }

    public void setCriteresId(LongFilter criteresId) {
        this.criteresId = criteresId;
    }

    public LongFilter getSoumissionsId() {
        return soumissionsId;
    }

    public Optional<LongFilter> optionalSoumissionsId() {
        return Optional.ofNullable(soumissionsId);
    }

    public LongFilter soumissionsId() {
        if (soumissionsId == null) {
            setSoumissionsId(new LongFilter());
        }
        return soumissionsId;
    }

    public void setSoumissionsId(LongFilter soumissionsId) {
        this.soumissionsId = soumissionsId;
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
        final AppelOffreCriteria that = (AppelOffreCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(titre, that.titre) &&
            Objects.equals(dateCloture, that.dateCloture) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(criteresId, that.criteresId) &&
            Objects.equals(soumissionsId, that.soumissionsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, titre, dateCloture, statut, criteresId, soumissionsId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppelOffreCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalTitre().map(f -> "titre=" + f + ", ").orElse("") +
            optionalDateCloture().map(f -> "dateCloture=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalCriteresId().map(f -> "criteresId=" + f + ", ").orElse("") +
            optionalSoumissionsId().map(f -> "soumissionsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
