package com.mycompany.iaeval.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ReferenceLegaleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ReferenceLegale getReferenceLegaleSample1() {
        return new ReferenceLegale().id(1L).titre("titre1").qdrantUuid("qdrantUuid1").source("source1");
    }

    public static ReferenceLegale getReferenceLegaleSample2() {
        return new ReferenceLegale().id(2L).titre("titre2").qdrantUuid("qdrantUuid2").source("source2");
    }

    public static ReferenceLegale getReferenceLegaleRandomSampleGenerator() {
        return new ReferenceLegale()
            .id(longCount.incrementAndGet())
            .titre(UUID.randomUUID().toString())
            .qdrantUuid(UUID.randomUUID().toString())
            .source(UUID.randomUUID().toString());
    }
}
