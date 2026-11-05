package com.chubb.claims.service;
import java.util.UUID;
public interface ClaimEventPublisher { void claimChanged(UUID claimId,String event,String status); }
