package com.hyperclarity.dto;

public record TextChunk(int chunkIndex, String content, int startPosition, int endPosition) {
}
