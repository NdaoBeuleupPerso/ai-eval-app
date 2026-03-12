package com.mycompany.iaeval;

import com.mycompany.iaeval.config.AsyncSyncConfiguration;
import com.mycompany.iaeval.config.EmbeddedSQL;
import com.mycompany.iaeval.config.JacksonConfiguration;
import com.mycompany.iaeval.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { IaevalApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class })
@EmbeddedSQL
public @interface IntegrationTest {
}
