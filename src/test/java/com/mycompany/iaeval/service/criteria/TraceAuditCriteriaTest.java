package com.mycompany.iaeval.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TraceAuditCriteriaTest {

    @Test
    void newTraceAuditCriteriaHasAllFiltersNullTest() {
        var traceAuditCriteria = new TraceAuditCriteria();
        assertThat(traceAuditCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void traceAuditCriteriaFluentMethodsCreatesFiltersTest() {
        var traceAuditCriteria = new TraceAuditCriteria();

        setAllFilters(traceAuditCriteria);

        assertThat(traceAuditCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void traceAuditCriteriaCopyCreatesNullFilterTest() {
        var traceAuditCriteria = new TraceAuditCriteria();
        var copy = traceAuditCriteria.copy();

        assertThat(traceAuditCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(traceAuditCriteria)
        );
    }

    @Test
    void traceAuditCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var traceAuditCriteria = new TraceAuditCriteria();
        setAllFilters(traceAuditCriteria);

        var copy = traceAuditCriteria.copy();

        assertThat(traceAuditCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(traceAuditCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var traceAuditCriteria = new TraceAuditCriteria();

        assertThat(traceAuditCriteria).hasToString("TraceAuditCriteria{}");
    }

    private static void setAllFilters(TraceAuditCriteria traceAuditCriteria) {
        traceAuditCriteria.id();
        traceAuditCriteria.action();
        traceAuditCriteria.horodatage();
        traceAuditCriteria.identifiantUtilisateur();
        traceAuditCriteria.evaluationId();
        traceAuditCriteria.distinct();
    }

    private static Condition<TraceAuditCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getAction()) &&
                condition.apply(criteria.getHorodatage()) &&
                condition.apply(criteria.getIdentifiantUtilisateur()) &&
                condition.apply(criteria.getEvaluationId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TraceAuditCriteria> copyFiltersAre(TraceAuditCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getAction(), copy.getAction()) &&
                condition.apply(criteria.getHorodatage(), copy.getHorodatage()) &&
                condition.apply(criteria.getIdentifiantUtilisateur(), copy.getIdentifiantUtilisateur()) &&
                condition.apply(criteria.getEvaluationId(), copy.getEvaluationId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
