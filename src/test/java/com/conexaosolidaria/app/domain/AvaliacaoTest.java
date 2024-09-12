package com.conexaosolidaria.app.domain;

import static com.conexaosolidaria.app.domain.AvaliacaoTestSamples.*;
import static com.conexaosolidaria.app.domain.EventosTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.conexaosolidaria.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AvaliacaoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Avaliacao.class);
        Avaliacao avaliacao1 = getAvaliacaoSample1();
        Avaliacao avaliacao2 = new Avaliacao();
        assertThat(avaliacao1).isNotEqualTo(avaliacao2);

        avaliacao2.setId(avaliacao1.getId());
        assertThat(avaliacao1).isEqualTo(avaliacao2);

        avaliacao2 = getAvaliacaoSample2();
        assertThat(avaliacao1).isNotEqualTo(avaliacao2);
    }

    @Test
    void eventoTest() throws Exception {
        Avaliacao avaliacao = getAvaliacaoRandomSampleGenerator();
        Eventos eventosBack = getEventosRandomSampleGenerator();

        avaliacao.setEvento(eventosBack);
        assertThat(avaliacao.getEvento()).isEqualTo(eventosBack);

        avaliacao.evento(null);
        assertThat(avaliacao.getEvento()).isNull();
    }
}
