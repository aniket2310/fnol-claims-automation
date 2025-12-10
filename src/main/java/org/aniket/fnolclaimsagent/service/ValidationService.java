package org.aniket.fnolclaimsagent.service;

import org.aniket.fnolclaimsagent.dto.ExtractedFieldsDTO;

import java.util.List;

public interface ValidationService {
    List<String> findMissingFields(ExtractedFieldsDTO fields);
}
