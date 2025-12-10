package org.aniket.fnolclaimsagent.dto;

import lombok.Data;

import java.util.List;

@Data
public class ClaimAnalysisResultDTO {
    private ExtractedFieldsDTO extractedFields;
    private List<String> missingFields;
    private String recommendedRoute;
    private String reasoning;
    private String rawText;

}
