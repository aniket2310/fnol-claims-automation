package org.aniket.fnolclaimsagent.service;
import org.springframework.web.multipart.MultipartFile;


public interface DocumentExtractionService {

    String extractText(MultipartFile file);
}
