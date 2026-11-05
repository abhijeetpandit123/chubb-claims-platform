package com.chubb.claims.api;

import com.chubb.claims.domain.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class ClaimsDto {
    private ClaimsDto() {
    }

    public record CreateClaim(@NotNull ClaimType type, @NotNull Market market,
                              @NotBlank @Size(max = 2000) String description,
                              @NotNull @DecimalMin("0.01") @Digits(integer = 16, fraction = 2) BigDecimal estimatedAmount) {
    }

    public record TextCommand(@NotBlank @Size(max = 2000) String text) {
    }

    public record DecisionCommand(boolean approved, @NotBlank @Size(max = 2000) String reason) {
    }

    public record ClaimView(UUID id, long version, String claimantUsername, ClaimType type, Market market,
                            String description, BigDecimal estimatedAmount, String currency, ClaimStatus status,
                            String assignedOfficer, String informationRequest, String additionalInformation,
                            String decisionReason, Instant createdAt, Instant updatedAt) {
        public static ClaimView from(Claim c) {
            return new ClaimView(c.getId(), c.getVersion(), c.getClaimantUsername(), c.getType(), c.getMarket(), c.getDescription(), c.getEstimatedAmount(), c.getCurrency(), c.getStatus(), c.getAssignedOfficer(), c.getInformationRequest(), c.getAdditionalInformation(), c.getDecisionReason(), c.getCreatedAt(), c.getUpdatedAt());
        }
    }

    public record Exposure(String market, String currency, BigDecimal amount, long outstandingCount) {
    }

    public record Dashboard(long submitted, long inReview, long awaitingInfo, long approved, long rejected,
                            long settled, java.util.List<Exposure> exposure) {
    }
}
