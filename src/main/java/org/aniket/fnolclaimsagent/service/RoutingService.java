package org.aniket.fnolclaimsagent.service;

import org.aniket.fnolclaimsagent.dto.ClaimAnalysisResultDTO;
import org.aniket.fnolclaimsagent.dto.ExtractedFieldsDTO;

import java.util.List;

public interface RoutingService {

    ClaimAnalysisResultDTO routeClaim(ExtractedFieldsDTO fields, List<String> missingFields, String rawText);


}
