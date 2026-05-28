package com.hyperclarity.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentResponse(
        UUID id,
        String filename,
        String contentType,
        Long fileSize,
        LocalDateTime uploadedAt,
        String extractedText) {
}
