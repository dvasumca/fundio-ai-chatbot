
package com.fundio.aichatbot.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(DatabaseController.class)
class DatabaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldReturnDatabaseHealthDetails() throws Exception {

        Map<String, Object> databaseDetails = Map.of(
                "database", "fundio_ai",
                "username", "postgres",
                "postgres_version", "PostgreSQL 17"
        );

        when(jdbcTemplate.queryForMap(anyString()))
                .thenReturn(databaseDetails);

        mockMvc.perform(get("/api/database/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.database").value("fundio_ai"))
                .andExpect(jsonPath("$.username").value("postgres"))
                .andExpect(jsonPath("$.postgres_version")
                        .value("PostgreSQL 17"));

        verify(jdbcTemplate).queryForMap(anyString());
    }

    @Test
    void shouldReturnDatabaseHealthAsJson() throws Exception {

        Map<String, Object> databaseDetails = Map.of(
                "database", "fundio_ai",
                "username", "postgres",
                "postgres_version", "PostgreSQL 17"
        );

        when(jdbcTemplate.queryForMap(anyString()))
                .thenReturn(databaseDetails);

        mockMvc.perform(get("/api/database/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        org.springframework.http.MediaType.APPLICATION_JSON));

        verify(jdbcTemplate, times(1)).queryForMap(anyString());
    }
}
