package com.hyperclarity.service.embedding;

import com.hyperclarity.dto.Embedding;
import com.hyperclarity.exception.EmbeddingGenerationException;
import com.hyperclarity.util.OpenAiProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Service
public class OpenAIEmbeddingService implements EmbeddingService {

    private static final String EMBEDDINGS_PATH = "/v1/embeddings";

    private final OpenAiProperties openAiProperties;
    private final RestClient openAiRestClient;

    public OpenAIEmbeddingService(
            OpenAiProperties openAiProperties,
            @Qualifier("openAiRestClient") RestClient openAiRestClient) {
        this.openAiProperties = openAiProperties;
        this.openAiRestClient = openAiRestClient;
    }

    @Override
    public Embedding generateEmbedding(String text) {
        validateText(text);
        List<Embedding> embeddings = generateEmbeddings(List.of(text));
        return embeddings.get(0);
    }

    @Override
    public List<Embedding> generateEmbeddings(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return Collections.emptyList();
        }

        for (String text : texts) {
            validateText(text);
        }

        validateApiKey();

        CreateEmbeddingRequest request = new CreateEmbeddingRequest(
                openAiProperties.getEmbeddingModel(),
                texts.size() == 1 ? texts.get(0) : texts);

        try {
            CreateEmbeddingResponse response = openAiRestClient.post()
                    .uri(EMBEDDINGS_PATH)
                    .body(request)
                    .retrieve()
                    .body(CreateEmbeddingResponse.class);

            if (response == null || response.data() == null || response.data().isEmpty()) {
                throw new EmbeddingGenerationException("OpenAI returned an empty embeddings response");
            }

            String modelName = response.model() != null ? response.model() : openAiProperties.getEmbeddingModel();
            List<EmbeddingData> orderedData = new ArrayList<>(response.data());
            orderedData.sort(Comparator.comparingInt(EmbeddingData::index));

            List<Embedding> embeddings = new ArrayList<>(orderedData.size());
            for (EmbeddingData item : orderedData) {
                String sourceText = texts.get(item.index());
                embeddings.add(new Embedding(sourceText, List.copyOf(item.embedding()), modelName));
            }
            return Collections.unmodifiableList(embeddings);
        } catch (RestClientResponseException ex) {
            throw new EmbeddingGenerationException(
                    "OpenAI embeddings API request failed: " + ex.getStatusCode().value() + " " + ex.getResponseBodyAsString(),
                    ex);
        } catch (RestClientException ex) {
            throw new EmbeddingGenerationException("Failed to call OpenAI embeddings API", ex);
        }
    }

    private void validateApiKey() {
        if (openAiProperties.getApiKey() == null || openAiProperties.getApiKey().isBlank()) {
            throw new EmbeddingGenerationException("OpenAI API key is not configured (set OPENAI_API_KEY)");
        }
    }

    private void validateText(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text must not be blank");
        }
    }

    private record CreateEmbeddingRequest(String model, Object input) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record CreateEmbeddingResponse(List<EmbeddingData> data, String model) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record EmbeddingData(List<Double> embedding, int index) {
    }
}
