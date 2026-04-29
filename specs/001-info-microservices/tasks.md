# Tasks: INFO Microservices

**Input**: Design documents from `specs/001-info-microservices/` (spec.md, plan.md, data-model.md, contracts/)
**Prerequisites**: `plan.md`, `spec.md`

---

## Phase 1: Setup (Project initialization)

- [x] T001 Create root Gradle build files in `build.gradle.kts` and `settings.gradle.kts`
- [x] T002 \[P\] Create Gradle multi-project skeleton directories: `services/service-a/` and `services/service-b/` with placeholder `build.gradle.kts` files
- [x] T003 \[P\] Create a minimal `services/service-a/src/main/java/...` and `services/service-b/src/main/java/...` directory structure and `src/test` folders for each service
- [x] T004 \[P\] Add Dockerfiles for each service: `services/service-a/Dockerfile` and `services/service-b/Dockerfile`
- [x] T005 \[P\] Add Helm chart scaffolds: `charts/service-a/Chart.yaml`, `charts/service-a/values.yaml`, `charts/service-b/Chart.yaml`, `charts/service-b/values.yaml`, and `charts/keycloak/` overlay
- [x] T006 \[P\] Create Flux directory and placeholder HelmRelease manifests: `flux/keycloak-helmrelease.yaml`, `flux/service-a-helmrelease.yaml`, `flux/service-b-helmrelease.yaml`
- [x] T007 Create a repository-level `.gitlab-ci.yml` with jobs for build, image, and publish (stages: build, test, image, publish)
- [x] T008 Create `README.md` at repo root documenting the monorepo layout and quickstart pointers (`specs/001-info-microservices/quickstart.md`)

---

## Phase 2: Foundational (Blocking prerequisites)

**Purpose**: Core infrastructure required before implementing user stories (must complete before Phase 3)

- [x] T009 Setup Flyway migrations directory for Service B: `services/service-b/src/main/resources/db/migration/V1__create_info_table.sql`
- [x] T010 \[P\] Add PostgreSQL datasource configuration for Service B: `services/service-b/src/main/resources/application.yml`
- [x] T011 \[P\] Add Spring Boot Actuator and configure health/readiness endpoints for both services in `services/service-a/build.gradle.kts`, `services/service-b/build.gradle.kts` and `services/*/src/main/resources/application.yml`
- [x] T012 \[P\] Configure structured JSON logging for both services in `services/*/src/main/resources/logback-spring.xml` or `application.yml`
- [x] T013 \[P\] Implement Micrometer OpenTelemetry exporter configuration in both services: `services/service-a/src/main/resources/application.yml` and `services/service-b/src/main/resources/application.yml`
- [x] T014 \[P\] Add OpenTelemetry Collector Helm chart/config at `charts/otel-collector/` and FluxCD HelmRelease `flux/otel-collector-helmrelease.yaml` to receive OTLP and export to LGTM
- [x] T015 Implement Spring Security base configs:
  - [x] T015a \[P\] Create resource-server JWT validation config for Service B in `services/service-b/src/main/java/com/example/serviceb/config/SecurityConfig.java`
  - [x] T015b \[P\] Create OAuth2 client-credentials config for Service A in `services/service-a/src/main/java/com/example/servicea/config/OAuth2ClientConfig.java` and values in `charts/service-a/values.yaml`
- [x] T016 \[P\] Create Keycloak HelmRelease manifest `flux/keycloak-helmrelease.yaml` and chart overrides in `charts/keycloak/` to deploy Keycloak as OIDC provider
- [x] T017 \[P\] Add Kubernetes Secrets/values templating in `charts/service-a/values.yaml` and `charts/service-b/values.yaml` to consume client ID/secret and JWKS/issuer configs (do not store secrets in repo)
- [x] T018 Configure CI to build and publish images and to push Helm chart artifacts (update `.gitlab-ci.yml`) at `.gitlab-ci.yml`
- [ ] T019 \[P\] Add DB migration verification step to CI for Service B (run Flyway validate/migrate in pipeline) in `.gitlab-ci.yml`

**Checkpoint**: After these tasks complete, user stories implementation can begin.

---

## Phase 3: User Story 1 - Retrieve INFO via Service A (Priority: P1) 🎯 MVP

Goal: Implement a client-facing `GET /info` on Service A that returns the INFO payload from Service B using OIDC client-credentials.

Independent Test: Deploy both services locally (or in a test cluster), register a client in Keycloak, obtain client credentials, call Service A `GET /info` and verify returned JSON matches the INFO stored in Service B.

### Implementation

- [x] T020 \[US1\] Create DTO for INFO in `services/service-a/src/main/java/com/example/servicea/dto/InfoDto.java`
- [x] T021 \[US1\] Implement `InfoController` in `services/service-a/src/main/java/com/example/servicea/controller/InfoController.java` exposing `GET /info`
- [x] T022 \[P\] \[US1\] Implement `InfoClient` that calls Service B's `GET /info` using Spring WebClient and OAuth2 client credentials (class: `services/service-a/src/main/java/com/example/servicea/client/InfoClient.java`)
- [x] T023 \[US1\] Wire OAuth2 client registration and token acquisition (or use Spring's `OAuth2AuthorizedClientManager`) for Service A in `services/service-a/src/main/resources/application.yml` and `services/service-a/src/main/java/.../OAuth2ClientConfig.java`
- [x] T024 \[US1\] Add error handling for upstream failures (map 404/5xx to appropriate responses) in `services/service-a/src/main/java/com/example/servicea/controller/InfoController.java`
- [ ] T025 \[US1\] Add integration test (mock Service B) for Service A `GET /info` in `services/service-a/src/test/java/com/example/servicea/InfoControllerIT.java`
- [ ] T026 \[US1\] Add an end-to-end smoke test that runs Service B (Testcontainer or local) and Service A and calls `GET /info` in `tests/integration/service-a-to-b-smoke.sh`

Checkpoint: Service A `GET /info` should be runnable independently and return data from Service B when Keycloak and Service B are available.

---

## Phase 4: User Story 2 - Manage INFO in Service B (Priority: P2)

Goal: Implement Service B datasource, entity, and endpoints to persist and serve INFO.

Independent Test: Use Service B API to create/update INFO and verify retrieval through Service B and Service A.

### Implementation

- [x] T027 \[US2\] Create JPA entity `InfoEntity` in `services/service-b/src/main/java/com/example/serviceb/model/InfoEntity.java` (fields: name, description, version, updatedAt)
- [x] T028 \[US2\] Create Spring Data JPA repository `InfoRepository` in `services/service-b/src/main/java/com/example/serviceb/repository/InfoRepository.java`
- [x] T029 \[US2\] Implement `InfoService` business service in `services/service-b/src/main/java/com/example/serviceb/service/InfoService.java` to handle persistence and validation
- [x] T030 \[US2\] Implement `InfoController` REST endpoints in `services/service-b/src/main/java/com/example/serviceb/controller/InfoController.java` for `GET /info` and `PUT /info`
- [x] T031 \[US2\] Add Flyway migration file `services/service-b/src/main/resources/db/migration/V1__create_info_table.sql` (if not already created by T009)
- [ ] T032 \[US2\] Add integration tests for Service B using Testcontainers Postgres in `services/service-b/src/test/java/com/example/serviceb/InfoControllerIT.java`
- [ ] T033 \[US2\] Add security tests to verify that Service B rejects requests without valid JWT tokens in `services/service-b/src/test/java/.../SecurityIT.java`

Checkpoint: Service B should be functional and independently testable.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Finalize packaging, observability, deployment wiring, and documentation.

- [ ] T034 \[P\] Update Helm charts `charts/*/values.yaml` to include liveness/readiness probes, resource requests/limits, and OIDC values
- [ ] T035 \[P\] Add OpenTelemetry Collector HelmRelease and LGTM exporter config in `charts/otel-collector/` and `flux/otel-collector-helmrelease.yaml`
- [ ] T036 \[P\] Add GitLab CI job to run integration tests and e2e smoke tests before image publish in `.gitlab-ci.yml`
- [ ] T037 Update `docs/deployment.md` with rollout notes, secrets management, and Keycloak client setup steps
- [ ] T038 \[P\] Run a local quickstart validation script `scripts/quickstart-validate.sh` to verify quickstart steps work
- [ ] T039 \[P\] Security hardening: add container image scanning step and remediate high severity findings in CI pipeline
- [ ] T040 \[P\] Add README sections for observability (how to connect to OTel collector and LGTM)

---

## Dependencies & Execution Order

- Phase 1 (Setup): T001..T008 — can begin immediately
- Phase 2 (Foundational): T009..T019 — BLOCKS all user story implementation
- Phase 3 (US1): T020..T026 — depends on Foundational completion
- Phase 4 (US2): T027..T033 — depends on Foundational completion
- Polish: T034..T040 — runs after US1 and US2

## Parallel Execution Examples

- Setup parallel example: run T002, T003, T004, T005 in parallel (different files)
- US1 parallel example: T022 (InfoClient) and T023 (OAuth2 wiring) can be done in parallel by different developers
- US2 parallel example: T027 (entity) and T028 (repository) can be done in parallel

## Dependency Graph (story-level)

- Foundational (T009..T019) -> {US1 (T020..T026), US2 (T027..T033)} -> Polish (T034..T040)

## Independent Test Criteria (per story)

- US1: Deploy Service A + Service B + Keycloak in test environment; obtain client credentials; call `GET /info` on Service A and assert JSON (name, description, version) matches Service B; unauthorized requests are rejected.
- US2: Use Service B API to `PUT /info` then `GET /info` and assert persisted values; verify unauthorized requests are rejected.

## Parallel Opportunities Identified

- Many setup and foundational tasks are parallelizable (marked with \[P\]).
- Service-specific implementation tasks within a story are parallelizable where they touch different files.

## Suggested MVP Scope

- MVP: Complete Foundational phase and US1 (Service A `GET /info` wired to Service B) — allows a consumer to retrieve INFO via Service A and validates OIDC-protected service-to-service call.

## Format Validation

- All tasks follow the required checklist format: `- [ ] T### [P?] [US?] Description with file path`.

---

## Report

- Total tasks: 40
- Tasks per story/phase:
  - Phase 1 (Setup): 8
  - Phase 2 (Foundational): 11 (includes sub-tasks T015a/T015b)
  - US1 (P1): 7
  - US2 (P2): 7
  - Polish: 6
- Parallel opportunities: Many tasks labeled \[P\]; see above sections.
- Independent test criteria: Listed in "Independent Test Criteria (per story)" above.
- Suggested MVP: US1 only (complete Foundational + US1)
