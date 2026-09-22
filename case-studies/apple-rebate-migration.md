# Apple Compact Rebate Tool — Monolith to Microservices

**Role:** Software Engineer · Collabera · 2018–2019
**Domain:** Enterprise Systems Modernization

## Context

Apple's Compact Rebate Tool was running as a single Java monolith, limiting release velocity and scalability. The goal was to decompose it into independently deployable services without disrupting a live business-critical tool.

## What I Did

- Migrated the monolith into **12 Spring Boot microservices** using a strangler-fig pattern, incrementally routing functionality out of the legacy system.
- Executed a shadow-mode cutover, running the new services alongside the legacy system for validation before full release.
- Improved data access patterns through composite indexing and targeted query rewrites.

## Outcome

- **Zero incidents** across an 8-month shadow-mode and cutover period.
- Reduced **p95 latency by 25%** through query and indexing optimizations.

## Skills Demonstrated

`Microservices migration` · `Strangler-fig pattern` · `Spring Boot` · `Database performance tuning` · `Risk-managed cutovers`
