package com.chubb.claims.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "claims")
public class Claim {
    @Id
    private UUID id;
    @Version
    private long version;
    @Column(nullable = false)
    private String claimantUsername;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ClaimType type;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Market market;
    @Column(nullable = false, length = 2000)
    private String description;
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal estimatedAmount;
    @Column(nullable = false)
    private String currency;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ClaimStatus status;
    private String assignedOfficer;
    @Column(length = 2000)
    private String informationRequest;
    @Column(length = 2000)
    private String additionalInformation;
    @Column(length = 2000)
    private String decisionReason;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    protected Claim() {
    }

    public Claim(String claimantUsername, ClaimType type, Market market, String description, BigDecimal estimatedAmount) {
        this.id = UUID.randomUUID();
        this.claimantUsername = claimantUsername;
        this.type = type;
        this.market = market;
        this.description = description;
        this.estimatedAmount = estimatedAmount;
        this.currency = market.currency();
        this.status = ClaimStatus.SUBMITTED;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    public String getClaimantUsername() {
        return claimantUsername;
    }

    public ClaimType getType() {
        return type;
    }

    public Market getMarket() {
        return market;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getEstimatedAmount() {
        return estimatedAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public String getAssignedOfficer() {
        return assignedOfficer;
    }

    public String getInformationRequest() {
        return informationRequest;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void assignTo(String officer) {
        require(ClaimStatus.SUBMITTED);
        assignedOfficer = officer;
        status = ClaimStatus.IN_REVIEW;
        touch();
    }

    public void requestInformation(String officer, String question) {
        ownOfficer(officer);
        require(ClaimStatus.IN_REVIEW);
        informationRequest = question;
        status = ClaimStatus.INFO_REQUESTED;
        touch();
    }

    public void supplyInformation(String username, String answer) {
        if (!claimantUsername.equals(username)) throw new IllegalStateException("Not the owner");
        require(ClaimStatus.INFO_REQUESTED);
        additionalInformation = answer;
        informationRequest = null;
        status = ClaimStatus.IN_REVIEW;
        touch();
    }

    public void decide(String officer, boolean approved, String reason) {
        ownOfficer(officer);
        require(ClaimStatus.IN_REVIEW);
        decisionReason = reason;
        status = approved ? ClaimStatus.APPROVED : ClaimStatus.REJECTED;
        touch();
    }

    public void settle(String officer) {
        ownOfficer(officer);
        require(ClaimStatus.APPROVED);
        status = ClaimStatus.SETTLED;
        touch();
    }

    private void ownOfficer(String user) {
        if (!user.equals(assignedOfficer)) throw new IllegalStateException("Only assigned officer can act");
    }

    private void require(ClaimStatus state) {
        if (status != state) throw new IllegalStateException("Expected " + state + " but found " + status);
    }

    private void touch() {
        updatedAt = Instant.now();
    }
}
