package org.aniket.fnolclaimsagent.service;

import org.aniket.fnolclaimsagent.dto.ClaimAnalysisResultDTO;
import org.aniket.fnolclaimsagent.model.Claim;

import java.util.List;
import java.util.Optional;

public interface ClaimService {
    Claim saveFromAnalysis(ClaimAnalysisResultDTO analysis);
    List<Claim> findAll();
    Optional<Claim> findById(Long id);
}
