
package com.fundio.aichatbot.controller;

import com.fundio.aichatbot.dto.SearchRequest;
import com.fundio.aichatbot.service.RagService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private static final Logger log =
            LoggerFactory.getLogger(RagController.class);

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/ask")
    public String ask(@RequestBody SearchRequest request) {

        log.info("Received RAG question");

        String answer = ragService.ask(request.query());

        log.info("RAG answer generated successfully");

        return answer;
    }
}
