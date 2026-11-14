
package com.fundio.aichatbot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class DocumentIngestionServiceTest {

    private PdfExtractionService pdfExtractionService;
    private TextChunkingService textChunkingService;
    private EmbeddingService embeddingService;

    private DocumentIngestionService ingestionService;

    @BeforeEach
    void setUp() {

        pdfExtractionService = mock(PdfExtractionService.class);
        textChunkingService = mock(TextChunkingService.class);
        embeddingService = mock(EmbeddingService.class);

        ingestionService = new DocumentIngestionService(
                pdfExtractionService,
                textChunkingService,
                embeddingService
        );
    }

    @Test
    void shouldExtractChunkAndStorePdfContent() throws IOException {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "Land_Record.pdf",
                "application/pdf",
                "sample PDF".getBytes()
        );

        when(pdfExtractionService.extractText(file))
                .thenReturn(List.of(
                        new PdfExtractionService.PageContent(
                                1, "Survey number 372 contains 2 acres."
                        ),
                        new PdfExtractionService.PageContent(
                                2, "Owner name: Anthaiah."
                        )
                ));

        when(textChunkingService.splitText(
                "Survey number 372 contains 2 acres."))
                .thenReturn(List.of(
                        "Survey number 372 contains 2 acres."
                ));

        when(textChunkingService.splitText(
                "Owner name: Anthaiah."))
                .thenReturn(List.of(
                        "Owner name: Anthaiah."
                ));

        when(embeddingService.storeChunks(
                anyList(), anyString(), anyInt()))
                .thenReturn(1);

        int result = ingestionService.ingestPdf(file);

        assertEquals(2, result);

        verify(pdfExtractionService).extractText(file);

        verify(textChunkingService, times(2))
                .splitText(anyString());

        verify(embeddingService).storeChunks(
                List.of("Survey number 372 contains 2 acres."),
                "Land_Record.pdf",
                1
        );

        verify(embeddingService).storeChunks(
                List.of("Owner name: Anthaiah."),
                "Land_Record.pdf",
                2
        );
    }
}
