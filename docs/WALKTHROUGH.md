# Panel walkthrough playbook (30–60 minutes)

## 0–3 minutes — problem and prioritization
- Two-persona claims experience, backend foundation, controlled lifecycle, basic workload and exposure reporting.
- Explain that this is a sprint-format assessment (2–3h target / 5h cap), not production insurance software.
- State explicit in/out scope and the decision to build strong lifecycle validation over superficial distributed complexity.

## 3–8 minutes — architecture
- Show README diagram and folder structure: API → service → domain → repository, and event publisher port.
- Why modular monolith? Cohesive ACID writes and easy run; no justified microservice seams yet.
- Why REST? Immediate synchronous user commands. Why not Kafka now? Downstream async consumers absent; outbox/retries needed for reliability.
- Discuss domain model, optimistic locking, 6-state lifecycle and authorization.

## 8–15 minutes — live demo
1. Claimant1 submits SG motor claim for 2,500 SGD.
2. Officer1 sees claim in queue and picks it up; counts change.
3. Officer requests police report number; claimant1 supplies it.
4. Officer approves, settles; exposure disappears from outstanding view.
5. Demonstrate claimant2 cannot read someone else's claim and officer2 cannot decide officer1's claim.
6. Demonstrate rejection branch and 409 invalid transition.

## 15–20 minutes — testing and trade-offs
- Run `mvn test` and `npm run build` LIVE if pre-verified. Do not claim success before executing.
- Explain real versus approximate exposure; grouped market + currency is essential.
- Show AI journal: where an AI suggestion was accepted, challenged and explicitly overridden.
- Highlight shortfalls candidly: fakeable demo accounts, in-memory DB, lack of outbox and real event delivery, no audit-grade trace.

## 20–35 minutes — likely Q&A

**Why not microservices?** Wrong cost/benefit for a focused 5-hour solution. Extract when independent scale, teams or transactional boundaries justify it.

**Why doesn't every operation use Kafka?** A claimant needs immediate validation and durable claim ID; REST is fit for interactive transactions. Kafka is better for downstream notifications, analytics and decoupled integrations.

**How do you avoid losing events?** You do not with this logging demo. Production: transactional outbox, CDC/worker, retries, consumer idempotency and DLQ.

**How do you prevent two officers approving one claim?** Assigned-officer domain check + optimistic locking version. Later normalize concurrency conflict to 409 and use explicit ETag/If-Match.

**How do you handle multi-currency exposure?** Return separate currency groups; FX conversion requires a valuation timestamp, rate source, and policy, none provided by the brief.

**Is this SOLID?** SRP layering, DIP on events port, thin interfaces, replaceable implementations, testable state transitions. Don't overclaim patterns.

**Why are you not using OAuth?** Demo-only Basic auth reduces startup overhead; OIDC Authorization Code + PKCE, TLS, role/tenant claims and secure token/session handling would be release blockers for a real environment.

**How did you test it?** Point to actual JUnit and MockMvc tests; provide real command output once executed. Never imply unexecuted test sources are passing.

## 35–45 minutes — what with more time?
1. Make build green, comprehensive integration tests and CI.
2. PostgreSQL + Flyway migrations, audit trail, OIDC, production security boundaries.
3. Transactional outbox + Kafka for communications and reliable downstream integrations.
4. Reserves vs estimates, claims payments, attachments, notifications, SLAs and escalation.
5. Pagination, analytics projections, multi-market tenant policy, accessibility and performance tests.

## Final 5 minutes — ask the panel
- Is the primary driver throughput, auditability, latency, or claimant experience?
- Are six APAC markets legally separate tenants, and are reserves reported in local or standardized currencies?
- What event consumers are actually required, and what guarantees apply to claim decisions?
- What quality/security expectations distinguish a successful prototype from a production launch?
