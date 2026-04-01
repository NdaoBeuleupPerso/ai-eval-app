package com.mycompany.iaeval.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    @Value("${spring.ai.mistralai.api-key}")
    private String apiKey;

    private final RestClient.Builder restClientBuilder; // Injectez le builder !

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiService(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    public String askIA(String prompt) {
        try {
            // 1. Configuration du client Apache (Fix 411)
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            factory.setConnectTimeout(15000);
            factory.setReadTimeout(60000);

            // RestClient restClient = RestClient.builder().requestFactory(factory).build();
            RestClient restClient = restClientBuilder.build();

            // 2. Appel API Mistral
            log.info("Appel Mistral AI...");

            ResponseEntity<String> response = restClient
                .post()
                .uri("https://api.mistral.ai/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("Accept-Encoding", "identity") // Interdit la compression GZIP (évite le taille: 1)
                .header("User-Agent", "Java/IaEvalApp") // Identifie l'appel (évite les blocages Cloudflare)
                .body(Map.of("model", "mistral-small-latest", "messages", List.of(Map.of("role", "user", "content", prompt))))
                .retrieve()
                .toEntity(String.class); // On récupère l'entité complète

            String rawJson = response.getBody();

            if (rawJson == null || rawJson.length() < 5) {
                return "ERREUR : Réponse vide ou trop courte du serveur.";
            }

            // 3. Extraction propre avec Jackson (plus de split manuel)
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode contentNode = root.path("choices").get(0).path("message").path("content");

            if (contentNode.isMissingNode()) {
                log.error("JSON invalide : {}", rawJson);
                return "ERREUR : Format de réponse IA inconnu.";
            }

            return contentNode.asText();
        } catch (Exception e) {
            log.error("ERREUR IA : ", e);
            return "ERREUR IA TECHNIQUE : " + e.getMessage();
        }
    }
}
