package com.chubb.claims.infrastructure;

import com.chubb.claims.domain.*;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    List<Claim> findByClaimantUsernameOrderByCreatedAtDesc(String username);

    List<Claim> findAllByOrderByCreatedAtDesc();
}
