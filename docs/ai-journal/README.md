# AI working journal — assessment snapshot

> This journal records **available factual interactions** and **design judgments**, not fabricated prompts, elapsed times or tool outputs. Keep appending dated entries during local testing/refinement before submission. The hiring panel asks what was accepted, challenged and overridden.

## Entry 001 — User-provided assessment direction (2026-11-05)
**User direction (paraphrase):** Read the uploaded Chubb APAC take-home PDF and the previously shared repository preparation video; implement a professional Java Spring Boot + Angular application addressing the brief, with SOLID principles, suitable design patterns, comprehensive README and end-to-end SDLC considerations.

**Brief prioritization:** Backend first; Angular required; service/event boundaries are up to implementer; no more than 5 hours in an actual timed assessment; genuine Git history, runnable application, AI working journal, and panel walkthrough are deliverables.

**Accepted:** A claimant/officer role split, domain lifecycle, officer queue, provisional liability summary, meaningful tests, explicit choices and trade-offs, a complete setup guide.

## Entry 002 — Architecture and decomposition
**Prompt/theme:** Model a minimal defensible claims lifecycle for a time-limited backend-first assessment; decide modular monolith versus microservices, REST versus Kafka, and simple event-boundary strategy.

**Accepted:** Modular monolith; Claim aggregate owns transitions; `ClaimsService` handles transactional use cases; JPA repository; REST for UI; `ClaimEventPublisher` port to isolate future asynchronous integrations.

**Challenged:** Adding Kafka simply because the brief mentions it. Kafka without an outbox and consumers can create a misleading appearance of event-driven reliability.

**Override:** Use structured logging adapter only; explicitly document that it does not provide delivery guarantees or asynchronous handling.

## Entry 003 — Exposure, currency and security
**Prompt/theme:** Handle outstanding liability sensibly across six markets while keeping claimant data private and staff actions scoped to an assigned officer.

**Accepted:** Exposure grouped per market and original currency, excluding rejected/settled; basic authentication/authorization; optimistic locking; input validation.

**Challenged:** Summing all claim estimates across different currencies; displaying all claims to claimants; treating demo credentials as suitable for production.

**Override:** Currency-specific reporting; restricted claimant endpoints; clearly documented non-production demo auth.

## Entry 004 — Test and delivery evidence
**Prompt/theme:** Provide executable test sources, Docker Compose, Angular UI, API examples, test plan and panel walkthrough without overstating verification.

**Accepted:** JUnit unit tests, Spring MockMvc integration test sources, Dockerfiles, README acceptance checklist, walkthrough outline.

**Not accepted as a claim:** That tests are already green or Docker successfully starts. Environment had no Maven or Docker binary and no accessible package registry; results require user-side verification.

## Ongoing journal template

```text
Timestamp / commit:
Prompt / question asked:
AI recommendation:
What I accepted:
What I challenged:
What I overrode and why:
Code/tests changed:
Commands executed and actual results:
Open concerns:
```

**Next entries to add in real development:** Actual compile errors and fixes; security/access-control test results; reason for each engineering change; authentic Git commit hashes; follow-up improvements rejected due to the time cap.
