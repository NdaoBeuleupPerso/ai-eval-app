package com.mycompany.iaeval.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AppelOffreCriteriaTest {

    @Test
    void newAppelOffreCriteriaHasAllFiltersNullTest() {
        var appelOffreCriteria = new AppelOffreCriteria();
        assertThat(appelOffreCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void appelOffreCriteriaFluentMethodsCreatesFiltersTest() {
        var appelOffreCriteria = new AppelOffreCriteria();

        setAllFilters(appelOffreCriteria);

        assertThat(appelOffreCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void appelOffreCriteriaCopyCreatesNullFilterTest() {
        var appelOffreCriteria = new AppelOffreCriteria();
        var copy = appelOffreCriteria.copy();

        assertThat(appelOffreCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(appelOffreCriteria)
        );
    }

    @Test
    void appelOffreCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var appelOffreCriteria = new AppelOffreCriteria();
        setAllFilters(appelOffreCriteria);

        var copy = appelOffreCriteria.copy();

        assertThat(appelOffreCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(appelOffreCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var appelOffreCriteria = new AppelOffreCriteria();

        assertThat(appelOffreCriteria).hasToString("AppelOffreCriteria{}");
    }

    private static void setAllFilters(AppelOffreCriteria appelOffreCriteria) {
        appelOffreCriteria.id();
        appelOffreCriteria.reference();
        appelOffreCriteria.titre();
        appelOffreCriteria.dateCloture();
        appelOffreCriteria.statut();
        appelOffreCriteria.criteresId();
        appelOffreCriteria.soumissionsId();
        appelOffreCriteria.distinct();
    }

    private static Condition<AppelOffreCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getTitre()) &&
                condition.apply(criteria.getDateCloture()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getCriteresId()) &&
                condition.apply(criteria.getSoumissionsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AppelOffreCriteria> copyFiltersAre(AppelOffreCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getTitre(), copy.getTitre()) &&
                condition.apply(criteria.getDateCloture(), copy.getDateCloture()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getCriteresId(), copy.getCriteresId()) &&
                condition.apply(criteria.getSoumissionsId(), copy.getSoumissionsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
