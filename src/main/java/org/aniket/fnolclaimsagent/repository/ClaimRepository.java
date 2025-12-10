package org.aniket.fnolclaimsagent.repository;

import org.aniket.fnolclaimsagent.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
}
