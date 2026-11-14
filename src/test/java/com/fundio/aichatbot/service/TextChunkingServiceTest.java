
package com.fundio.aichatbot.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TextChunkingServiceTest {

    private TextChunkingService chunkingService;

    @BeforeEach
    void setUp() {
        chunkingService = new TextChunkingService();
    }

    @Test
    void shouldReturnEmptyListForEmptyText() {

        assertTrue(chunkingService.splitText("").isEmpty());
        assertTrue(chunkingService.splitText(null).isEmpty());
    }

    @Test
    void shouldReturnSingleChunkForShortText() {

        String text = "Survey number 372 contains 2 acres.";

        List<String> chunks = chunkingService.splitText(text);

        assertEquals(1, chunks.size());
        assertEquals(text, chunks.get(0));
    }

    @Test
    void shouldSplitLongTextIntoMultipleChunks() {

        String text = ("Survey number 372 contains land ownership details. ")
                .repeat(50);

        List<String> chunks = chunkingService.splitText(text);

        assertTrue(chunks.size() > 1);

        for (String chunk : chunks) {
            assertTrue(chunk.length() <= 1000);
        }
    }

    @Test
    void shouldPreserveAllTextAcrossChunks() {

        String text = ("Anthaiah owned agricultural land in survey number 372. ")
                .repeat(50);

        List<String> chunks = chunkingService.splitText(text);

        assertFalse(chunks.isEmpty());

        assertTrue(chunks.get(0).startsWith("Anthaiah"));
        assertTrue(chunks.get(chunks.size() - 1)
                .contains("survey number 372."));
    }
}
