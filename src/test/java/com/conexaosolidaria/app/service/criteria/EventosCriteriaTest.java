package com.conexaosolidaria.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class EventosCriteriaTest {

    @Test
    void newEventosCriteriaHasAllFiltersNullTest() {
        var eventosCriteria = new EventosCriteria();
        assertThat(eventosCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void eventosCriteriaFluentMethodsCreatesFiltersTest() {
        var eventosCriteria = new EventosCriteria();

        setAllFilters(eventosCriteria);

        assertThat(eventosCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void eventosCriteriaCopyCreatesNullFilterTest() {
        var eventosCriteria = new EventosCriteria();
        var copy = eventosCriteria.copy();

        assertThat(eventosCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(eventosCriteria)
        );
    }

    @Test
    void eventosCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var eventosCriteria = new EventosCriteria();
        setAllFilters(eventosCriteria);

        var copy = eventosCriteria.copy();

        assertThat(eventosCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(eventosCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var eventosCriteria = new EventosCriteria();

        assertThat(eventosCriteria).hasToString("EventosCriteria{}");
    }

    private static void setAllFilters(EventosCriteria eventosCriteria) {
        eventosCriteria.id();
        eventosCriteria.nome();
        eventosCriteria.dataCadastro();
        eventosCriteria.dataEvento();
        eventosCriteria.horaInicio();
        eventosCriteria.horaTermino();
        eventosCriteria.observacao();
        eventosCriteria.userId();
        eventosCriteria.distinct();
    }

    private static Condition<EventosCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNome()) &&
                condition.apply(criteria.getDataCadastro()) &&
                condition.apply(criteria.getDataEvento()) &&
                condition.apply(criteria.getHoraInicio()) &&
                condition.apply(criteria.getHoraTermino()) &&
                condition.apply(criteria.getObservacao()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<EventosCriteria> copyFiltersAre(EventosCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNome(), copy.getNome()) &&
                condition.apply(criteria.getDataCadastro(), copy.getDataCadastro()) &&
                condition.apply(criteria.getDataEvento(), copy.getDataEvento()) &&
                condition.apply(criteria.getHoraInicio(), copy.getHoraInicio()) &&
                condition.apply(criteria.getHoraTermino(), copy.getHoraTermino()) &&
                condition.apply(criteria.getObservacao(), copy.getObservacao()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
