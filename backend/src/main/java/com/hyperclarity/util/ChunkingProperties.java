package com.hyperclarity.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hyperclarity.chunking")
public class ChunkingProperties {

    private int chunkSize = ChunkingDefaults.CHUNK_SIZE;
    private int overlap = ChunkingDefaults.OVERLAP;

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public int getOverlap() {
        return overlap;
    }

    public void setOverlap(int overlap) {
        this.overlap = overlap;
    }
}
