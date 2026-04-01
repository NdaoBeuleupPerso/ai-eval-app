package com.mycompany.iaeval.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TraceAuditTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static TraceAudit getTraceAuditSample1() {
        return new TraceAudit().id(1L).action("action1").identifiantUtilisateur("identifiantUtilisateur1");
    }

    public static TraceAudit getTraceAuditSample2() {
        return new TraceAudit().id(2L).action("action2").identifiantUtilisateur("identifiantUtilisateur2");
    }

    public static TraceAudit getTraceAuditRandomSampleGenerator() {
        return new TraceAudit()
            .id(longCount.incrementAndGet())
            .action(UUID.randomUUID().toString())
            .identifiantUtilisateur(UUID.randomUUID().toString());
    }
}
