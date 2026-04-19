package com.mycompany.iaeval.service;

import com.mycompany.iaeval.domain.enumeration.TypeSource;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
// 2. Imports Apache Tika
import org.apache.tika.Tika;
import org.springframework.ai.document.Document; // <--- C'est lui et UNIQUEMENT lui
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class IngestionService {

    private final VectorStore vectorStore;
    private final Tika tika = new Tika();

    public IngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void extraireEtVectoriser(byte[] fileContent, String fileName, TypeSource type) throws Exception {
        // 1. Extraction automatique du texte (PDF ou Image)
        String texteExtrait = tika.parseToString(new ByteArrayInputStream(fileContent));

        // 2. Découpage simple par blocs de 1000 caractères
        // (En prod, utilisez TokenTextSplitter de Spring AI)
        List<String> chunks = splitText(texteExtrait, 1000);

        // 3. Envoi groupé à Qdrant avec les métadonnées de type
        List<Document> documents = chunks
            .stream()
            .map(chunk ->
                new Document(chunk, Map.of("source_type", type.name(), "file_name", fileName, "date_ingestion", Instant.now().toString()))
            )
            .toList();

        vectorStore.add(documents);
    }

    private List<String> splitText(String text, int size) {
        // Logique de découpage rudimentaire pour l'exemple
        List<String> ret = new ArrayList<>();
        for (int i = 0; i < text.length(); i += size) {
            ret.add(text.substring(i, Math.min(text.length(), i + size)));
        }
        return ret;
    }
}
