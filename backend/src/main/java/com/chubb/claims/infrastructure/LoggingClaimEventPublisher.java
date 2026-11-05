package com.chubb.claims.infrastructure;

import com.chubb.claims.service.ClaimEventPublisher;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingClaimEventPublisher implements ClaimEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(LoggingClaimEventPublisher.class);

    public void claimChanged(UUID id, String event, String status) {
        log.info("Claim event={} claimId={} status={}", event, id, status);
    }
}
