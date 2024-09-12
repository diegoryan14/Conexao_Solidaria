package com.conexaosolidaria.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AvaliacaoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Avaliacao getAvaliacaoSample1() {
        return new Avaliacao().id(1L).estrelas(1).observacao("observacao1");
    }

    public static Avaliacao getAvaliacaoSample2() {
        return new Avaliacao().id(2L).estrelas(2).observacao("observacao2");
    }

    public static Avaliacao getAvaliacaoRandomSampleGenerator() {
        return new Avaliacao()
            .id(longCount.incrementAndGet())
            .estrelas(intCount.incrementAndGet())
            .observacao(UUID.randomUUID().toString());
    }
}
