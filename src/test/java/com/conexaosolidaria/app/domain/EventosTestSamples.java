package com.conexaosolidaria.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EventosTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Eventos getEventosSample1() {
        return new Eventos().id(1L).nome("nome1").horaInicio("horaInicio1").horaTermino("horaTermino1").observacao("observacao1");
    }

    public static Eventos getEventosSample2() {
        return new Eventos().id(2L).nome("nome2").horaInicio("horaInicio2").horaTermino("horaTermino2").observacao("observacao2");
    }

    public static Eventos getEventosRandomSampleGenerator() {
        return new Eventos()
            .id(longCount.incrementAndGet())
            .nome(UUID.randomUUID().toString())
            .horaInicio(UUID.randomUUID().toString())
            .horaTermino(UUID.randomUUID().toString())
            .observacao(UUID.randomUUID().toString());
    }
}
