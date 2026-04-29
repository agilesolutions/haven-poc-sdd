# Implementation Plan: INFO Microservices

**Branch**: `003-info-microservices` | **Date**: 2026-04-29 | **Spec**: `specs/001-info-microservices/spec.md`
**Input**: Feature specification from `/specs/001-info-microservices/spec.md`

## Summary

Implement two Spring Boot microservices (Service A and Service B) in a monorepo. Service B is the source-of-truth for a single INFO entity persisted in PostgreSQL. Service A exposes a client-facing GET /info endpoint and forwards authenticated requests to Service B using OIDC client-credentials. Deliverables include service scaffolding (Gradle, Spring Boot 4, JDK 25), container images, Helm charts, GitLab CI for build/publish, FluxCD HelmReleases for deployment (including Keycloak HelmRelease), and observability via Micrometer -> OpenTelemetry Collector -> LGTM stack.

## Technical Context

**Language/Version**: Java 25 (JDK 25), Spring Boot 4  
**Build System**: Gradle (multi-project build)  
**Primary Dependencies**: Spring Web, Spring Security (OAuth2 Resource Server & Client), Spring Data JPA, PostgreSQL driver, Micrometer OpenTelemetry, OpenTelemetry SDK (if needed), Flyway for DB migrations, Lombok (optional for DTOs), Testcontainers for integration tests.  
**Storage**: PostgreSQL (managed or in-cluster).  
**Testing**: JUnit 5, MockMVC for controller tests, Testcontainers for integration tests against Postgres and Keycloak emulation.  
**Target Platform**: Kubernetes clusters managed with FluxCD (GitOps). Images built in CI and stored in registry (GitLab container registry recommended).  
**Project Type**: Microservices (monorepo containing two subprojects: service-a, service-b)  
**Performance Goals**: 95% of requests served under 1s p95 in typical staging load; service-to-service call latency kept low with client-side timeout of 2s and circuit-breaker.  
**Constraints**: OIDC provider will be Keycloak deployed via FluxCD HelmRelease; services must accept/validate JWT access tokens; secrets stored in cluster secret store; Helm charts required for FluxCD; telemetry exported to LGTM stack via OTel Collector.  
**Scale/Scope**: Initial scope is a single INFO record and two services for v1.

## Constitution Check

The implementation will comply with the repository constitution (Kubernetes-native deployments, health/readiness endpoints, metrics, observability, CI/CD, resource requests, security). No violations identified.

## Project Structure

Recommended monorepo layout (Gradle multi-project):

```
/ (repo root)
├─ build.gradle.kts           # root Gradle configuration, plugin management
├─ settings.gradle.kts
├─ gradle/                   # wrapper, scripts
├─ services/
│  ├─ service-a/             # Spring Boot app: client-facing API
│  │  ├─ build.gradle.kts
│  │  └─ src/
│  └─ service-b/             # Spring Boot app: INFO service + DB
│     ├─ build.gradle.kts
│     └─ src/
├─ charts/                   # Helm charts for both services + shared libs
│  ├─ service-a/
│  ├─ service-b/
│  └─ keycloak/              # optionally vendor chart overrides
├─ flux/                     # FluxCD HelmRelease manifests referencing charts
└─ specs/001-info-microservices
   ├─ spec.md
   ├─ plan.md
   ├─ data-model.md
   ├─ research.md
   ├─ contracts/
   └─ quickstart.md
```

**Structure Decision**: Use a Gradle multi-project monorepo with isolated subprojects for `service-a` and `service-b`. Charts will be colocated in `/charts` and consumed by FluxCD manifests in `/flux`.

## Interfaces & Contracts

- Service B: REST API (GET /info, PUT /info) — requires bearer token (OIDC). Documented in `specs/.../contracts/service-b-openapi.yaml`.
- Service A: REST API (GET /info) — requires bearer token for client requests; when calling Service B, Service A uses client-credentials to obtain an access token from Keycloak and calls B's GET /info.

## CI/CD

- GitLab CI pipelines (one pipeline per merge to main and per branch):
  - Build: Gradle build, unit tests, static analysis
  - Image build: Build Docker images for service-a and service-b, tag with CI_COMMIT_SHORT_SHA
  - Publish: Push images to GitLab Container Registry
  - Release: Create a chart package (helm package) and push to Helm repo (or keep in same git repo and let FluxCD pull charts via git/OCI)
  - CD: FluxCD watching Git (flux/ directory) will pick up HelmRelease manifests and reconcile the cluster. Keycloak is deployed as a FluxCD-managed HelmRelease.

## Observability and Telemetry

- Instrument both services with Micrometer using the OpenTelemetry exporter.
- Deploy an OpenTelemetry Collector (as part of cluster observability stack) to receive OTLP and export to LGTM stack.
- Configure metrics endpoint (`/actuator/prometheus` or OpenTelemetry metrics) and traces exported via OTel.

## Security

- Keycloak deployed as an OIDC provider via FluxCD HelmRelease.
- Service A configured as an OAuth2 client for the client-credentials grant to request tokens from Keycloak when calling Service B.
- Service B configured as an OAuth2 resource server validating incoming JWT access tokens (issuer, jwks URI) and enforcing scopes/roles where appropriate.
- Secrets (client ID/secret) will be stored in Kubernetes Secrets and referenced by Helm values.

## DB Migrations

- Use Flyway for schema migrations in Service B. Migration scripts placed in `service-b/src/main/resources/db/migration`.
- Initial migration to create `info` table with columns: name (TEXT primary key), description (TEXT), version (TEXT).

## Phase Plan

- Phase 0 (Research): Completed — technology choices provided by user. research.md reflects the rationale and alternatives.
- Phase 1 (Design): Produce `data-model.md`, OpenAPI contracts (in `contracts/`), and `quickstart.md`. Update agent context file.
- Phase 2 (Implementation planning): Produce `tasks.md` and estimate effort per task.

## Risks & Mitigations

- OIDC complexity: Use Keycloak via HelmRelease and standard Spring OAuth2 components; test token flows with Testcontainers-Keycloak or a local Keycloak dev container.
- Observability plumbing: Use community OTel collector Helm chart and test exporting to LGTM in staging.
- Resource constraints: Set conservative CPU/memory requests and tune after metrics observed.

## Deliverables

- Gradle multi-project scaffold for Service A and Service B (Spring Boot 4, JDK 25)
- OpenAPI contracts in `specs/001-info-microservices/contracts/`
- Helm charts for both services and Keycloak references
- GitLab CI pipeline snippets in `ci/` or `.gitlab-ci.yml` modifications
- FluxCD HelmRelease manifests under `flux/`
- Observability: Micrometer + OTel Collector configuration and sample LGTM exporter config

## Next Steps

1. Generate `data-model.md`, `contracts/` OpenAPI files, and `quickstart.md` (Phase 1) — next action.
2. Create `tasks.md` with implementation checklist and estimates (Phase 2).
3. Optionally scaffold the Spring Boot projects and Helm charts (implementation).
