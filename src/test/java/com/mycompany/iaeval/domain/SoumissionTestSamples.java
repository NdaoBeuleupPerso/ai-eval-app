package com.mycompany.iaeval.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class SoumissionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Soumission getSoumissionSample1() {
        return new Soumission().id(1L);
    }

    public static Soumission getSoumissionSample2() {
        return new Soumission().id(2L);
    }

    public static Soumission getSoumissionRandomSampleGenerator() {
        return new Soumission().id(longCount.incrementAndGet());
    }
}
