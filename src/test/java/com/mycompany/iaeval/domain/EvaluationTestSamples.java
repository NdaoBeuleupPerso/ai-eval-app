package com.mycompany.iaeval.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class EvaluationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Evaluation getEvaluationSample1() {
        return new Evaluation().id(1L);
    }

    public static Evaluation getEvaluationSample2() {
        return new Evaluation().id(2L);
    }

    public static Evaluation getEvaluationRandomSampleGenerator() {
        return new Evaluation().id(longCount.incrementAndGet());
    }
}
