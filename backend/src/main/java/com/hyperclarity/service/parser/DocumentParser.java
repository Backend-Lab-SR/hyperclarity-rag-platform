package com.hyperclarity.service.parser;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentParser {

    boolean supports(String contentType);

    String extractText(MultipartFile file);
}
