package org.aniket.fnolclaimsagent.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_number", length = 100)
    private String policyNumber;

    @Column(name = "policy_holder_name", length = 255)
    private String policyHolderName;

    @Column(name = "incident_date")
    private LocalDate incidentDate;

    @Column(name = "incident_time", length = 50)
    private String incidentTime;

    @Column(columnDefinition = "TEXT")
    private String location;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "claim_type", length = 50)
    private String claimType;

    @Column(name = "estimated_damage", precision = 15, scale = 2)
    private BigDecimal estimatedDamage;

    @Column(name = "recommended_route", length = 100)
    private String recommendedRoute;

    @Column(columnDefinition = "TEXT")
    private String reasoning;

    @Column(name = "raw_text", columnDefinition = "LONGTEXT")
    private String rawText;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
