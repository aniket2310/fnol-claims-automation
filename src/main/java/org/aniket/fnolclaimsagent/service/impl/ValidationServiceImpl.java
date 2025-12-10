package org.aniket.fnolclaimsagent.service.impl;

import org.aniket.fnolclaimsagent.dto.ExtractedFieldsDTO;
import org.aniket.fnolclaimsagent.service.ValidationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationServiceImpl implements ValidationService {
    @Override
    public List<String> findMissingFields(ExtractedFieldsDTO f) {

        List<String> missing = new ArrayList<>();


        if (isEmpty(f.getPolicyNumber()))
            missing.add("policyNumber");

        if (isEmpty(f.getPolicyHolderName()))
            missing.add("policyHolderName");

        if (isEmpty(f.getIncidentDate()))
            missing.add("incidentDate");

        if (isEmpty(f.getLocation()))
            missing.add("location");

        if (isEmpty(f.getDescription()))
            missing.add("description");

        if (isEmpty(f.getClaimType()))
            missing.add("claimType");

        if (isEmpty(f.getEstimatedDamage()))
            missing.add("estimatedDamage");

        return missing;
        }
    private boolean isEmpty(String s) {
        return (s == null || s.trim().isEmpty());
    }
}
