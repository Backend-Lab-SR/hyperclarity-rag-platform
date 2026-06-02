package com.hyperclarity.service.vectorstore;

import com.hyperclarity.dto.SearchResult;
import com.hyperclarity.dto.VectorDocument;
import java.util.List;

public interface VectorStore {

    void upsert(VectorDocument document);

    void upsert(List<VectorDocument> documents);

    List<SearchResult> similaritySearch(List<Double> queryEmbedding, int topK);
}
