package com.conexaosolidaria.app.domain;

import static com.conexaosolidaria.app.domain.EventosTestSamples.*;
import static com.conexaosolidaria.app.domain.InscricaoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.conexaosolidaria.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InscricaoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Inscricao.class);
        Inscricao inscricao1 = getInscricaoSample1();
        Inscricao inscricao2 = new Inscricao();
        assertThat(inscricao1).isNotEqualTo(inscricao2);

        inscricao2.setId(inscricao1.getId());
        assertThat(inscricao1).isEqualTo(inscricao2);

        inscricao2 = getInscricaoSample2();
        assertThat(inscricao1).isNotEqualTo(inscricao2);
    }

    @Test
    void eventoTest() throws Exception {
        Inscricao inscricao = getInscricaoRandomSampleGenerator();
        Eventos eventosBack = getEventosRandomSampleGenerator();

        inscricao.setEvento(eventosBack);
        assertThat(inscricao.getEvento()).isEqualTo(eventosBack);

        inscricao.evento(null);
        assertThat(inscricao.getEvento()).isNull();
    }
}
