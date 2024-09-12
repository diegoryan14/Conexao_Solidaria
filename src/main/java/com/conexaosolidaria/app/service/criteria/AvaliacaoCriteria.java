package com.conexaosolidaria.app.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.conexaosolidaria.app.domain.Avaliacao} entity. This class is used
 * in {@link com.conexaosolidaria.app.web.rest.AvaliacaoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /avaliacaos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AvaliacaoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter estrelas;

    private StringFilter observacao;

    private LongFilter userId;

    private LongFilter eventoId;

    private Boolean distinct;

    public AvaliacaoCriteria() {}

    public AvaliacaoCriteria(AvaliacaoCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.estrelas = other.optionalEstrelas().map(IntegerFilter::copy).orElse(null);
        this.observacao = other.optionalObservacao().map(StringFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.eventoId = other.optionalEventoId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AvaliacaoCriteria copy() {
        return new AvaliacaoCriteria(this);
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

    public IntegerFilter getEstrelas() {
        return estrelas;
    }

    public Optional<IntegerFilter> optionalEstrelas() {
        return Optional.ofNullable(estrelas);
    }

    public IntegerFilter estrelas() {
        if (estrelas == null) {
            setEstrelas(new IntegerFilter());
        }
        return estrelas;
    }

    public void setEstrelas(IntegerFilter estrelas) {
        this.estrelas = estrelas;
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

    public LongFilter getEventoId() {
        return eventoId;
    }

    public Optional<LongFilter> optionalEventoId() {
        return Optional.ofNullable(eventoId);
    }

    public LongFilter eventoId() {
        if (eventoId == null) {
            setEventoId(new LongFilter());
        }
        return eventoId;
    }

    public void setEventoId(LongFilter eventoId) {
        this.eventoId = eventoId;
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
        final AvaliacaoCriteria that = (AvaliacaoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(estrelas, that.estrelas) &&
            Objects.equals(observacao, that.observacao) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(eventoId, that.eventoId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, estrelas, observacao, userId, eventoId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AvaliacaoCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalEstrelas().map(f -> "estrelas=" + f + ", ").orElse("") +
            optionalObservacao().map(f -> "observacao=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalEventoId().map(f -> "eventoId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
