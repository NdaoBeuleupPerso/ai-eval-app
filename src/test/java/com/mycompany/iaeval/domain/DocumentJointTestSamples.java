package com.mycompany.iaeval.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DocumentJointTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static DocumentJoint getDocumentJointSample1() {
        return new DocumentJoint().id(1L).nom("nom1").url("url1").idExterne("idExterne1");
    }

    public static DocumentJoint getDocumentJointSample2() {
        return new DocumentJoint().id(2L).nom("nom2").url("url2").idExterne("idExterne2");
    }

    public static DocumentJoint getDocumentJointRandomSampleGenerator() {
        return new DocumentJoint()
            .id(longCount.incrementAndGet())
            .nom(UUID.randomUUID().toString())
            .url(UUID.randomUUID().toString())
            .idExterne(UUID.randomUUID().toString());
    }
}
