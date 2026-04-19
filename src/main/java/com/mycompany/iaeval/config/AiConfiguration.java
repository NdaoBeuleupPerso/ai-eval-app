package com.mycompany.iaeval.config;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiEmbeddingModel;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiConfiguration {

    private final Logger log = LoggerFactory.getLogger(AiConfiguration.class);
    private final AiProperties aiProperties;

    public AiConfiguration(AiProperties aiProperties) {
        this.aiProperties = aiProperties;
    }

    // CE BEAN EST LA CLÉ : Il s'applique à TOUS les clients HTTP de l'app
    //    @Bean
    //    public RestClientCustomizer restClientCustomizer() {
    //        return restClientBuilder -> {
    //            log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //            log.info("INJECTION DU FIX APACHE POUR CHAT ET EMBEDDINGS (QDRANT)");
    //            log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    //            factory.setConnectTimeout(20000);
    //            restClientBuilder.requestFactory(factory);
    //            // On force le mode identity pour éviter le GZIP (fix taille 1)
    //            restClientBuilder.defaultHeader("Accept-Encoding", "identity");
    //        };
    //
    //
    //    }
    //

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> {
            log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            log.info("APPLICATION DU FIX 411 VIA JDK HTTP CLIENT (SPRING 6.1+)");
            log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            // JdkClientHttpRequestFactory est le remplaçant moderne et non-déprécié
            // Il calcule automatiquement le Content-Length pour les payloads standards.
            JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
            factory.setReadTimeout(Duration.ofSeconds(60));

            restClientBuilder.requestFactory(factory);
            restClientBuilder.defaultHeader("Accept-Encoding", "identity");
        };
    }

    @Bean
    @Primary
    public MistralAiApi mistralAiApi(RestClient.Builder builder) {
        return new MistralAiApi(aiProperties.getBaseUrl(), aiProperties.getApiKey(), builder, new DefaultResponseErrorHandler());
    }

    @SuppressWarnings("deprecation")
    @Bean
    @Primary
    public MistralAiChatModel mistralAiChatModel(MistralAiApi api) {
        return new MistralAiChatModel(api);
    }

    @Bean
    @Primary
    public MistralAiEmbeddingModel mistralAiEmbeddingModel(MistralAiApi api) {
        // C'est ce bean qui va maintenant utiliser le fix Apache
        return new MistralAiEmbeddingModel(api);
    }
}
