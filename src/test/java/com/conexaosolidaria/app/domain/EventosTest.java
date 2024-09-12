package com.conexaosolidaria.app.domain;

import static com.conexaosolidaria.app.domain.EventosTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.conexaosolidaria.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventosTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Eventos.class);
        Eventos eventos1 = getEventosSample1();
        Eventos eventos2 = new Eventos();
        assertThat(eventos1).isNotEqualTo(eventos2);

        eventos2.setId(eventos1.getId());
        assertThat(eventos1).isEqualTo(eventos2);

        eventos2 = getEventosSample2();
        assertThat(eventos1).isNotEqualTo(eventos2);
    }
}
