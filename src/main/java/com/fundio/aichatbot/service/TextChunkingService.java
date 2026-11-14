
package com.fundio.aichatbot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TextChunkingService {

    private static final int CHUNK_SIZE = 1000;
    private static final int CHUNK_OVERLAP = 150;

    public List<String> splitText(String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        List<String> chunks = new ArrayList<>();

        String normalizedText = text.trim();

        int start = 0;

        while (start < normalizedText.length()) {

            int end = Math.min(
                    start + CHUNK_SIZE,
                    normalizedText.length()
            );

            if (end < normalizedText.length()) {

                int boundary = normalizedText.lastIndexOf(' ', end);

                if (boundary > start + CHUNK_SIZE / 2) {
                    end = boundary;
                }
            }

            String chunk = normalizedText
                    .substring(start, end)
                    .trim();

            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            if (end >= normalizedText.length()) {
                break;
            }

            start = end - CHUNK_OVERLAP;
        }

        return chunks;
    }
}
