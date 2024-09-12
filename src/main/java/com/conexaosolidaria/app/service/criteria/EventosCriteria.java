package com.conexaosolidaria.app.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.conexaosolidaria.app.domain.Eventos} entity. This class is used
 * in {@link com.conexaosolidaria.app.web.rest.EventosResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /eventos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventosCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nome;

    private InstantFilter dataCadastro;

    private InstantFilter dataEvento;

    private StringFilter horaInicio;

    private StringFilter horaTermino;

    private StringFilter observacao;

    private LongFilter userId;

    private Boolean distinct;

    public EventosCriteria() {}

    public EventosCriteria(EventosCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nome = other.optionalNome().map(StringFilter::copy).orElse(null);
        this.dataCadastro = other.optionalDataCadastro().map(InstantFilter::copy).orElse(null);
        this.dataEvento = other.optionalDataEvento().map(InstantFilter::copy).orElse(null);
        this.horaInicio = other.optionalHoraInicio().map(StringFilter::copy).orElse(null);
        this.horaTermino = other.optionalHoraTermino().map(StringFilter::copy).orElse(null);
        this.observacao = other.optionalObservacao().map(StringFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EventosCriteria copy() {
        return new EventosCriteria(this);
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

    public StringFilter getNome() {
        return nome;
    }

    public Optional<StringFilter> optionalNome() {
        return Optional.ofNullable(nome);
    }

    public StringFilter nome() {
        if (nome == null) {
            setNome(new StringFilter());
        }
        return nome;
    }

    public void setNome(StringFilter nome) {
        this.nome = nome;
    }

    public InstantFilter getDataCadastro() {
        return dataCadastro;
    }

    public Optional<InstantFilter> optionalDataCadastro() {
        return Optional.ofNullable(dataCadastro);
    }

    public InstantFilter dataCadastro() {
        if (dataCadastro == null) {
            setDataCadastro(new InstantFilter());
        }
        return dataCadastro;
    }

    public void setDataCadastro(InstantFilter dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public InstantFilter getDataEvento() {
        return dataEvento;
    }

    public Optional<InstantFilter> optionalDataEvento() {
        return Optional.ofNullable(dataEvento);
    }

    public InstantFilter dataEvento() {
        if (dataEvento == null) {
            setDataEvento(new InstantFilter());
        }
        return dataEvento;
    }

    public void setDataEvento(InstantFilter dataEvento) {
        this.dataEvento = dataEvento;
    }

    public StringFilter getHoraInicio() {
        return horaInicio;
    }

    public Optional<StringFilter> optionalHoraInicio() {
        return Optional.ofNullable(horaInicio);
    }

    public StringFilter horaInicio() {
        if (horaInicio == null) {
            setHoraInicio(new StringFilter());
        }
        return horaInicio;
    }

    public void setHoraInicio(StringFilter horaInicio) {
        this.horaInicio = horaInicio;
    }

    public StringFilter getHoraTermino() {
        return horaTermino;
    }

    public Optional<StringFilter> optionalHoraTermino() {
        return Optional.ofNullable(horaTermino);
    }

    public StringFilter horaTermino() {
        if (horaTermino == null) {
            setHoraTermino(new StringFilter());
        }
        return horaTermino;
    }

    public void setHoraTermino(StringFilter horaTermino) {
        this.horaTermino = horaTermino;
    }

    public StringFilter getObservacao() {
        return observacao;
    }

    public Optional<StringFilter> optionalObservacao() {
        return Optional.ofNullable(observacao);
    }

    public StringFilter observacao() {
        if (observacao == null) {
            setObservacao(new StringFilter());
        }
        return observacao;
    }

    public void setObservacao(StringFilter observacao) {
        this.observacao = observacao;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final EventosCriteria that = (EventosCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nome, that.nome) &&
            Objects.equals(dataCadastro, that.dataCadastro) &&
            Objects.equals(dataEvento, that.dataEvento) &&
            Objects.equals(horaInicio, that.horaInicio) &&
            Objects.equals(horaTermino, that.horaTermino) &&
            Objects.equals(observacao, that.observacao) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, dataCadastro, dataEvento, horaInicio, horaTermino, observacao, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventosCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNome().map(f -> "nome=" + f + ", ").orElse("") +
            optionalDataCadastro().map(f -> "dataCadastro=" + f + ", ").orElse("") +
            optionalDataEvento().map(f -> "dataEvento=" + f + ", ").orElse("") +
            optionalHoraInicio().map(f -> "horaInicio=" + f + ", ").orElse("") +
            optionalHoraTermino().map(f -> "horaTermino=" + f + ", ").orElse("") +
            optionalObservacao().map(f -> "observacao=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
