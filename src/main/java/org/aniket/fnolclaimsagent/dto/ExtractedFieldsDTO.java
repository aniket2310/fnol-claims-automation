package org.aniket.fnolclaimsagent.dto;

import lombok.Data;

@Data
public class ExtractedFieldsDTO {
    private String policyNumber;
    private String policyHolderName;

    // Incident info
    private String incidentDate;
    private String incidentTime;
    private String location;
    private String description;

    // Claim / asset info
    private String claimType;
    private String estimatedDamage;
}
