package com.mycompany.iaeval.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module.Feature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.LocalTime;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Configuration
public class JacksonConfiguration {

    /**
     * Support for Java date and time API.
     * @return the corresponding Jackson module.
     */
    @Bean
    public JavaTimeModule javaTimeModule() {
        final JavaTimeModule javaTime = new JavaTimeModule();
        javaTime.addSerializer(
            LocalTime.class,
            new JsonSerializer<LocalTime>() {
                @Override
                public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeString(value.toString());
                }
            }
        );
        return javaTime;
    }

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> restClientBuilder.requestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder ->
            builder.postConfigurer(objectMapper ->
                objectMapper
                    .getFactory()
                    .setStreamReadConstraints(
                        StreamReadConstraints.builder()
                            .maxStringLength(20_000_000) // 20 Mo au lieu de 8 Ko
                            .build()
                    )
            );
    }

    @Bean
    public Jdk8Module jdk8TimeModule() {
        return new Jdk8Module();
    }

    /*
     * Support for Hibernate types in Jackson.
     */
    @Bean
    public Hibernate6Module hibernate6Module() {
        return new Hibernate6Module().configure(Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);
    }
}
