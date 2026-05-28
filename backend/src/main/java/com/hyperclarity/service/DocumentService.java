package com.hyperclarity.service;

import com.hyperclarity.dto.DocumentResponse;
import com.hyperclarity.dto.UploadResponse;
import com.hyperclarity.entity.DocumentEntity;
import com.hyperclarity.exception.DocumentNotFoundException;
import com.hyperclarity.exception.InvalidDocumentUploadException;
import com.hyperclarity.exception.UnsupportedFileTypeException;
import com.hyperclarity.repository.DocumentRepository;
import com.hyperclarity.service.parser.DocumentParser;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final List<DocumentParser> documentParsers;

    public DocumentService(DocumentRepository documentRepository, List<DocumentParser> documentParsers) {
        this.documentRepository = documentRepository;
        this.documentParsers = documentParsers;
    }

    public UploadResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidDocumentUploadException("Uploaded file is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new InvalidDocumentUploadException("Uploaded file must have a filename");
        }

        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            throw new UnsupportedFileTypeException("unknown");
        }

        DocumentParser parser = documentParsers.stream()
                .filter(candidate -> candidate.supports(contentType))
                .findFirst()
                .orElseThrow(() -> new UnsupportedFileTypeException(contentType));

        String extractedText = parser.extractText(file);

        DocumentEntity document = new DocumentEntity();
        document.setFilename(filename);
        document.setContentType(contentType);
        document.setExtractedText(extractedText);
        document.setFileSize(file.getSize());

        DocumentEntity saved = documentRepository.save(document);

        return new UploadResponse(
                saved.getId(),
                saved.getFilename(),
                saved.getContentType(),
                saved.getUploadedAt());
    }

    public List<DocumentResponse> listDocuments() {
        return documentRepository.findAll().stream()
                .map(this::toMetadataResponse)
                .toList();
    }

    public DocumentResponse getDocument(UUID id) {
        DocumentEntity document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));
        return toDetailResponse(document);
    }

    private DocumentResponse toMetadataResponse(DocumentEntity document) {
        return new DocumentResponse(
                document.getId(),
                document.getFilename(),
                document.getContentType(),
                document.getFileSize(),
                document.getUploadedAt(),
                null);
    }

    private DocumentResponse toDetailResponse(DocumentEntity document) {
        return new DocumentResponse(
                document.getId(),
                document.getFilename(),
                document.getContentType(),
                document.getFileSize(),
                document.getUploadedAt(),
                document.getExtractedText());
    }
}
