
package com.fundio.aichatbot.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fundio.aichatbot.service.SearchService;

@WebMvcTest(SearchController.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchService searchService;

    @Test
    void shouldReturnSearchResults() throws Exception {

        String query = "Who owns survey number 372?";

        Document document = new Document(
                "Survey number 372 contains 2 acres.",
                Map.of(
                        "documentName", "Land_Record.pdf",
                        "pageNumber", 5
                )
        );

        when(searchService.search(query))
                .thenReturn(List.of(document));

        mockMvc.perform(post("/api/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "query": "Who owns survey number 372?"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].text")
                        .value("Survey number 372 contains 2 acres."))
                .andExpect(jsonPath("$[0].metadata.documentName")
                        .value("Land_Record.pdf"))
                .andExpect(jsonPath("$[0].metadata.pageNumber")
                        .value(5));

        verify(searchService).search(query);
    }

    @Test
    void shouldReturnEmptyListWhenNoResultsFound() throws Exception {

        when(searchService.search("Unknown survey number"))
                .thenReturn(List.of());

        mockMvc.perform(post("/api/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "query": "Unknown survey number"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(searchService).search("Unknown survey number");
    }

    @Test
    void shouldReturnBadRequestForInvalidJson() throws Exception {

        mockMvc.perform(post("/api/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(searchService);
    }
}
