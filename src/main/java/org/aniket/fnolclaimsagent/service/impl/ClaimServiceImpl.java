package org.aniket.fnolclaimsagent.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aniket.fnolclaimsagent.dto.ClaimAnalysisResultDTO;
import org.aniket.fnolclaimsagent.dto.ExtractedFieldsDTO;
import org.aniket.fnolclaimsagent.model.Claim;
import org.aniket.fnolclaimsagent.repository.ClaimRepository;
import org.aniket.fnolclaimsagent.service.ClaimService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;

    @Override
    public Claim saveFromAnalysis(ClaimAnalysisResultDTO analysis) {
        ExtractedFieldsDTO f = analysis.getExtractedFields();

        Claim.ClaimBuilder b = Claim.builder()
                .policyNumber(f.getPolicyNumber())
                .policyHolderName(f.getPolicyHolderName())
                .incidentTime(f.getIncidentTime())
                .location(f.getLocation())
                .description(f.getDescription())
                .claimType(f.getClaimType())
                .recommendedRoute(analysis.getRecommendedRoute())
                .reasoning(analysis.getReasoning())
                .rawText(analysis.getRawText() != null ? analysis.getRawText() : null)
                .createdAt(LocalDateTime.now());

        // parse incident date (expecting MM/dd/yyyy or dd/MM/yyyy) — try common formats
        String d = f.getIncidentDate();
        if (d != null && !d.isBlank()) {
            LocalDate parsed = parseDateFlexible(d);
            b.incidentDate(parsed);
        }

        // parse estimated damage
        String est = f.getEstimatedDamage();
        if (est != null && !est.isBlank()) {
            try {
                String cleaned = est.replaceAll("[^0-9.]", "");
                b.estimatedDamage(new BigDecimal(cleaned));
            } catch (Exception ex) {
                log.warn("Could not parse estimated damage '{}'", est);
            }
        }

        Claim claim = b.build();
        return claimRepository.save(claim);
    }

    @Override
    public List<Claim> findAll() {
        return claimRepository.findAll();
    }

    @Override
    public Optional<Claim> findById(Long id) {
        return claimRepository.findById(id);
    }

    private LocalDate parseDateFlexible(String d) {
        for (DateTimeFormatter fmt : new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("M/d/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("d/M/yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
        }) {
            try {
                return LocalDate.parse(d, fmt);
            } catch (Exception ignored) {}
        }
        return null;
    }
}
