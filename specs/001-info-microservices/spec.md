# Feature Specification: INFO Microservices

**Feature Branch**: `001-info-microservices`  
**Created**: 2026-04-29  
**Status**: Draft  
**Input**: User description: "/specify implement 2 microservices exposing REST API endpoints. Service A is exposing an INFO endpoint, which is calling the INFO REST API endpoint on Service B. Service B is storing info on a Postgresql database entity called INFO with attributes name, description and version. Call from Service A to B should be protected with OIDC client credential flow."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Retrieve INFO via Service A (Priority: P1)
As an API consumer, I call Service A's /info endpoint to retrieve the canonical INFO record.

**Why this priority**: This is the primary user journey that delivers value: a single endpoint consumers can call to get the latest INFO data.

**Independent Test**: Call Service A's /info endpoint with valid client credentials; expect JSON response containing name, description, version. This can be tested with an integration test or via a deployed test environment.

**Acceptance Scenarios**:
1. **Given** Service A is running and can reach Service B, **When** a client with valid credentials calls GET /info on Service A, **Then** Service A returns HTTP 200 with the INFO payload sourced from Service B.
2. **Given** invalid client credentials, **When** a client calls GET /info on Service A, **Then** Service A responds with HTTP 401/403 and does not forward the request to Service B.
3. **Given** Service B is unavailable, **When** a valid request reaches Service A, **Then** Service A returns a clear 5xx error indicating upstream failure and does not return stale/incorrect data.

---

### User Story 2 - Manage INFO in Service B (Priority: P2)
As a data owner, I create or update the INFO record in Service B's datastore so that Service A can fetch the current values.

**Why this priority**: Service B is the source of truth for INFO; without this, Service A cannot deliver accurate data.

**Independent Test**: Use Service B's admin or API endpoints to create/update INFO; then verify retrieval via Service B and via Service A.

**Acceptance Scenarios**:
1. **Given** a valid create/update payload for INFO, **When** an authorized actor submits it to Service B, **Then** Service B persists the INFO record and responds with HTTP 200/201.
2. **Given** the INFO record exists, **When** Service A requests INFO from Service B, **Then** Service B returns HTTP 200 with the latest persisted fields.

---

### Edge Cases

- Concurrent updates to INFO: ensure last-write-wins or a defined resolution policy; detect and surface conflict if multi-writer semantics are required.
- Missing INFO record: Service B should return a clear 404 that Service A maps to a meaningful client response (for example, 404 or 204 depending on UX decision).
- Partial data persisted (e.g., null or empty fields): validation should reject invalid payloads at Service B.
- Large payloads for description: enforce a reasonable maximum length and validate at Service B.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Service A MUST expose a REST endpoint GET /info that returns a single INFO payload derived from Service B.
- **FR-002**: Service A MUST call Service B's INFO REST endpoint to retrieve the data; Service A must not have its own independent persistence for INFO (it is a read-through aggregation).
- **FR-003**: Service B MUST expose a REST endpoint (e.g., GET /info and POST/PUT /info) to retrieve and update the INFO entity.
- **FR-004**: Service B MUST persist the INFO entity in a relational datastore with attributes: name (string, non-empty), description (string), version (string, semantic-style recommended).
- **FR-005**: Calls from Service A to Service B MUST be authenticated using OIDC client credentials flow; Service A acts as an OAuth/OIDC client when calling Service B.
- **FR-006**: Service B MUST validate bearer tokens presented by callers and reject unauthorized requests with standard HTTP status codes (401/403).
- **FR-007**: Both services MUST implement health/readiness endpoints and graceful shutdown handling.
- **FR-008**: Both services MUST log structured events (request/response for /info, errors) to allow tracing and debugging; sensitive data MUST be redacted.
- **FR-009**: Both services MUST expose an HTTP metrics endpoint suitable for collection by cluster monitoring.
- **FR-010**: Both services MUST return appropriate and consistent error codes for client and server errors (4xx for client problems, 5xx for server/upstream failures).
- **FR-011**: All external calls (Service A->B and B->database) MUST use timeouts and retries where appropriate; retry policies MUST prevent duplicated side-effects on write operations.

*Notes on FR-005*: OIDC provider details (issuer, token endpoint, client credentials) are part of environment configuration and must be provided by platform/operator.

### Key Entities *(include if feature involves data)*

- **INFO**: The canonical entity with key attributes:
  - name: string (required)
  - description: string (nullable, max length reasonable)
  - version: string (required)

- **AUTH TOKEN**: OIDC access token presented by Service A when calling Service B (validated by Service B).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Authenticated requests to Service A's GET /info return the correct INFO payload (matching Service B) in 99% of successful tests in an integration environment.
- **SC-002**: Unauthorized requests (missing/invalid credentials) to the chain (Service A or direct to Service B) are rejected with the appropriate 4xx status in 100% of tests.
- **SC-003**: Service A obtains INFO from Service B and responds to the client within an acceptable latency (e.g., 95% of requests complete within 1 second) under normal test load.
- **SC-004**: Data persisted in Service B is durable and retrievable: after creating/updating INFO, subsequent reads return the updated values in 100% of tests.
- **SC-005**: Health/readiness endpoints report correct status; deployments can detect and act on unhealthy instances (readiness false leads to no traffic) during rollout tests.

## Assumptions

- An OIDC provider (issuer, token endpoint, client credential registration) is available and configured in the target environment.
- A relational database compatible with Postgres is available to Service B (production-grade, backed-up, and reachable via network).
- Service-to-service network connectivity is available in the deployment environment; DNS or service discovery resolves Service B for Service A.
- A secrets mechanism exists for storing client credentials/TLS certificates (not stored in code or repo).
- A single INFO record is sufficient for the initial scope (no need for multi-row or per-tenant variants).



