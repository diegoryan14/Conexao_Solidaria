package com.conexaosolidaria.app.web.rest;

import static com.conexaosolidaria.app.domain.EventosAsserts.*;
import static com.conexaosolidaria.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.conexaosolidaria.app.IntegrationTest;
import com.conexaosolidaria.app.domain.Eventos;
import com.conexaosolidaria.app.domain.User;
import com.conexaosolidaria.app.repository.EventosRepository;
import com.conexaosolidaria.app.service.EventosService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EventosResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EventosResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATA_CADASTRO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATA_CADASTRO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATA_EVENTO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATA_EVENTO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_HORA_INICIO = "AAAAAAAAAA";
    private static final String UPDATED_HORA_INICIO = "BBBBBBBBBB";

    private static final String DEFAULT_HORA_TERMINO = "AAAAAAAAAA";
    private static final String UPDATED_HORA_TERMINO = "BBBBBBBBBB";

    private static final String DEFAULT_OBSERVACAO = "AAAAAAAAAA";
    private static final String UPDATED_OBSERVACAO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/eventos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EventosRepository eventosRepository;

    @Mock
    private EventosRepository eventosRepositoryMock;

    @Mock
    private EventosService eventosServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventosMockMvc;

    private Eventos eventos;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Eventos createEntity(EntityManager em) {
        Eventos eventos = new Eventos()
            .nome(DEFAULT_NOME)
            .dataCadastro(DEFAULT_DATA_CADASTRO)
            .dataEvento(DEFAULT_DATA_EVENTO)
            .horaInicio(DEFAULT_HORA_INICIO)
            .horaTermino(DEFAULT_HORA_TERMINO)
            .observacao(DEFAULT_OBSERVACAO);
        return eventos;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Eventos createUpdatedEntity(EntityManager em) {
        Eventos eventos = new Eventos()
            .nome(UPDATED_NOME)
            .dataCadastro(UPDATED_DATA_CADASTRO)
            .dataEvento(UPDATED_DATA_EVENTO)
            .horaInicio(UPDATED_HORA_INICIO)
            .horaTermino(UPDATED_HORA_TERMINO)
            .observacao(UPDATED_OBSERVACAO);
        return eventos;
    }

    @BeforeEach
    public void initTest() {
        eventos = createEntity(em);
    }

    @Test
    @Transactional
    void createEventos() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Eventos
        var returnedEventos = om.readValue(
            restEventosMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventos)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Eventos.class
        );

        // Validate the Eventos in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEventosUpdatableFieldsEquals(returnedEventos, getPersistedEventos(returnedEventos));
    }

    @Test
    @Transactional
    void createEventosWithExistingId() throws Exception {
        // Create the Eventos with an existing ID
        eventos.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventosMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventos)))
            .andExpect(status().isBadRequest());

        // Validate the Eventos in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEventos() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList
        restEventosMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventos.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].dataCadastro").value(hasItem(DEFAULT_DATA_CADASTRO.toString())))
            .andExpect(jsonPath("$.[*].dataEvento").value(hasItem(DEFAULT_DATA_EVENTO.toString())))
            .andExpect(jsonPath("$.[*].horaInicio").value(hasItem(DEFAULT_HORA_INICIO)))
            .andExpect(jsonPath("$.[*].horaTermino").value(hasItem(DEFAULT_HORA_TERMINO)))
            .andExpect(jsonPath("$.[*].observacao").value(hasItem(DEFAULT_OBSERVACAO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEventosWithEagerRelationshipsIsEnabled() throws Exception {
        when(eventosServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEventosMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(eventosServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEventosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(eventosServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEventosMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(eventosRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEventos() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get the eventos
        restEventosMockMvc
            .perform(get(ENTITY_API_URL_ID, eventos.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(eventos.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME))
            .andExpect(jsonPath("$.dataCadastro").value(DEFAULT_DATA_CADASTRO.toString()))
            .andExpect(jsonPath("$.dataEvento").value(DEFAULT_DATA_EVENTO.toString()))
            .andExpect(jsonPath("$.horaInicio").value(DEFAULT_HORA_INICIO))
            .andExpect(jsonPath("$.horaTermino").value(DEFAULT_HORA_TERMINO))
            .andExpect(jsonPath("$.observacao").value(DEFAULT_OBSERVACAO));
    }

    @Test
    @Transactional
    void getEventosByIdFiltering() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        Long id = eventos.getId();

        defaultEventosFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEventosFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEventosFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEventosByNomeIsEqualToSomething() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where nome equals to
        defaultEventosFiltering("nome.equals=" + DEFAULT_NOME, "nome.equals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllEventosByNomeIsInShouldWork() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where nome in
        defaultEventosFiltering("nome.in=" + DEFAULT_NOME + "," + UPDATED_NOME, "nome.in=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllEventosByNomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where nome is not null
        defaultEventosFiltering("nome.specified=true", "nome.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByNomeContainsSomething() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where nome contains
        defaultEventosFiltering("nome.contains=" + DEFAULT_NOME, "nome.contains=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllEventosByNomeNotContainsSomething() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where nome does not contain
        defaultEventosFiltering("nome.doesNotContain=" + UPDATED_NOME, "nome.doesNotContain=" + DEFAULT_NOME);
    }

    @Test
    @Transactional
    void getAllEventosByDataCadastroIsEqualToSomething() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where dataCadastro equals to
        defaultEventosFiltering("dataCadastro.equals=" + DEFAULT_DATA_CADASTRO, "dataCadastro.equals=" + UPDATED_DATA_CADASTRO);
    }

    @Test
    @Transactional
    void getAllEventosByDataCadastroIsInShouldWork() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where dataCadastro in
        defaultEventosFiltering(
            "dataCadastro.in=" + DEFAULT_DATA_CADASTRO + "," + UPDATED_DATA_CADASTRO,
            "dataCadastro.in=" + UPDATED_DATA_CADASTRO
        );
    }

    @Test
    @Transactional
    void getAllEventosByDataCadastroIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where dataCadastro is not null
        defaultEventosFiltering("dataCadastro.specified=true", "dataCadastro.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByDataEventoIsEqualToSomething() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where dataEvento equals to
        defaultEventosFiltering("dataEvento.equals=" + DEFAULT_DATA_EVENTO, "dataEvento.equals=" + UPDATED_DATA_EVENTO);
    }

    @Test
    @Transactional
    void getAllEventosByDataEventoIsInShouldWork() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where dataEvento in
        defaultEventosFiltering("dataEvento.in=" + DEFAULT_DATA_EVENTO + "," + UPDATED_DATA_EVENTO, "dataEvento.in=" + UPDATED_DATA_EVENTO);
    }

    @Test
    @Transactional
    void getAllEventosByDataEventoIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where dataEvento is not null
        defaultEventosFiltering("dataEvento.specified=true", "dataEvento.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByHoraInicioIsEqualToSomething() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where horaInicio equals to
        defaultEventosFiltering("horaInicio.equals=" + DEFAULT_HORA_INICIO, "horaInicio.equals=" + UPDATED_HORA_INICIO);
    }

    @Test
    @Transactional
    void getAllEventosByHoraInicioIsInShouldWork() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where horaInicio in
        defaultEventosFiltering("horaInicio.in=" + DEFAULT_HORA_INICIO + "," + UPDATED_HORA_INICIO, "horaInicio.in=" + UPDATED_HORA_INICIO);
    }

    @Test
    @Transactional
    void getAllEventosByHoraInicioIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where horaInicio is not null
        defaultEventosFiltering("horaInicio.specified=true", "horaInicio.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByHoraInicioContainsSomething() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where horaInicio contains
        defaultEventosFiltering("horaInicio.contains=" + DEFAULT_HORA_INICIO, "horaInicio.contains=" + UPDATED_HORA_INICIO);
    }

    @Test
    @Transactional
    void getAllEventosByHoraInicioNotContainsSomething() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where horaInicio does not contain
        defaultEventosFiltering("horaInicio.doesNotContain=" + UPDATED_HORA_INICIO, "horaInicio.doesNotContain=" + DEFAULT_HORA_INICIO);
    }

    @Test
    @Transactional
    void getAllEventosByHoraTerminoIsEqualToSomething() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where horaTermino equals to
        defaultEventosFiltering("horaTermino.equals=" + DEFAULT_HORA_TERMINO, "horaTermino.equals=" + UPDATED_HORA_TERMINO);
    }

    @Test
    @Transactional
    void getAllEventosByHoraTerminoIsInShouldWork() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where horaTermino in
        defaultEventosFiltering(
            "horaTermino.in=" + DEFAULT_HORA_TERMINO + "," + UPDATED_HORA_TERMINO,
            "horaTermino.in=" + UPDATED_HORA_TERMINO
        );
    }

    @Test
    @Transactional
    void getAllEventosByHoraTerminoIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where horaTermino is not null
        defaultEventosFiltering("horaTermino.specified=true", "horaTermino.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByHoraTerminoContainsSomething() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where horaTermino contains
        defaultEventosFiltering("horaTermino.contains=" + DEFAULT_HORA_TERMINO, "horaTermino.contains=" + UPDATED_HORA_TERMINO);
    }

    @Test
    @Transactional
    void getAllEventosByHoraTerminoNotContainsSomething() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where horaTermino does not contain
        defaultEventosFiltering("horaTermino.doesNotContain=" + UPDATED_HORA_TERMINO, "horaTermino.doesNotContain=" + DEFAULT_HORA_TERMINO);
    }

    @Test
    @Transactional
    void getAllEventosByObservacaoIsEqualToSomething() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where observacao equals to
        defaultEventosFiltering("observacao.equals=" + DEFAULT_OBSERVACAO, "observacao.equals=" + UPDATED_OBSERVACAO);
    }

    @Test
    @Transactional
    void getAllEventosByObservacaoIsInShouldWork() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where observacao in
        defaultEventosFiltering("observacao.in=" + DEFAULT_OBSERVACAO + "," + UPDATED_OBSERVACAO, "observacao.in=" + UPDATED_OBSERVACAO);
    }

    @Test
    @Transactional
    void getAllEventosByObservacaoIsNullOrNotNull() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where observacao is not null
        defaultEventosFiltering("observacao.specified=true", "observacao.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByObservacaoContainsSomething() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where observacao contains
        defaultEventosFiltering("observacao.contains=" + DEFAULT_OBSERVACAO, "observacao.contains=" + UPDATED_OBSERVACAO);
    }

    @Test
    @Transactional
    void getAllEventosByObservacaoNotContainsSomething() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        // Get all the eventosList where observacao does not contain
        defaultEventosFiltering("observacao.doesNotContain=" + UPDATED_OBSERVACAO, "observacao.doesNotContain=" + DEFAULT_OBSERVACAO);
    }

    @Test
    @Transactional
    void getAllEventosByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            eventosRepository.saveAndFlush(eventos);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        eventos.setUser(user);
        eventosRepository.saveAndFlush(eventos);
        Long userId = user.getId();
        // Get all the eventosList where user equals to userId
        defaultEventosShouldBeFound("userId.equals=" + userId);

        // Get all the eventosList where user equals to (userId + 1)
        defaultEventosShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultEventosFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultEventosShouldBeFound(shouldBeFound);
        defaultEventosShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEventosShouldBeFound(String filter) throws Exception {
        restEventosMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eventos.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].dataCadastro").value(hasItem(DEFAULT_DATA_CADASTRO.toString())))
            .andExpect(jsonPath("$.[*].dataEvento").value(hasItem(DEFAULT_DATA_EVENTO.toString())))
            .andExpect(jsonPath("$.[*].horaInicio").value(hasItem(DEFAULT_HORA_INICIO)))
            .andExpect(jsonPath("$.[*].horaTermino").value(hasItem(DEFAULT_HORA_TERMINO)))
            .andExpect(jsonPath("$.[*].observacao").value(hasItem(DEFAULT_OBSERVACAO)));

        // Check, that the count call also returns 1
        restEventosMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEventosShouldNotBeFound(String filter) throws Exception {
        restEventosMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEventosMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEventos() throws Exception {
        // Get the eventos
        restEventosMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEventos() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventos
        Eventos updatedEventos = eventosRepository.findById(eventos.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEventos are not directly saved in db
        em.detach(updatedEventos);
        updatedEventos
            .nome(UPDATED_NOME)
            .dataCadastro(UPDATED_DATA_CADASTRO)
            .dataEvento(UPDATED_DATA_EVENTO)
            .horaInicio(UPDATED_HORA_INICIO)
            .horaTermino(UPDATED_HORA_TERMINO)
            .observacao(UPDATED_OBSERVACAO);

        restEventosMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEventos.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedEventos))
            )
            .andExpect(status().isOk());

        // Validate the Eventos in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEventosToMatchAllProperties(updatedEventos);
    }

    @Test
    @Transactional
    void putNonExistingEventos() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventos.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventosMockMvc
            .perform(put(ENTITY_API_URL_ID, eventos.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventos)))
            .andExpect(status().isBadRequest());

        // Validate the Eventos in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEventos() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventos.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventosMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventos))
            )
            .andExpect(status().isBadRequest());

        // Validate the Eventos in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEventos() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventos.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventosMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventos)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Eventos in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEventosWithPatch() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventos using partial update
        Eventos partialUpdatedEventos = new Eventos();
        partialUpdatedEventos.setId(eventos.getId());

        partialUpdatedEventos.horaInicio(UPDATED_HORA_INICIO).observacao(UPDATED_OBSERVACAO);

        restEventosMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventos.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEventos))
            )
            .andExpect(status().isOk());

        // Validate the Eventos in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventosUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEventos, eventos), getPersistedEventos(eventos));
    }

    @Test
    @Transactional
    void fullUpdateEventosWithPatch() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eventos using partial update
        Eventos partialUpdatedEventos = new Eventos();
        partialUpdatedEventos.setId(eventos.getId());

        partialUpdatedEventos
            .nome(UPDATED_NOME)
            .dataCadastro(UPDATED_DATA_CADASTRO)
            .dataEvento(UPDATED_DATA_EVENTO)
            .horaInicio(UPDATED_HORA_INICIO)
            .horaTermino(UPDATED_HORA_TERMINO)
            .observacao(UPDATED_OBSERVACAO);

        restEventosMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEventos.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEventos))
            )
            .andExpect(status().isOk());

        // Validate the Eventos in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventosUpdatableFieldsEquals(partialUpdatedEventos, getPersistedEventos(partialUpdatedEventos));
    }

    @Test
    @Transactional
    void patchNonExistingEventos() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventos.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventosMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eventos.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(eventos))
            )
            .andExpect(status().isBadRequest());

        // Validate the Eventos in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEventos() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventos.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventosMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventos))
            )
            .andExpect(status().isBadRequest());

        // Validate the Eventos in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEventos() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        eventos.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventosMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(eventos)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Eventos in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEventos() throws Exception {
        // Initialize the database
        eventosRepository.saveAndFlush(eventos);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the eventos
        restEventosMockMvc
            .perform(delete(ENTITY_API_URL_ID, eventos.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return eventosRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Eventos getPersistedEventos(Eventos eventos) {
        return eventosRepository.findById(eventos.getId()).orElseThrow();
    }

    protected void assertPersistedEventosToMatchAllProperties(Eventos expectedEventos) {
        assertEventosAllPropertiesEquals(expectedEventos, getPersistedEventos(expectedEventos));
    }

    protected void assertPersistedEventosToMatchUpdatableProperties(Eventos expectedEventos) {
        assertEventosAllUpdatablePropertiesEquals(expectedEventos, getPersistedEventos(expectedEventos));
    }
}
