
package com.fundio.aichatbot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

class EmbeddingServiceTest {

    private VectorStore vectorStore;
    private EmbeddingService embeddingService;

    @BeforeEach
    void setUp() {
        vectorStore = mock(VectorStore.class);
        embeddingService = new EmbeddingService(vectorStore);
    }

    @Test
    void shouldStoreDocumentTextSuccessfully() {

        String text = "Land belonging to Anthaiah";
        String documentName = "Anthaiah_Land_Record.pdf";
        Integer pageNumber = 1;

        embeddingService.storeText(text, documentName, pageNumber);

        verify(vectorStore).add(anyList());
    }

    @Test
    void shouldStoreCorrectDocumentMetadata() {

        embeddingService.storeText(
                "Land belonging to Anthaiah",
                "Anthaiah_Land_Record.pdf",
                1
        );

        var captor = org.mockito.ArgumentCaptor
                .forClass(List.class);

        verify(vectorStore).add(captor.capture());

        @SuppressWarnings("unchecked")
        List<Document> documents =
                (List<Document>) captor.getValue();

        assertEquals(1, documents.size());
        assertEquals(
                "Land belonging to Anthaiah",
                documents.get(0).getText()
        );
        assertEquals(
                "Anthaiah_Land_Record.pdf",
                documents.get(0).getMetadata().get("documentName")
        );
        assertEquals(
                1,
                documents.get(0).getMetadata().get("pageNumber")
        );
    }

    @Test
    void shouldPropagateExceptionWhenVectorStoreFails() {

        doThrow(new RuntimeException("Vector database unavailable"))
                .when(vectorStore)
                .add(anyList());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> embeddingService.storeText(
                        "Land belonging to Anthaiah",
                        "Anthaiah_Land_Record.pdf",
                        1
                )
        );

        assertEquals(
                "Vector database unavailable",
                exception.getMessage()
        );

        verify(vectorStore).add(anyList());
    }
}
