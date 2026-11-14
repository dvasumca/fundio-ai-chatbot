
package com.fundio.aichatbot.dto;

public record EmbeddingRequest(
        String text,
        String documentName,
        Integer pageNumber
) {
}