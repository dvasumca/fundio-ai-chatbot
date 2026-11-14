
package com.fundio.aichatbot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    private static final Logger log =
            LoggerFactory.getLogger(EmbeddingService.class);

    private final VectorStore vectorStore;

    public EmbeddingService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        log.info("EmbeddingService initialized successfully");
    }

    // Existing manual text storage method
    public void storeText(
            String text,
            String documentName,
            Integer pageNumber) {

        log.info("Storing document text. Document: {}, Page: {}",
                documentName, pageNumber);

        try {
            Document document = new Document(
                    text,
                    Map.of(
                            "documentName", documentName,
                            "pageNumber", pageNumber
                    )
            );

            vectorStore.add(List.of(document));

            log.info("Document embedding stored successfully");

        } catch (Exception e) {
            log.error("Failed to store document embedding", e);
            throw e;
        }
    }

    // New method for storing multiple PDF text chunks
    public int storeChunks(
            List<String> chunks,
            String documentName,
            Integer pageNumber) {

        if (chunks == null || chunks.isEmpty()) {
            throw new IllegalArgumentException(
                    "No text chunks available for storage");
        }

        log.info("Storing {} chunks. Document: {}, Page: {}",
                chunks.size(), documentName, pageNumber);

        try {
            List<Document> documents = new ArrayList<>();

            for (int i = 0; i < chunks.size(); i++) {

                Map<String, Object> metadata = new HashMap<>();

                metadata.put("documentName", documentName);
                metadata.put("pageNumber", pageNumber);
                metadata.put("chunkNumber", i + 1);

                Document document = new Document(
                        chunks.get(i),
                        metadata
                );

                documents.add(document);
            }

            vectorStore.add(documents);

            log.info("{} chunks stored successfully", documents.size());

            return documents.size();

        } catch (Exception e) {
            log.error("Failed to store document chunks", e);
            throw e;
        }
    }
}
