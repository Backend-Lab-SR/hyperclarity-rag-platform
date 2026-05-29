package com.hyperclarity.service;

import com.hyperclarity.dto.TextChunk;
import com.hyperclarity.util.ChunkingProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ChunkingService {

    private final ChunkingProperties chunkingProperties;

    public ChunkingService(ChunkingProperties chunkingProperties) {
        this.chunkingProperties = chunkingProperties;
    }

    public List<TextChunk> chunk(String text) {
        return chunk(text, chunkingProperties.getChunkSize(), chunkingProperties.getOverlap());
    }

    public List<TextChunk> chunk(String text, int chunkSize, int overlap) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        validateChunkingConfig(chunkSize, overlap);

        List<TextChunk> chunks = new ArrayList<>();
        int textLength = text.length();
        int start = 0;
        int chunkIndex = 0;
        int step = chunkSize - overlap;

        while (start < textLength) {
            int end = Math.min(start + chunkSize, textLength);
            String content = text.substring(start, end);
            chunks.add(new TextChunk(chunkIndex, content, start, end));
            chunkIndex++;

            if (end >= textLength) {
                break;
            }

            start += step;
        }

        return Collections.unmodifiableList(chunks);
    }

    private void validateChunkingConfig(int chunkSize, int overlap) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be greater than 0");
        }
        if (overlap < 0) {
            throw new IllegalArgumentException("overlap must be greater than or equal to 0");
        }
        if (overlap >= chunkSize) {
            throw new IllegalArgumentException("overlap must be less than chunkSize");
        }
    }
}
