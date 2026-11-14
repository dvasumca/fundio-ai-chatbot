
package com.fundio.aichatbot.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentIngestionService {

    private static final Logger log =
            LoggerFactory.getLogger(DocumentIngestionService.class);

    private final PdfExtractionService pdfExtractionService;
    private final TextChunkingService textChunkingService;
    private final EmbeddingService embeddingService;

    public DocumentIngestionService(
            PdfExtractionService pdfExtractionService,
            TextChunkingService textChunkingService,
            EmbeddingService embeddingService) {

        this.pdfExtractionService = pdfExtractionService;
        this.textChunkingService = textChunkingService;
        this.embeddingService = embeddingService;
    }

    public int ingestPdf(MultipartFile file) throws IOException {

        String documentName = file.getOriginalFilename();

        log.info("Starting PDF ingestion: {}", documentName);

        // 1. Extract text page by page
        List<PdfExtractionService.PageContent> pages =
                pdfExtractionService.extractText(file);

        int totalChunks = 0;

        // 2. Chunk each page and store its embeddings
        for (PdfExtractionService.PageContent page : pages) {

            List<String> chunks =
                    textChunkingService.splitText(page.text());

            // 3. Store chunks in PGVector
            int storedChunks = embeddingService.storeChunks(
                    chunks,
                    documentName,
                    page.pageNumber()
            );

            totalChunks += storedChunks;
        }

        log.info(
                "PDF ingestion completed. Document: {}, Pages: {}, Chunks: {}",
                documentName,
                pages.size(),
                totalChunks
        );

        return totalChunks;
    }
}
