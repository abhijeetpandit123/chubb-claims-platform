package com.chubb.claims;

import com.chubb.claims.domain.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ClaimWorkflowTest {
    private Claim claim() {
        return new Claim("claimant1", ClaimType.MOTOR, Market.SG, "Rear-end collision", new BigDecimal("2500.00"));
    }

    @Test
    void happyPath() {
        Claim c = claim();
        c.assignTo("officer1");
        c.requestInformation("officer1", "Upload report");
        c.supplyInformation("claimant1", "Police report #123");
        c.decide("officer1", true, "Approved");
        c.settle("officer1");
        assertEquals(ClaimStatus.SETTLED, c.getStatus());
    }

    @Test
    void cannotSkipReview() {
        assertThrows(IllegalStateException.class, () -> claim().decide("officer1", true, "x"));
    }

    @Test
    void officerOwnershipRequired() {
        Claim c = claim();
        c.assignTo("officer1");
        assertThrows(IllegalStateException.class, () -> c.decide("officer2", false, "x"));
    }

    @Test
    void claimantOwnershipRequired() {
        Claim c = claim();
        c.assignTo("officer1");
        c.requestInformation("officer1", "More info");
        assertThrows(IllegalStateException.class, () -> c.supplyInformation("claimant2", "x"));
    }

    @Test
    void currencyDerivesFromMarket() {
        assertEquals("SGD", claim().getCurrency());
    }
}
