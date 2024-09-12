package com.conexaosolidaria.app.web.rest;

import static com.conexaosolidaria.app.domain.InscricaoAsserts.*;
import static com.conexaosolidaria.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.conexaosolidaria.app.IntegrationTest;
import com.conexaosolidaria.app.domain.Eventos;
import com.conexaosolidaria.app.domain.Inscricao;
import com.conexaosolidaria.app.domain.User;
import com.conexaosolidaria.app.repository.InscricaoRepository;
import com.conexaosolidaria.app.service.InscricaoService;
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
 * Integration tests for the {@link InscricaoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InscricaoResourceIT {

    private static final Instant DEFAULT_DATA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/inscricaos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Mock
    private InscricaoRepository inscricaoRepositoryMock;

    @Mock
    private InscricaoService inscricaoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInscricaoMockMvc;

    private Inscricao inscricao;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inscricao createEntity(EntityManager em) {
        Inscricao inscricao = new Inscricao().data(DEFAULT_DATA);
        return inscricao;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inscricao createUpdatedEntity(EntityManager em) {
        Inscricao inscricao = new Inscricao().data(UPDATED_DATA);
        return inscricao;
    }

    @BeforeEach
    public void initTest() {
        inscricao = createEntity(em);
    }

    @Test
    @Transactional
    void createInscricao() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Inscricao
        var returnedInscricao = om.readValue(
            restInscricaoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inscricao)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Inscricao.class
        );

        // Validate the Inscricao in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertInscricaoUpdatableFieldsEquals(returnedInscricao, getPersistedInscricao(returnedInscricao));
    }

    @Test
    @Transactional
    void createInscricaoWithExistingId() throws Exception {
        // Create the Inscricao with an existing ID
        inscricao.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInscricaoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inscricao)))
            .andExpect(status().isBadRequest());

        // Validate the Inscricao in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllInscricaos() throws Exception {
        // Initialize the database
        inscricaoRepository.saveAndFlush(inscricao);

        // Get all the inscricaoList
        restInscricaoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inscricao.getId().intValue())))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInscricaosWithEagerRelationshipsIsEnabled() throws Exception {
        when(inscricaoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInscricaoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(inscricaoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInscricaosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(inscricaoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInscricaoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(inscricaoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInscricao() throws Exception {
        // Initialize the database
        inscricaoRepository.saveAndFlush(inscricao);

        // Get the inscricao
        restInscricaoMockMvc
            .perform(get(ENTITY_API_URL_ID, inscricao.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inscricao.getId().intValue()))
            .andExpect(jsonPath("$.data").value(DEFAULT_DATA.toString()));
    }

    @Test
    @Transactional
    void getInscricaosByIdFiltering() throws Exception {
        // Initialize the database
        inscricaoRepository.saveAndFlush(inscricao);

        Long id = inscricao.getId();

        defaultInscricaoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultInscricaoFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultInscricaoFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInscricaosByDataIsEqualToSomething() throws Exception {
        // Initialize the database
        inscricaoRepository.saveAndFlush(inscricao);

        // Get all the inscricaoList where data equals to
        defaultInscricaoFiltering("data.equals=" + DEFAULT_DATA, "data.equals=" + UPDATED_DATA);
    }

    @Test
    @Transactional
    void getAllInscricaosByDataIsInShouldWork() throws Exception {
        // Initialize the database
        inscricaoRepository.saveAndFlush(inscricao);

        // Get all the inscricaoList where data in
        defaultInscricaoFiltering("data.in=" + DEFAULT_DATA + "," + UPDATED_DATA, "data.in=" + UPDATED_DATA);
    }

    @Test
    @Transactional
    void getAllInscricaosByDataIsNullOrNotNull() throws Exception {
        // Initialize the database
        inscricaoRepository.saveAndFlush(inscricao);

        // Get all the inscricaoList where data is not null
        defaultInscricaoFiltering("data.specified=true", "data.specified=false");
    }

    @Test
    @Transactional
    void getAllInscricaosByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            inscricaoRepository.saveAndFlush(inscricao);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        inscricao.setUser(user);
        inscricaoRepository.saveAndFlush(inscricao);
        Long userId = user.getId();
        // Get all the inscricaoList where user equals to userId
        defaultInscricaoShouldBeFound("userId.equals=" + userId);

        // Get all the inscricaoList where user equals to (userId + 1)
        defaultInscricaoShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllInscricaosByEventoIsEqualToSomething() throws Exception {
        Eventos evento;
        if (TestUtil.findAll(em, Eventos.class).isEmpty()) {
            inscricaoRepository.saveAndFlush(inscricao);
            evento = EventosResourceIT.createEntity(em);
        } else {
            evento = TestUtil.findAll(em, Eventos.class).get(0);
        }
        em.persist(evento);
        em.flush();
        inscricao.setEvento(evento);
        inscricaoRepository.saveAndFlush(inscricao);
        Long eventoId = evento.getId();
        // Get all the inscricaoList where evento equals to eventoId
        defaultInscricaoShouldBeFound("eventoId.equals=" + eventoId);

        // Get all the inscricaoList where evento equals to (eventoId + 1)
        defaultInscricaoShouldNotBeFound("eventoId.equals=" + (eventoId + 1));
    }

    private void defaultInscricaoFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultInscricaoShouldBeFound(shouldBeFound);
        defaultInscricaoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInscricaoShouldBeFound(String filter) throws Exception {
        restInscricaoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inscricao.getId().intValue())))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA.toString())));

        // Check, that the count call also returns 1
        restInscricaoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInscricaoShouldNotBeFound(String filter) throws Exception {
        restInscricaoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInscricaoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInscricao() throws Exception {
        // Get the inscricao
        restInscricaoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInscricao() throws Exception {
        // Initialize the database
        inscricaoRepository.saveAndFlush(inscricao);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inscricao
        Inscricao updatedInscricao = inscricaoRepository.findById(inscricao.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInscricao are not directly saved in db
        em.detach(updatedInscricao);
        updatedInscricao.data(UPDATED_DATA);

        restInscricaoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInscricao.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedInscricao))
            )
            .andExpect(status().isOk());

        // Validate the Inscricao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInscricaoToMatchAllProperties(updatedInscricao);
    }

    @Test
    @Transactional
    void putNonExistingInscricao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inscricao.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInscricaoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inscricao.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inscricao))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inscricao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInscricao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inscricao.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInscricaoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inscricao))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inscricao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInscricao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inscricao.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInscricaoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inscricao)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inscricao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInscricaoWithPatch() throws Exception {
        // Initialize the database
        inscricaoRepository.saveAndFlush(inscricao);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inscricao using partial update
        Inscricao partialUpdatedInscricao = new Inscricao();
        partialUpdatedInscricao.setId(inscricao.getId());

        restInscricaoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInscricao.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInscricao))
            )
            .andExpect(status().isOk());

        // Validate the Inscricao in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInscricaoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInscricao, inscricao),
            getPersistedInscricao(inscricao)
        );
    }

    @Test
    @Transactional
    void fullUpdateInscricaoWithPatch() throws Exception {
        // Initialize the database
        inscricaoRepository.saveAndFlush(inscricao);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inscricao using partial update
        Inscricao partialUpdatedInscricao = new Inscricao();
        partialUpdatedInscricao.setId(inscricao.getId());

        partialUpdatedInscricao.data(UPDATED_DATA);

        restInscricaoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInscricao.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInscricao))
            )
            .andExpect(status().isOk());

        // Validate the Inscricao in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInscricaoUpdatableFieldsEquals(partialUpdatedInscricao, getPersistedInscricao(partialUpdatedInscricao));
    }

    @Test
    @Transactional
    void patchNonExistingInscricao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inscricao.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInscricaoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inscricao.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inscricao))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inscricao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInscricao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inscricao.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInscricaoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inscricao))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inscricao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInscricao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inscricao.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInscricaoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(inscricao)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inscricao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInscricao() throws Exception {
        // Initialize the database
        inscricaoRepository.saveAndFlush(inscricao);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the inscricao
        restInscricaoMockMvc
            .perform(delete(ENTITY_API_URL_ID, inscricao.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return inscricaoRepository.count();
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

    protected Inscricao getPersistedInscricao(Inscricao inscricao) {
        return inscricaoRepository.findById(inscricao.getId()).orElseThrow();
    }

    protected void assertPersistedInscricaoToMatchAllProperties(Inscricao expectedInscricao) {
        assertInscricaoAllPropertiesEquals(expectedInscricao, getPersistedInscricao(expectedInscricao));
    }

    protected void assertPersistedInscricaoToMatchUpdatableProperties(Inscricao expectedInscricao) {
        assertInscricaoAllUpdatablePropertiesEquals(expectedInscricao, getPersistedInscricao(expectedInscricao));
    }
}
