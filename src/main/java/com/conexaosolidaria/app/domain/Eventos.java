package com.conexaosolidaria.app.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Eventos.
 */
@Entity
@Table(name = "eventos")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Eventos implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "data_cadastro")
    private Instant dataCadastro;

    @Column(name = "data_evento")
    private Instant dataEvento;

    @Column(name = "hora_inicio")
    private String horaInicio;

    @Column(name = "hora_termino")
    private String horaTermino;

    @Column(name = "observacao")
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Eventos id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public Eventos nome(String nome) {
        this.setNome(nome);
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Instant getDataCadastro() {
        return this.dataCadastro;
    }

    public Eventos dataCadastro(Instant dataCadastro) {
        this.setDataCadastro(dataCadastro);
        return this;
    }

    public void setDataCadastro(Instant dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Instant getDataEvento() {
        return this.dataEvento;
    }

    public Eventos dataEvento(Instant dataEvento) {
        this.setDataEvento(dataEvento);
        return this;
    }

    public void setDataEvento(Instant dataEvento) {
        this.dataEvento = dataEvento;
    }

    public String getHoraInicio() {
        return this.horaInicio;
    }

    public Eventos horaInicio(String horaInicio) {
        this.setHoraInicio(horaInicio);
        return this;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraTermino() {
        return this.horaTermino;
    }

    public Eventos horaTermino(String horaTermino) {
        this.setHoraTermino(horaTermino);
        return this;
    }

    public void setHoraTermino(String horaTermino) {
        this.horaTermino = horaTermino;
    }

    public String getObservacao() {
        return this.observacao;
    }

    public Eventos observacao(String observacao) {
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

    public Eventos user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Eventos)) {
            return false;
        }
        return getId() != null && getId().equals(((Eventos) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Eventos{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", dataCadastro='" + getDataCadastro() + "'" +
            ", dataEvento='" + getDataEvento() + "'" +
            ", horaInicio='" + getHoraInicio() + "'" +
            ", horaTermino='" + getHoraTermino() + "'" +
            ", observacao='" + getObservacao() + "'" +
            "}";
    }
}
