package com.hyperclarity.service.embedding;

import com.hyperclarity.dto.Embedding;
import java.util.List;

public interface EmbeddingService {

    Embedding generateEmbedding(String text);

    List<Embedding> generateEmbeddings(List<String> texts);
}
