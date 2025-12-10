package org.aniket.fnolclaimsagent.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.aniket.fnolclaimsagent.dto.ExtractedFieldsDTO;
import org.aniket.fnolclaimsagent.service.FieldExtractionService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class FieldExtractionServiceImpl implements FieldExtractionService {
    private static final Pattern DATE_OF_LOSS_PATTERN =
            Pattern.compile("DATE OF LOSS[^0-9]*([0-9]{1,2}/[0-9]{1,2}/[0-9]{2,4})", Pattern.CASE_INSENSITIVE);

    private static final Pattern ESTIMATE_AMOUNT_PATTERN =
            Pattern.compile("ESTIMATE AMOUNT[:\\s\\$]*([0-9,\\.]+)", Pattern.CASE_INSENSITIVE);

    private static final Pattern VIN_PATTERN =
            Pattern.compile("V\\.I\\.N\\.?[:\\s]*([A-Za-z0-9-]+)", Pattern.CASE_INSENSITIVE);

    @Override
    public ExtractedFieldsDTO extractFields(String rawText) {
        ExtractedFieldsDTO dto = new ExtractedFieldsDTO();

        if (rawText == null || rawText.isBlank()) {
            log.warn("Raw text empty for extraction");
            return dto;
        }

        // 1) Try parse key:value pairs from text (AcroForm output or synthesized lines)
        Map<String, String> kv = parseKeyValueLines(rawText);

        // Map common keys/aliases to DTO fields
        dto.setPolicyNumber(firstNonNull(
                kv.get("policy number"),
                kv.get("policy_number"),
                kv.get("policynumber"),
                kv.get("policy"),
                kv.get("policy no"),
                kv.get("policy#"),
                kv.get("policynum"),
                findInText(rawText, "POLICY NUMBER[:\\s]*([A-Za-z0-9\\-]+)")
        ));

        dto.setPolicyHolderName(firstNonNull(
                kv.get("name of insured"),
                kv.get("insured"),
                kv.get("insured name"),
                kv.get("policyholder"),
                kv.get("policy holder"),
                kv.get("insured name (first, middle, last)"),
                findInText(rawText, "NAME OF INSURED\\s*[:\\-]*\\s*(.+)")
        ));

        dto.setLocation(firstNonNull(
                kv.get("location of loss"),
                kv.get("location"),
                kv.get("address"),
                findInText(rawText, "LOCATION OF LOSS[:\\s]*(.+)")
        ));

        dto.setDescription(firstNonNull(
                kv.get("description of accident"),
                kv.get("description"),
                kv.get("remarks"),
                findInText(rawText, "DESCRIPTION OF ACCIDENT(?:.*?)([\\s\\S]*?)(?:LOSS|INSURED VEHICLE|Page \\d+ of)")
        ));

        dto.setIncidentDate(firstNonNull(
                kv.get("date of loss"),
                kv.get("loss date"),
                findInText(rawText, DATE_OF_LOSS_PATTERN)
        ));

        dto.setEstimatedDamage(firstNonNull(
                kv.get("estimated damage amount"),
                kv.get("estimate amount"),
                kv.get("estimated amount"),
                kv.get("estimateddamage"),
                kv.get("estimate"),
                findInText(rawText, "(?i)ESTIMATE AMOUNT[:\\s\\$]*([0-9,\\.]+)"),
                findInText(rawText, "(?i)Estimated Damage Amount[:\\s\\$]*([0-9,\\.]+)")
        ));



        dto.setIncidentTime(firstNonNull(
                kv.get("time of loss"),
                kv.get("time"),
                null
        ));

        // claim type: use direct key or simple keyword detection
        String ct = firstNonNull(kv.get("claim type"), kv.get("claimtype"), kv.get("type"));
        if (ct != null) {
            dto.setClaimType(ct);
        } else {
            String lower = rawText.toLowerCase();
            if (lower.contains("injury") || lower.contains("injured")) dto.setClaimType("INJURY");
            else dto.setClaimType("PROPERTY");
        }

        // estimated damage: try kv, then pattern
        String est = firstNonNull(kv.get("estimate amount"), kv.get("estimate"), kv.get("estimated damage"), findInText(rawText, ESTIMATE_AMOUNT_PATTERN));
        dto.setEstimatedDamage(est);

        // VIN fallback
        dto.setPolicyHolderName(nullIfEmpty(dto.getPolicyHolderName())); // normalize

        // If any values still null, attempt additional regex fallbacks
        if (dto.getPolicyNumber() == null) {
            dto.setPolicyNumber(findInText(rawText, "POLICY NUMBER[:\\s]*([A-Za-z0-9\\-]+)"));
        }

        // trim and cleanup
        cleanDto(dto);

        return dto;
    }

    // parse lines like "Field Name: value" from raw text into case-insensitive map
    private Map<String, String> parseKeyValueLines(String text) {
        Map<String, String> map = new HashMap<>();
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            if (line == null) continue;
            String l = line.trim();
            if (l.length() < 3) continue;
            int colon = l.indexOf(':');
            if (colon > 1 && colon < 60) {
                String key = l.substring(0, colon).trim().toLowerCase();
                String value = l.substring(colon + 1).trim();
                if (!value.isEmpty()) {
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    private String findInText(String text, Pattern p) {
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(1).trim();
        }
        return null;
    }

    private String findInText(String text, String regex) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        return findInText(text, p);
    }

    private String firstNonNull(String... vals) {
        if (vals == null) return null;
        for (String v : vals) {
            if (v != null && !v.isBlank()) return v.trim();
        }
        return null;
    }

    private void cleanDto(ExtractedFieldsDTO dto) {
        if (dto.getPolicyNumber() != null) dto.setPolicyNumber(dto.getPolicyNumber().replaceAll("[^A-Za-z0-9\\-]", "").trim());
        if (dto.getEstimatedDamage() != null) dto.setEstimatedDamage(dto.getEstimatedDamage().replaceAll("[^0-9.,]", "").trim());
        if (dto.getLocation() != null) dto.setLocation(dto.getLocation().replaceAll("[\\r\\n]+"," ").trim());
        if (dto.getDescription() != null) dto.setDescription(dto.getDescription().replaceAll("[\\r\\n]+"," ").trim());
    }

    private String nullIfEmpty(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }


}
