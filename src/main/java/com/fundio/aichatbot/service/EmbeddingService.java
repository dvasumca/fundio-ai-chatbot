
package com.fundio.aichatbot.service;

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

            vectorStore.add(java.util.List.of(document));

            log.info("Document embedding stored successfully");

        } catch (Exception e) {
            log.error("Failed to store document embedding", e);
            throw e;
        }
    }
}
