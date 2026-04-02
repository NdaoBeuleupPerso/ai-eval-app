package com.mycompany.iaeval.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CritereTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Critere getCritereSample1() {
        return new Critere().id(1L).nom("nom1").description("description1");
    }

    public static Critere getCritereSample2() {
        return new Critere().id(2L).nom("nom2").description("description2");
    }

    public static Critere getCritereRandomSampleGenerator() {
        return new Critere().id(longCount.incrementAndGet()).nom(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
