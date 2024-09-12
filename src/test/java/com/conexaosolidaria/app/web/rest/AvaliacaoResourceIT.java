package com.conexaosolidaria.app.web.rest;

import static com.conexaosolidaria.app.domain.AvaliacaoAsserts.*;
import static com.conexaosolidaria.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.conexaosolidaria.app.IntegrationTest;
import com.conexaosolidaria.app.domain.Avaliacao;
import com.conexaosolidaria.app.domain.Eventos;
import com.conexaosolidaria.app.domain.User;
import com.conexaosolidaria.app.repository.AvaliacaoRepository;
import com.conexaosolidaria.app.service.AvaliacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link AvaliacaoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AvaliacaoResourceIT {

    private static final Integer DEFAULT_ESTRELAS = 1;
    private static final Integer UPDATED_ESTRELAS = 2;
    private static final Integer SMALLER_ESTRELAS = 1 - 1;

    private static final String DEFAULT_OBSERVACAO = "AAAAAAAAAA";
    private static final String UPDATED_OBSERVACAO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/avaliacaos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private AvaliacaoRepository avaliacaoRepositoryMock;

    @Mock
    private AvaliacaoService avaliacaoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAvaliacaoMockMvc;

    private Avaliacao avaliacao;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Avaliacao createEntity(EntityManager em) {
        Avaliacao avaliacao = new Avaliacao().estrelas(DEFAULT_ESTRELAS).observacao(DEFAULT_OBSERVACAO);
        return avaliacao;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Avaliacao createUpdatedEntity(EntityManager em) {
        Avaliacao avaliacao = new Avaliacao().estrelas(UPDATED_ESTRELAS).observacao(UPDATED_OBSERVACAO);
        return avaliacao;
    }

    @BeforeEach
    public void initTest() {
        avaliacao = createEntity(em);
    }

    @Test
    @Transactional
    void createAvaliacao() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Avaliacao
        var returnedAvaliacao = om.readValue(
            restAvaliacaoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(avaliacao)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Avaliacao.class
        );

        // Validate the Avaliacao in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAvaliacaoUpdatableFieldsEquals(returnedAvaliacao, getPersistedAvaliacao(returnedAvaliacao));
    }

    @Test
    @Transactional
    void createAvaliacaoWithExistingId() throws Exception {
        // Create the Avaliacao with an existing ID
        avaliacao.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAvaliacaoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(avaliacao)))
            .andExpect(status().isBadRequest());

        // Validate the Avaliacao in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAvaliacaos() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        // Get all the avaliacaoList
        restAvaliacaoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(avaliacao.getId().intValue())))
            .andExpect(jsonPath("$.[*].estrelas").value(hasItem(DEFAULT_ESTRELAS)))
            .andExpect(jsonPath("$.[*].observacao").value(hasItem(DEFAULT_OBSERVACAO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAvaliacaosWithEagerRelationshipsIsEnabled() throws Exception {
        when(avaliacaoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAvaliacaoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(avaliacaoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAvaliacaosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(avaliacaoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAvaliacaoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(avaliacaoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAvaliacao() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        // Get the avaliacao
        restAvaliacaoMockMvc
            .perform(get(ENTITY_API_URL_ID, avaliacao.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(avaliacao.getId().intValue()))
            .andExpect(jsonPath("$.estrelas").value(DEFAULT_ESTRELAS))
            .andExpect(jsonPath("$.observacao").value(DEFAULT_OBSERVACAO));
    }

    @Test
    @Transactional
    void getAvaliacaosByIdFiltering() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        Long id = avaliacao.getId();

        defaultAvaliacaoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAvaliacaoFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAvaliacaoFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAvaliacaosByEstrelasIsEqualToSomething() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        // Get all the avaliacaoList where estrelas equals to
        defaultAvaliacaoFiltering("estrelas.equals=" + DEFAULT_ESTRELAS, "estrelas.equals=" + UPDATED_ESTRELAS);
    }

    @Test
    @Transactional
    void getAllAvaliacaosByEstrelasIsInShouldWork() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        // Get all the avaliacaoList where estrelas in
        defaultAvaliacaoFiltering("estrelas.in=" + DEFAULT_ESTRELAS + "," + UPDATED_ESTRELAS, "estrelas.in=" + UPDATED_ESTRELAS);
    }

    @Test
    @Transactional
    void getAllAvaliacaosByEstrelasIsNullOrNotNull() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        // Get all the avaliacaoList where estrelas is not null
        defaultAvaliacaoFiltering("estrelas.specified=true", "estrelas.specified=false");
    }

    @Test
    @Transactional
    void getAllAvaliacaosByEstrelasIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        // Get all the avaliacaoList where estrelas is greater than or equal to
        defaultAvaliacaoFiltering("estrelas.greaterThanOrEqual=" + DEFAULT_ESTRELAS, "estrelas.greaterThanOrEqual=" + UPDATED_ESTRELAS);
    }

    @Test
    @Transactional
    void getAllAvaliacaosByEstrelasIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        // Get all the avaliacaoList where estrelas is less than or equal to
        defaultAvaliacaoFiltering("estrelas.lessThanOrEqual=" + DEFAULT_ESTRELAS, "estrelas.lessThanOrEqual=" + SMALLER_ESTRELAS);
    }

    @Test
    @Transactional
    void getAllAvaliacaosByEstrelasIsLessThanSomething() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        // Get all the avaliacaoList where estrelas is less than
        defaultAvaliacaoFiltering("estrelas.lessThan=" + UPDATED_ESTRELAS, "estrelas.lessThan=" + DEFAULT_ESTRELAS);
    }

    @Test
    @Transactional
    void getAllAvaliacaosByEstrelasIsGreaterThanSomething() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        // Get all the avaliacaoList where estrelas is greater than
        defaultAvaliacaoFiltering("estrelas.greaterThan=" + SMALLER_ESTRELAS, "estrelas.greaterThan=" + DEFAULT_ESTRELAS);
    }

    @Test
    @Transactional
    void getAllAvaliacaosByObservacaoIsEqualToSomething() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        // Get all the avaliacaoList where observacao equals to
        defaultAvaliacaoFiltering("observacao.equals=" + DEFAULT_OBSERVACAO, "observacao.equals=" + UPDATED_OBSERVACAO);
    }

    @Test
    @Transactional
    void getAllAvaliacaosByObservacaoIsInShouldWork() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        // Get all the avaliacaoList where observacao in
        defaultAvaliacaoFiltering("observacao.in=" + DEFAULT_OBSERVACAO + "," + UPDATED_OBSERVACAO, "observacao.in=" + UPDATED_OBSERVACAO);
    }

    @Test
    @Transactional
    void getAllAvaliacaosByObservacaoIsNullOrNotNull() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        // Get all the avaliacaoList where observacao is not null
        defaultAvaliacaoFiltering("observacao.specified=true", "observacao.specified=false");
    }

    @Test
    @Transactional
    void getAllAvaliacaosByObservacaoContainsSomething() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        // Get all the avaliacaoList where observacao contains
        defaultAvaliacaoFiltering("observacao.contains=" + DEFAULT_OBSERVACAO, "observacao.contains=" + UPDATED_OBSERVACAO);
    }

    @Test
    @Transactional
    void getAllAvaliacaosByObservacaoNotContainsSomething() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        // Get all the avaliacaoList where observacao does not contain
        defaultAvaliacaoFiltering("observacao.doesNotContain=" + UPDATED_OBSERVACAO, "observacao.doesNotContain=" + DEFAULT_OBSERVACAO);
    }

    @Test
    @Transactional
    void getAllAvaliacaosByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            avaliacaoRepository.saveAndFlush(avaliacao);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        avaliacao.setUser(user);
        avaliacaoRepository.saveAndFlush(avaliacao);
        Long userId = user.getId();
        // Get all the avaliacaoList where user equals to userId
        defaultAvaliacaoShouldBeFound("userId.equals=" + userId);

        // Get all the avaliacaoList where user equals to (userId + 1)
        defaultAvaliacaoShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllAvaliacaosByEventoIsEqualToSomething() throws Exception {
        Eventos evento;
        if (TestUtil.findAll(em, Eventos.class).isEmpty()) {
            avaliacaoRepository.saveAndFlush(avaliacao);
            evento = EventosResourceIT.createEntity(em);
        } else {
            evento = TestUtil.findAll(em, Eventos.class).get(0);
        }
        em.persist(evento);
        em.flush();
        avaliacao.setEvento(evento);
        avaliacaoRepository.saveAndFlush(avaliacao);
        Long eventoId = evento.getId();
        // Get all the avaliacaoList where evento equals to eventoId
        defaultAvaliacaoShouldBeFound("eventoId.equals=" + eventoId);

        // Get all the avaliacaoList where evento equals to (eventoId + 1)
        defaultAvaliacaoShouldNotBeFound("eventoId.equals=" + (eventoId + 1));
    }

    private void defaultAvaliacaoFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAvaliacaoShouldBeFound(shouldBeFound);
        defaultAvaliacaoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAvaliacaoShouldBeFound(String filter) throws Exception {
        restAvaliacaoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(avaliacao.getId().intValue())))
            .andExpect(jsonPath("$.[*].estrelas").value(hasItem(DEFAULT_ESTRELAS)))
            .andExpect(jsonPath("$.[*].observacao").value(hasItem(DEFAULT_OBSERVACAO)));

        // Check, that the count call also returns 1
        restAvaliacaoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAvaliacaoShouldNotBeFound(String filter) throws Exception {
        restAvaliacaoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAvaliacaoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAvaliacao() throws Exception {
        // Get the avaliacao
        restAvaliacaoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAvaliacao() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the avaliacao
        Avaliacao updatedAvaliacao = avaliacaoRepository.findById(avaliacao.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAvaliacao are not directly saved in db
        em.detach(updatedAvaliacao);
        updatedAvaliacao.estrelas(UPDATED_ESTRELAS).observacao(UPDATED_OBSERVACAO);

        restAvaliacaoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAvaliacao.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAvaliacao))
            )
            .andExpect(status().isOk());

        // Validate the Avaliacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAvaliacaoToMatchAllProperties(updatedAvaliacao);
    }

    @Test
    @Transactional
    void putNonExistingAvaliacao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        avaliacao.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAvaliacaoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, avaliacao.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(avaliacao))
            )
            .andExpect(status().isBadRequest());

        // Validate the Avaliacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAvaliacao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        avaliacao.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAvaliacaoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(avaliacao))
            )
            .andExpect(status().isBadRequest());

        // Validate the Avaliacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAvaliacao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        avaliacao.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAvaliacaoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(avaliacao)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Avaliacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAvaliacaoWithPatch() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the avaliacao using partial update
        Avaliacao partialUpdatedAvaliacao = new Avaliacao();
        partialUpdatedAvaliacao.setId(avaliacao.getId());

        restAvaliacaoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAvaliacao.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAvaliacao))
            )
            .andExpect(status().isOk());

        // Validate the Avaliacao in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAvaliacaoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAvaliacao, avaliacao),
            getPersistedAvaliacao(avaliacao)
        );
    }

    @Test
    @Transactional
    void fullUpdateAvaliacaoWithPatch() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the avaliacao using partial update
        Avaliacao partialUpdatedAvaliacao = new Avaliacao();
        partialUpdatedAvaliacao.setId(avaliacao.getId());

        partialUpdatedAvaliacao.estrelas(UPDATED_ESTRELAS).observacao(UPDATED_OBSERVACAO);

        restAvaliacaoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAvaliacao.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAvaliacao))
            )
            .andExpect(status().isOk());

        // Validate the Avaliacao in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAvaliacaoUpdatableFieldsEquals(partialUpdatedAvaliacao, getPersistedAvaliacao(partialUpdatedAvaliacao));
    }

    @Test
    @Transactional
    void patchNonExistingAvaliacao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        avaliacao.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAvaliacaoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, avaliacao.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(avaliacao))
            )
            .andExpect(status().isBadRequest());

        // Validate the Avaliacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAvaliacao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        avaliacao.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAvaliacaoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(avaliacao))
            )
            .andExpect(status().isBadRequest());

        // Validate the Avaliacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAvaliacao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        avaliacao.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAvaliacaoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(avaliacao)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Avaliacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAvaliacao() throws Exception {
        // Initialize the database
        avaliacaoRepository.saveAndFlush(avaliacao);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the avaliacao
        restAvaliacaoMockMvc
            .perform(delete(ENTITY_API_URL_ID, avaliacao.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return avaliacaoRepository.count();
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

    protected Avaliacao getPersistedAvaliacao(Avaliacao avaliacao) {
        return avaliacaoRepository.findById(avaliacao.getId()).orElseThrow();
    }

    protected void assertPersistedAvaliacaoToMatchAllProperties(Avaliacao expectedAvaliacao) {
        assertAvaliacaoAllPropertiesEquals(expectedAvaliacao, getPersistedAvaliacao(expectedAvaliacao));
    }

    protected void assertPersistedAvaliacaoToMatchUpdatableProperties(Avaliacao expectedAvaliacao) {
        assertAvaliacaoAllUpdatablePropertiesEquals(expectedAvaliacao, getPersistedAvaliacao(expectedAvaliacao));
    }
}
