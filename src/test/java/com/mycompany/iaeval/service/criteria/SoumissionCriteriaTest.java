package com.mycompany.iaeval.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SoumissionCriteriaTest {

    @Test
    void newSoumissionCriteriaHasAllFiltersNullTest() {
        var soumissionCriteria = new SoumissionCriteria();
        assertThat(soumissionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void soumissionCriteriaFluentMethodsCreatesFiltersTest() {
        var soumissionCriteria = new SoumissionCriteria();

        setAllFilters(soumissionCriteria);

        assertThat(soumissionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void soumissionCriteriaCopyCreatesNullFilterTest() {
        var soumissionCriteria = new SoumissionCriteria();
        var copy = soumissionCriteria.copy();

        assertThat(soumissionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(soumissionCriteria)
        );
    }

    @Test
    void soumissionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var soumissionCriteria = new SoumissionCriteria();
        setAllFilters(soumissionCriteria);

        var copy = soumissionCriteria.copy();

        assertThat(soumissionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(soumissionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var soumissionCriteria = new SoumissionCriteria();

        assertThat(soumissionCriteria).hasToString("SoumissionCriteria{}");
    }

    private static void setAllFilters(SoumissionCriteria soumissionCriteria) {
        soumissionCriteria.id();
        soumissionCriteria.dateSoumission();
        soumissionCriteria.statut();
        soumissionCriteria.evaluationId();
        soumissionCriteria.documentsId();
        soumissionCriteria.appelOffreId();
        soumissionCriteria.candidatId();
        soumissionCriteria.distinct();
    }

    private static Condition<SoumissionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDateSoumission()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getEvaluationId()) &&
                condition.apply(criteria.getDocumentsId()) &&
                condition.apply(criteria.getAppelOffreId()) &&
                condition.apply(criteria.getCandidatId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SoumissionCriteria> copyFiltersAre(SoumissionCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDateSoumission(), copy.getDateSoumission()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getEvaluationId(), copy.getEvaluationId()) &&
                condition.apply(criteria.getDocumentsId(), copy.getDocumentsId()) &&
                condition.apply(criteria.getAppelOffreId(), copy.getAppelOffreId()) &&
                condition.apply(criteria.getCandidatId(), copy.getCandidatId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
