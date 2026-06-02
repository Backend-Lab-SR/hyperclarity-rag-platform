package com.hyperclarity.config;

import com.hyperclarity.util.OpenAiDefaults;
import com.hyperclarity.util.OpenAiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class OpenAiConfig {

    @Bean
    public RestClient openAiRestClient(OpenAiProperties openAiProperties, RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .baseUrl(OpenAiDefaults.API_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiProperties.getApiKey())
                .build();
    }
}
