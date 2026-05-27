package com.hyperclarity.service.parser;

import com.hyperclarity.exception.DocumentParsingException;
import java.io.IOException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PdfDocumentParser implements DocumentParser {

    @Override
    public boolean supports(String contentType) {
        return MediaType.APPLICATION_PDF_VALUE.equalsIgnoreCase(contentType);
    }

    @Override
    public String extractText(MultipartFile file) {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            return new PDFTextStripper().getText(document);
        } catch (IOException ex) {
            throw new DocumentParsingException("Failed to parse PDF document: " + file.getOriginalFilename(), ex);
        }
    }
}
