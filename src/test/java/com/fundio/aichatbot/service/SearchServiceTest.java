
package com.fundio.aichatbot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

class SearchServiceTest {

    private VectorStore vectorStore;
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        vectorStore = mock(VectorStore.class);
        searchService = new SearchService(vectorStore);
    }

    @Test
    void shouldReturnRelevantDocuments() {

        String query = "Who owns this land?";

        List<Document> expectedDocuments = List.of(
                new Document("Land belonging to Anthaiah")
        );

        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(expectedDocuments);

        List<Document> actualDocuments = searchService.search(query);

        assertNotNull(actualDocuments);
        assertEquals(1, actualDocuments.size());
        assertEquals(
                "Land belonging to Anthaiah",
                actualDocuments.get(0).getText()
        );

        verify(vectorStore).similaritySearch(any(SearchRequest.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoDocumentsFound() {

        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of());

        List<Document> results =
                searchService.search("Unknown land owner");

        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    void shouldPassCorrectQueryAndTopKToVectorStore() {

        String query = "Land belonging to Anthaiah";

        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of());

        searchService.search(query);

        var captor = org.mockito.ArgumentCaptor
                .forClass(SearchRequest.class);

        verify(vectorStore).similaritySearch(captor.capture());

        SearchRequest capturedRequest = captor.getValue();

        assertEquals(query, capturedRequest.getQuery());
        assertEquals(3, capturedRequest.getTopK());
    }
}
