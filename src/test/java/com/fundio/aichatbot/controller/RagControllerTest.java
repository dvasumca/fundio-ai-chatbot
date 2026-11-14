
package com.fundio.aichatbot.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fundio.aichatbot.service.RagService;

@WebMvcTest(RagController.class)
class RagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RagService ragService;

    @Test
    void shouldReturnRagAnswer() throws Exception {

        String question = "Who owns survey number 372?";
        String expectedAnswer = "The document contains ownership details.";

        when(ragService.ask(question)).thenReturn(expectedAnswer);

        mockMvc.perform(post("/api/rag/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "query": "Who owns survey number 372?"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedAnswer));

        verify(ragService).ask(question);
    }

    @Test
    void shouldReturnBadRequestForInvalidJson() throws Exception {

        mockMvc.perform(post("/api/rag/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(ragService);
    }
}
