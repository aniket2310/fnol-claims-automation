package org.aniket.fnolclaimsagent.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.aniket.fnolclaimsagent.dto.ClaimAnalysisResultDTO;
import org.aniket.fnolclaimsagent.dto.ExtractedFieldsDTO;
import org.aniket.fnolclaimsagent.model.ClaimRoute;
import org.aniket.fnolclaimsagent.service.RoutingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class RoutingServiceImpl  implements RoutingService {

    private static final String[] INVESTIGATION_KEYWORDS = new String[] {
            "fraud", "inconsistent", "staged", "suspicious", "fabricat"
    };


    @Override
    public ClaimAnalysisResultDTO routeClaim(ExtractedFieldsDTO fields, List<String> missingFields, String rawText) {

        ClaimAnalysisResultDTO result = new ClaimAnalysisResultDTO();
        result.setExtractedFields(fields);

        // 1) Investigation check (highest priority)
        if (rawText != null && containsAnyKeyword(rawText, INVESTIGATION_KEYWORDS)) {
            result.setRecommendedRoute(ClaimRoute.INVESTIGATION.name());
            result.setReasoning("Description contains potential fraud keywords -> Investigation flagged.");
            result.setMissingFields(missingFields);
            return result;
        }
        // 2) Specialist check (injury)
        if (fields != null && fields.getClaimType() != null) {
            String ct = fields.getClaimType().trim().toLowerCase(Locale.ROOT);
            if (ct.contains("injury") || ct.contains("injured")) {
                result.setRecommendedRoute(ClaimRoute.SPECIALIST_QUEUE.name());
                result.setReasoning("Claim type indicates INJURY -> route to Specialist Queue.");
                result.setMissingFields(missingFields);
                return result;
            }
        }

        // 3) Manual review if mandatory fields missing
        if (missingFields != null && !missingFields.isEmpty()) {
            result.setRecommendedRoute(ClaimRoute.MANUAL_REVIEW.name());
            result.setReasoning("One or more mandatory fields missing: " + String.join(", ", missingFields));
            result.setMissingFields(missingFields);
            return result;
        }

        // 4) Fast-track if estimatedDamage < 25000
        BigDecimal amt = parseAmount(fields != null ? fields.getEstimatedDamage() : null);
        if (amt != null) {
            BigDecimal threshold = BigDecimal.valueOf(25000);
            if (amt.compareTo(threshold) < 0) {
                result.setRecommendedRoute(ClaimRoute.FAST_TRACK.name());
                result.setReasoning("Estimated damage < 25,000 -> Fast-track.");
                result.setMissingFields(missingFields);
                return result;
            } else {
                // amount parsed and >= threshold -> manual review by default
                result.setRecommendedRoute(ClaimRoute.MANUAL_REVIEW.name());
                result.setReasoning("Estimated damage >= 25,000 -> Manual review required.");
                result.setMissingFields(missingFields);
                return result;
            }
        }

        // 5) Default fallback
        result.setRecommendedRoute(ClaimRoute.MANUAL_REVIEW.name());
        result.setReasoning("Unable to determine fast-track (no valid estimate). Defaulting to Manual Review.");
        result.setMissingFields(missingFields);
        return result;
    }

    private boolean containsAnyKeyword(String text, String[] keywords) {
        String low = text.toLowerCase(Locale.ROOT);
        for (String k : keywords) {
            if (low.contains(k.toLowerCase(Locale.ROOT))) return true;
        }
        return false;
    }

    private BigDecimal parseAmount(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            // remove commas, currency symbols and spaces
            String cleaned = s.replaceAll("[^0-9.]", "");
            if (cleaned.isBlank()) return null;
            return new BigDecimal(cleaned);
        } catch (Exception ex) {
            log.warn("Could not parse estimated amount '{}': {}", s, ex.getMessage());
            return null;
        }
    }

}
