
package com.fundio.aichatbot.controller;

import com.fundio.aichatbot.dto.EmbeddingRequest;
import com.fundio.aichatbot.dto.EmbeddingResponse;
import com.fundio.aichatbot.service.EmbeddingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/embeddings")
public class EmbeddingController {

    private static final Logger log =
            LoggerFactory.getLogger(EmbeddingController.class);

    private final EmbeddingService embeddingService;

    public EmbeddingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostMapping
    public EmbeddingResponse storeEmbedding(
            @RequestBody EmbeddingRequest request) {

        log.info("Received document embedding request: {}",
                request.documentName());

        embeddingService.storeText(
                request.text(),
                request.documentName(),
                request.pageNumber()
        );

        log.info("Document embedding stored successfully");

        return new EmbeddingResponse(
                request.text(),
                1024,
                "Document embedding stored successfully"
        );
    }
}
