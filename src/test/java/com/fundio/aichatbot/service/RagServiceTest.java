
package com.fundio.aichatbot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;

class RagServiceTest {

    private ChatClient chatClient;
    private ChatClient.Builder chatClientBuilder;
    private ChatClient.ChatClientRequestSpec requestSpec;
    private ChatClient.CallResponseSpec responseSpec;

    private SearchService searchService;
    private RagService ragService;

    @BeforeEach
    void setUp() {

        chatClient = mock(ChatClient.class);
        chatClientBuilder = mock(ChatClient.Builder.class);
        requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        responseSpec = mock(ChatClient.CallResponseSpec.class);

        searchService = mock(SearchService.class);

        when(chatClientBuilder.build()).thenReturn(chatClient);

        ragService = new RagService(chatClientBuilder, searchService);
    }

    @Test
    void shouldGenerateAnswerUsingRetrievedDocuments() {

        String question = "Who owns this land?";

        Document document = new Document(
                "Land belonging to Anthaiah",
                java.util.Map.of(
                        "documentName", "Anthaiah_Land_Record.pdf",
                        "pageNumber", 1
                )
        );

        when(searchService.search(question))
                .thenReturn(List.of(document));

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content())
                .thenReturn("The land belongs to Anthaiah.");

        String answer = ragService.ask(question);

        assertEquals("The land belongs to Anthaiah.", answer);

        verify(searchService).search(question);
        verify(chatClient).prompt();
        verify(responseSpec).content();
    }

    @Test
    void shouldReturnMessageWhenNoDocumentsFound() {

        String question = "Who owns this land?";

        when(searchService.search(question))
                .thenReturn(List.of());

        String answer = ragService.ask(question);

        assertEquals(
                "No relevant information found in the uploaded documents.",
                answer
        );

        verify(searchService).search(question);
        verifyNoInteractions(chatClient);
    }

    @Test
    void shouldIncludeDocumentDetailsInLlmContext() {

        String question = "Who owns this land?";

        Document document = new Document(
                "Land belonging to Anthaiah",
                java.util.Map.of(
                        "documentName", "Anthaiah_Land_Record.pdf",
                        "pageNumber", 2
                )
        );

        when(searchService.search(question))
                .thenReturn(List.of(document));

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn("Anthaiah");

        ragService.ask(question);

        org.mockito.ArgumentCaptor<String> contextCaptor =
                org.mockito.ArgumentCaptor.forClass(String.class);

        verify(requestSpec).user(contextCaptor.capture());

        String actualContext = contextCaptor.getValue();

        assertTrue(actualContext.contains("Anthaiah_Land_Record.pdf"));
        assertTrue(actualContext.contains("Page: 2"));
        assertTrue(actualContext.contains("Land belonging to Anthaiah"));
        assertTrue(actualContext.contains(question));
    }
}
