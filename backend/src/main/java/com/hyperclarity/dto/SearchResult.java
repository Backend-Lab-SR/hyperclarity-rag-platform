package com.hyperclarity.dto;

import java.util.UUID;

public record SearchResult(UUID documentId, String chunkId, String content, double similarityScore) {
}
