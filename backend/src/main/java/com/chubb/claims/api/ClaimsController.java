package com.chubb.claims.api;

import com.chubb.claims.api.ClaimsDto.*;
import com.chubb.claims.service.ClaimsService;
import jakarta.validation.Valid;

import java.security.Principal;
import java.util.*;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ClaimsController {
    private final ClaimsService service;

    public ClaimsController(ClaimsService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication user) {
        return Map.of("username", user.getName(), "role", isStaff(user) ? "STAFF" : "CLAIMANT");
    }

    @PostMapping("/claims")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CLAIMANT')")
    public ClaimView create(Principal p, @Valid @RequestBody CreateClaim input) {
        return service.create(p.getName(), input);
    }

    @GetMapping("/claims")
    public List<ClaimView> list(Authentication p) {
        return isStaff(p) ? service.all() : service.mine(p.getName());
    }

    @GetMapping("/claims/{id}")
    public ClaimView one(@PathVariable UUID id, Authentication p) {
        return service.one(id, p.getName(), isStaff(p));
    }

    @PostMapping("/claims/{id}/assign")
    @PreAuthorize("hasRole('STAFF')")
    public ClaimView assign(@PathVariable UUID id, Principal p) {
        return service.assign(id, p.getName());
    }

    @PostMapping("/claims/{id}/request-info")
    @PreAuthorize("hasRole('STAFF')")
    public ClaimView requestInfo(@PathVariable UUID id, Principal p, @Valid @RequestBody TextCommand input) {
        return service.requestInfo(id, p.getName(), input);
    }

    @PostMapping("/claims/{id}/provide-info")
    @PreAuthorize("hasRole('CLAIMANT')")
    public ClaimView supplyInfo(@PathVariable UUID id, Principal p, @Valid @RequestBody TextCommand input) {
        return service.supplyInfo(id, p.getName(), input);
    }

    @PostMapping("/claims/{id}/decision")
    @PreAuthorize("hasRole('STAFF')")
    public ClaimView decide(@PathVariable UUID id, Principal p, @Valid @RequestBody DecisionCommand input) {
        return service.decide(id, p.getName(), input);
    }

    @PostMapping("/claims/{id}/settle")
    @PreAuthorize("hasRole('STAFF')")
    public ClaimView settle(@PathVariable UUID id, Principal p) {
        return service.settle(id, p.getName());
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('STAFF')")
    public Dashboard dashboard() {
        return service.dashboard();
    }

    private boolean isStaff(Authentication p) {
        return p.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));
    }
}
