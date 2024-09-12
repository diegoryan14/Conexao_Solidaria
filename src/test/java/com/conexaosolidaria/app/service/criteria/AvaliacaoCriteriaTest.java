package com.conexaosolidaria.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AvaliacaoCriteriaTest {

    @Test
    void newAvaliacaoCriteriaHasAllFiltersNullTest() {
        var avaliacaoCriteria = new AvaliacaoCriteria();
        assertThat(avaliacaoCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void avaliacaoCriteriaFluentMethodsCreatesFiltersTest() {
        var avaliacaoCriteria = new AvaliacaoCriteria();

        setAllFilters(avaliacaoCriteria);

        assertThat(avaliacaoCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void avaliacaoCriteriaCopyCreatesNullFilterTest() {
        var avaliacaoCriteria = new AvaliacaoCriteria();
        var copy = avaliacaoCriteria.copy();

        assertThat(avaliacaoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(avaliacaoCriteria)
        );
    }

    @Test
    void avaliacaoCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var avaliacaoCriteria = new AvaliacaoCriteria();
        setAllFilters(avaliacaoCriteria);

        var copy = avaliacaoCriteria.copy();

        assertThat(avaliacaoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(avaliacaoCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var avaliacaoCriteria = new AvaliacaoCriteria();

        assertThat(avaliacaoCriteria).hasToString("AvaliacaoCriteria{}");
    }

    private static void setAllFilters(AvaliacaoCriteria avaliacaoCriteria) {
        avaliacaoCriteria.id();
        avaliacaoCriteria.estrelas();
        avaliacaoCriteria.observacao();
        avaliacaoCriteria.userId();
        avaliacaoCriteria.eventoId();
        avaliacaoCriteria.distinct();
    }

    private static Condition<AvaliacaoCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getEstrelas()) &&
                condition.apply(criteria.getObservacao()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getEventoId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AvaliacaoCriteria> copyFiltersAre(AvaliacaoCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getEstrelas(), copy.getEstrelas()) &&
                condition.apply(criteria.getObservacao(), copy.getObservacao()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getEventoId(), copy.getEventoId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
