package com.hyperclarity.dto;

import java.util.List;
import java.util.UUID;

public record VectorDocument(UUID documentId, String chunkId, String content, List<Double> embedding) {
}
