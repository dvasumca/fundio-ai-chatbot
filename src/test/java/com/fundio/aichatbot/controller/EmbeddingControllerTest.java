
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

import com.fundio.aichatbot.service.EmbeddingService;

@WebMvcTest(EmbeddingController.class)
class EmbeddingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmbeddingService embeddingService;

    @Test
    void shouldStoreEmbeddingSuccessfully() throws Exception {

        mockMvc.perform(post("/api/embeddings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "text": "This document contains land ownership details.",
                        "documentName": "Anthaiah_Land_Record.pdf",
                        "pageNumber": 1
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text")
                        .value("This document contains land ownership details."))
                .andExpect(jsonPath("$.dimensions").value(1024))
                .andExpect(jsonPath("$.status")
                        .value("Document embedding stored successfully"));

        verify(embeddingService).storeText(
                "This document contains land ownership details.",
                "Anthaiah_Land_Record.pdf",
                1
        );
    }

    @Test
    void shouldPassCorrectDocumentDetailsToService() throws Exception {

        mockMvc.perform(post("/api/embeddings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "text": "Survey number 372 contains 2 acres.",
                        "documentName": "Land_Record.pdf",
                        "pageNumber": 5
                    }
                    """))
                .andExpect(status().isOk());

        verify(embeddingService).storeText(
                "Survey number 372 contains 2 acres.",
                "Land_Record.pdf",
                5
        );
    }

    @Test
    void shouldReturnBadRequestForInvalidJson() throws Exception {

        mockMvc.perform(post("/api/embeddings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(embeddingService);
    }
}
