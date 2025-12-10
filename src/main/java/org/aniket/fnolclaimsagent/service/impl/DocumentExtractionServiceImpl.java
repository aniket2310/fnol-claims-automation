package org.aniket.fnolclaimsagent.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.aniket.fnolclaimsagent.service.DocumentExtractionService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class DocumentExtractionServiceImpl implements DocumentExtractionService {

    private static final String DEFAULT_SAMPLE_PATH = "fnols/sample.pdf";

    @Override
    public String extractText(MultipartFile file) {
        try {
            // If no file provided or empty -> fallback to classpath sample
            if (file == null || file.isEmpty()) {
                log.info("No uploaded file provided or file is empty. Falling back to classpath resource: {}", DEFAULT_SAMPLE_PATH);
                return extractTextFromClasspath(DEFAULT_SAMPLE_PATH);
            }

            String contentType = file.getContentType();
            log.info("Extracting text from uploaded file: {}, contentType={}", file.getOriginalFilename(), contentType);

            // If content type indicates PDF -> use PDFBox
            if (contentType != null && contentType.equalsIgnoreCase("application/pdf")) {
                return extractTextFromPdf(file);
            }

            // For text-like or unknown types, attempt to read as plain text first.
            // If plain-text reading fails or is empty, try PDF as a fallback (some PDFs may be sent with different content-type).
            if (contentType != null && contentType.startsWith("text/")) {
                return extractTextFromPlainText(file);
            }

            // Unknown content-type: try PDF first (many clients send application/octet-stream for PDFs)
            try {
                return extractTextFromPdf(file);
            } catch (IOException pdfEx) {
                log.warn("Failed to parse uploaded file as PDF (trying plain text): {}", pdfEx.getMessage());
                // fallback to plain text read
                return extractTextFromPlainText(file);
            }

        } catch (IOException e) {
            log.error("Error while extracting text from file or classpath resource", e);
            throw new RuntimeException("Failed to extract text: " + e.getMessage(), e);
        }
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        byte[] pdfBytes = file.getBytes();
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractTextFromPlainText(MultipartFile file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    /**
     * Read a resource from classpath (src/main/resources/...) and extract text.
     * Uses PDF parsing for .pdf files; otherwise reads as plain text.
     */
    private String extractTextFromClasspath(String resourcePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(resourcePath);

        if (!resource.exists()) {
            throw new IOException("Classpath resource not found: " + resourcePath);
        }

        String lower = resourcePath.toLowerCase();
        if (lower.endsWith(".pdf")) {
            // PDFBox 3.x requires byte[]
            byte[] pdfBytes = resource.getContentAsByteArray();
            try (PDDocument document = Loader.loadPDF(pdfBytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        } else {
            // treat as plain text resource
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
            }
            return sb.toString();
        }
    }
}
