package com.conexaosolidaria.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Avaliacao.
 */
@Entity
@Table(name = "avaliacao")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Avaliacao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "estrelas")
    private Integer estrelas;

    @Column(name = "observacao")
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Eventos evento;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Avaliacao id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEstrelas() {
        return this.estrelas;
    }

    public Avaliacao estrelas(Integer estrelas) {
        this.setEstrelas(estrelas);
        return this;
    }

    public void setEstrelas(Integer estrelas) {
        this.estrelas = estrelas;
    }

    public String getObservacao() {
        return this.observacao;
    }

    public Avaliacao observacao(String observacao) {
        this.setObservacao(observacao);
        return this;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Avaliacao user(User user) {
        this.setUser(user);
        return this;
    }

    public Eventos getEvento() {
        return this.evento;
    }

    public void setEvento(Eventos eventos) {
        this.evento = eventos;
    }

    public Avaliacao evento(Eventos eventos) {
        this.setEvento(eventos);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Avaliacao)) {
            return false;
        }
        return getId() != null && getId().equals(((Avaliacao) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Avaliacao{" +
            "id=" + getId() +
            ", estrelas=" + getEstrelas() +
            ", observacao='" + getObservacao() + "'" +
            "}";
    }
}
