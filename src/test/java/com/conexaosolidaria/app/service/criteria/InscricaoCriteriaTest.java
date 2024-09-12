package com.conexaosolidaria.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class InscricaoCriteriaTest {

    @Test
    void newInscricaoCriteriaHasAllFiltersNullTest() {
        var inscricaoCriteria = new InscricaoCriteria();
        assertThat(inscricaoCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void inscricaoCriteriaFluentMethodsCreatesFiltersTest() {
        var inscricaoCriteria = new InscricaoCriteria();

        setAllFilters(inscricaoCriteria);

        assertThat(inscricaoCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void inscricaoCriteriaCopyCreatesNullFilterTest() {
        var inscricaoCriteria = new InscricaoCriteria();
        var copy = inscricaoCriteria.copy();

        assertThat(inscricaoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(inscricaoCriteria)
        );
    }

    @Test
    void inscricaoCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var inscricaoCriteria = new InscricaoCriteria();
        setAllFilters(inscricaoCriteria);

        var copy = inscricaoCriteria.copy();

        assertThat(inscricaoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(inscricaoCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var inscricaoCriteria = new InscricaoCriteria();

        assertThat(inscricaoCriteria).hasToString("InscricaoCriteria{}");
    }

    private static void setAllFilters(InscricaoCriteria inscricaoCriteria) {
        inscricaoCriteria.id();
        inscricaoCriteria.data();
        inscricaoCriteria.userId();
        inscricaoCriteria.eventoId();
        inscricaoCriteria.distinct();
    }

    private static Condition<InscricaoCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getData()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getEventoId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<InscricaoCriteria> copyFiltersAre(InscricaoCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getData(), copy.getData()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getEventoId(), copy.getEventoId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
