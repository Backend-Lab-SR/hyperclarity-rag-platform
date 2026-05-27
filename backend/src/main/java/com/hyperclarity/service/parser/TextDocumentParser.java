package com.hyperclarity.service.parser;

import com.hyperclarity.exception.DocumentParsingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class TextDocumentParser implements DocumentParser {

    @Override
    public boolean supports(String contentType) {
        return MediaType.TEXT_PLAIN_VALUE.equalsIgnoreCase(contentType);
    }

    @Override
    public String extractText(MultipartFile file) {
        try {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new DocumentParsingException("Failed to parse text document: " + file.getOriginalFilename(), ex);
        }
    }
}
