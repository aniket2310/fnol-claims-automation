package org.aniket.fnolclaimsagent.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aniket.fnolclaimsagent.dto.ClaimAnalysisResultDTO;
import org.aniket.fnolclaimsagent.dto.ExtractedFieldsDTO;
import org.aniket.fnolclaimsagent.dto.TextExtractionResponse;
import org.aniket.fnolclaimsagent.model.Claim;
import org.aniket.fnolclaimsagent.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class ClaimController {

    private final DocumentExtractionService documentExtractionService;
    private final FieldExtractionService fieldExtractionService;
    private final ValidationService validationService;
    private final RoutingService routingService;
    private final ClaimService claimService;


    @Operation(summary = "Upload file & extract raw text")
    @PostMapping("/extract-text")
    public ResponseEntity<?> extractTextFromFile(
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            String text = documentExtractionService.extractText(file);
            TextExtractionResponse response = new TextExtractionResponse(text);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("Failed to extract text", ex);
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Failed to extract text");
            err.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @Operation(summary = "Extract fields + validate + routing decision")
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeClaim(
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            String text = documentExtractionService.extractText(file);
            ExtractedFieldsDTO extracted = fieldExtractionService.extractFields(text);
            List<String> missing = validationService.findMissingFields(extracted);

            ClaimAnalysisResultDTO result = routingService.routeClaim(extracted, missing, text);
            result.setRawText(text);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            log.error("Failed to analyze claim", ex);
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Failed to analyze claim");
            err.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @Operation(summary = "Save Analyzed Claim")
    @PostMapping("/save")
    public ResponseEntity<?> saveClaim(@RequestBody ClaimAnalysisResultDTO analysis) {
        try {
            Claim saved = claimService.saveFromAnalysis(analysis);
            return ResponseEntity.ok(saved);
        } catch (Exception ex) {
            log.error("Failed to save claim", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
        }
    }

    @Operation(summary = "List all saved claims")
    @GetMapping
    public ResponseEntity<List<Claim>> listAll() {
        return ResponseEntity.ok(claimService.findAll());
    }

    @Operation(summary = "Get single claim by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Claim> getById(@PathVariable Long id) {
        Optional<Claim> opt = claimService.findById(id);
        if (opt.isPresent()) {
            return ResponseEntity.ok(opt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }



}
