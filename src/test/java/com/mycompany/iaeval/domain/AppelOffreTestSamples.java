package com.mycompany.iaeval.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AppelOffreTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AppelOffre getAppelOffreSample1() {
        return new AppelOffre().id(1L).reference("reference1").titre("titre1");
    }

    public static AppelOffre getAppelOffreSample2() {
        return new AppelOffre().id(2L).reference("reference2").titre("titre2");
    }

    public static AppelOffre getAppelOffreRandomSampleGenerator() {
        return new AppelOffre().id(longCount.incrementAndGet()).reference(UUID.randomUUID().toString()).titre(UUID.randomUUID().toString());
    }
}
