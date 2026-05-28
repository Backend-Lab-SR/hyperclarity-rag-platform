package com.hyperclarity.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UploadResponse(
        UUID id,
        String filename,
        String contentType,
        LocalDateTime uploadedAt) {
}
