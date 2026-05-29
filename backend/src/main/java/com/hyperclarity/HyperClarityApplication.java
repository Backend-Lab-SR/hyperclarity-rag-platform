package com.hyperclarity;

import com.hyperclarity.util.ChunkingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ChunkingProperties.class)
public class HyperClarityApplication {

    public static void main(String[] args) {
        SpringApplication.run(HyperClarityApplication.class, args);
    }
}
