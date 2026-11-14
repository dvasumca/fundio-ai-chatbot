
package com.fundio.aichatbot.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

import com.fundio.aichatbot.service.DocumentIngestionService;

@WebMvcTest(DocumentUploadController.class)
class DocumentUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentIngestionService ingestionService;

    @Test
    void shouldUploadPdfSuccessfully() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "Land_Record.pdf",
                "application/pdf",
                "sample PDF content".getBytes()
        );

        when(ingestionService.ingestPdf(file)).thenReturn(5);

        mockMvc.perform(multipart("/api/documents/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentName").value("Land_Record.pdf"))
                .andExpect(jsonPath("$.chunksStored").value(5))
                .andExpect(jsonPath("$.status")
                        .value("PDF uploaded and processed successfully"));
    }

    @Test
    void shouldRejectInvalidPdf() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                "sample text".getBytes()
        );

        when(ingestionService.ingestPdf(file))
                .thenThrow(new IllegalArgumentException("Only PDF files are allowed"));

        mockMvc.perform(multipart("/api/documents/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Only PDF files are allowed"));
    }

    @Test
    void shouldHandlePdfReadError() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                "invalid PDF".getBytes()
        );

        when(ingestionService.ingestPdf(file))
                .thenThrow(new IOException("PDF read error"));

        mockMvc.perform(multipart("/api/documents/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Unable to read PDF file"));
    }
}
