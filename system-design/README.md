# System Design

Architecture write-ups for the systems referenced in [case-studies/](../case-studies/). These go one level deeper than the case studies — component breakdowns, diagrams, and the trade-offs behind key decisions — for anyone (interviewer, hiring manager, curious engineer) who wants to see how I reason about design, not just the outcomes.

| Design Doc | System | Focus |
|---|---|---|
| [Device Attestation Architecture](./device-attestation-architecture.md) | Attestation & Monitoring Service | Cryptographic attestation at fleet scale, tamper detection |
| [Multi-Bank Integration Layer](./multi-bank-integration-layer.md) | NPCI Dispute Platform | High-availability integration across heterogeneous partners |
| [REST to Event-Driven Migration](./rest-to-event-driven-migration.md) | Core Platform | Zero-downtime architectural migration across regions |

Each doc follows the same shape: **Problem → Constraints → Architecture → Key Decisions & Trade-offs → What I'd Do Differently**.
