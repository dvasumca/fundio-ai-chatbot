
package com.fundio.aichatbot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

class ChatServiceTest {

    private ChatClient chatClient;
    private ChatClient.Builder chatClientBuilder;
    private ChatClient.ChatClientRequestSpec requestSpec;
    private ChatClient.CallResponseSpec responseSpec;

    private ChatService chatService;

    @BeforeEach
    void setUp() {

        chatClient = mock(ChatClient.class);
        chatClientBuilder = mock(ChatClient.Builder.class);
        requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        responseSpec = mock(ChatClient.CallResponseSpec.class);

        when(chatClientBuilder.build()).thenReturn(chatClient);

        chatService = new ChatService(chatClientBuilder);
    }

    @Test
    void shouldReturnChatResponse() {

        String message = "Hello, introduce yourself";
        String expectedResponse = "Hello! I am FUN-DIO AI Chatbot.";

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(message)).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn(expectedResponse);

        String actualResponse = chatService.getResponse(message);

        assertEquals(expectedResponse, actualResponse);

        verify(chatClient).prompt();
        verify(requestSpec).user(message);
        verify(requestSpec).call();
        verify(responseSpec).content();
    }

    @Test
    void shouldPassUserMessageToChatClient() {

        String message = "What is RAG?";

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(message)).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn("RAG retrieves relevant information.");

        chatService.getResponse(message);

        verify(requestSpec, times(1)).user(message);
    }
}
