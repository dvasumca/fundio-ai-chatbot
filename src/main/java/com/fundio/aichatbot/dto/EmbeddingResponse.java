
package com.fundio.aichatbot.dto;

public record EmbeddingResponse(
        String text,
        int dimensions,
        String status
) {
}
