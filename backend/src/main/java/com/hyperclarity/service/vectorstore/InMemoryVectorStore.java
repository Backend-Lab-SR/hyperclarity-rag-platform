package com.hyperclarity.service.vectorstore;

import com.hyperclarity.dto.SearchResult;
import com.hyperclarity.dto.VectorDocument;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Local-only vector store for development and testing. Not suitable for production.
 */
public class InMemoryVectorStore implements VectorStore {

    private final Map<String, VectorDocument> documents = new ConcurrentHashMap<>();

    @Override
    public void upsert(VectorDocument document) {
        validateDocument(document);
        documents.put(storageKey(document), document);
    }

    @Override
    public void upsert(List<VectorDocument> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }
        for (VectorDocument document : documents) {
            upsert(document);
        }
    }

    @Override
    public List<SearchResult> similaritySearch(List<Double> queryEmbedding, int topK) {
        validateQueryEmbedding(queryEmbedding);
        if (topK <= 0 || documents.isEmpty()) {
            return List.of();
        }

        List<SearchResult> results = new ArrayList<>(documents.size());
        for (VectorDocument document : documents.values()) {
            double score = cosineSimilarity(queryEmbedding, document.embedding());
            results.add(new SearchResult(
                    document.documentId(),
                    document.chunkId(),
                    document.content(),
                    score));
        }

        results.sort(Comparator.comparingDouble(SearchResult::similarityScore).reversed());
        if (results.size() <= topK) {
            return List.copyOf(results);
        }
        return List.copyOf(results.subList(0, topK));
    }

    private String storageKey(VectorDocument document) {
        return document.documentId() + ":" + document.chunkId();
    }

    private void validateDocument(VectorDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("Vector document must not be null");
        }
        if (document.documentId() == null) {
            throw new IllegalArgumentException("documentId must not be null");
        }
        if (document.chunkId() == null || document.chunkId().isBlank()) {
            throw new IllegalArgumentException("chunkId must not be blank");
        }
        if (document.content() == null || document.content().isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }
        validateEmbedding(document.embedding());
    }

    private void validateQueryEmbedding(List<Double> queryEmbedding) {
        if (queryEmbedding == null || queryEmbedding.isEmpty()) {
            throw new IllegalArgumentException("queryEmbedding must not be null or empty");
        }
    }

    private void validateEmbedding(List<Double> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            throw new IllegalArgumentException("embedding must not be null or empty");
        }
    }

    private double cosineSimilarity(List<Double> left, List<Double> right) {
        if (left.size() != right.size()) {
            throw new IllegalArgumentException("Embedding dimensions must match");
        }

        double dotProduct = 0.0;
        double leftMagnitude = 0.0;
        double rightMagnitude = 0.0;

        for (int i = 0; i < left.size(); i++) {
            double leftValue = left.get(i);
            double rightValue = right.get(i);
            dotProduct += leftValue * rightValue;
            leftMagnitude += leftValue * leftValue;
            rightMagnitude += rightValue * rightValue;
        }

        if (leftMagnitude == 0.0 || rightMagnitude == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(leftMagnitude) * Math.sqrt(rightMagnitude));
    }
}
