package com.hyperclarity.config;

import com.hyperclarity.service.vectorstore.InMemoryVectorStore;
import com.hyperclarity.service.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {

    @Bean
    public VectorStore vectorStore() {
        return new InMemoryVectorStore();
    }
}
