
package com.fundio.aichatbot.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import com.fundio.aichatbot.dto.SearchRequest;
import com.fundio.aichatbot.service.SearchService;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private static final Logger log =
            LoggerFactory.getLogger(SearchController.class);

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping
    public List<Document> search(@RequestBody SearchRequest request) {

        log.info("Received semantic search request");

        List<Document> results =
                searchService.search(request.query());

        log.info("Semantic search returned {} results", results.size());

        return results;
    }
}
