package org.aniket.fnolclaimsagent.service;

import org.aniket.fnolclaimsagent.dto.ExtractedFieldsDTO;

public interface FieldExtractionService {

    ExtractedFieldsDTO extractFields(String rawText);


}
