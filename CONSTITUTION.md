# haven-poc-sdd Constitution

## Core Principles

### I. Microservice-First
All services are designed as independently deployable, single-responsibility microservices. Each service owns its data and API contract. Services must be independently testable and deployable without coordinating releases across unrelated teams.

### II. Kubernetes-Native Deployments
Production deployments target Kubernetes. Services must provide container images, a Helm (or equivalent) chart/manifest, and Kubernetes-ready health probes (liveness/readiness). Deployments should follow immutable image principles.

### III. Twelve-Factor Configuration
Follow twelve-factor app principles for config: store config in the environment, do not bake runtime configuration into images, and support externalized secrets via the cluster secret mechanism or a managed secret store.

### IV. Observability (Logging, Metrics, Tracing)
Structured JSON logging at INFO/WARN/ERROR levels, metrics exposed via an HTTP metrics endpoint, and distributed tracing instrumentation. Logs, metrics, and traces must be configurable and exportable to cluster observability tooling.

### V. Health, Readiness & Graceful Shutdown
Services must implement health, readiness, and shutdown hooks. Readiness must reflect the ability to serve production traffic; liveness must detect unrecoverable failures. Services must handle SIGTERM gracefully and complete in-flight requests before exit.

### VI. Security & Least Privilege
Communication should use TLS in transit where practical. Services run with least-privilege Kubernetes service accounts and minimal container permissions. Secrets must not be checked into source control.

### VII. Resilience & Fault Tolerance
Design for failure: idempotent operations, retries with exponential backoff where appropriate, timeouts, and bulkhead/circuit-breaker patterns for external dependencies. Avoid cascading failures.

### VIII. CI/CD & Immutable Artifacts
Every change must produce a versioned, immutable container image. CI pipelines build, test, and publish images; CD pipelines promote artifacts through environments and apply Kubernetes manifests via automation.

### IX. Versioning & Compatibility
APIs must be versioned. Backward-compatible changes preferred; breaking changes require coordination, a migration plan, and updated contracts.

### X. Resource Management & Cost Awareness
Every service must declare CPU/memory requests and limits appropriate to expected load. Teams should monitor resource usage and optimize for cost-effective operation.

## Additional Constraints

- Avoid stateful single-node assumptions; prefer external managed services for durable state (databases, queues) unless explicitly justified.
- Image sizes should be kept minimal; use multi-stage builds and small base images where practical.
- Do not depend on cluster-wide privileges; request the minimal RBAC necessary.

## Development Workflow

- Use local development profiles that simulate production config (e.g., via remote services or emulators) while enabling fast iteration.
- Write unit and integration tests; include a small contract test for any public API.
- PRs must include a summary of deployment implications (new manifests, config, secrets) and a basic rollout plan.
- CI must run tests, static analysis, and a basic container scan before merging.

## Quality Gates

- All services must have health/readiness endpoints and expose metrics.
- PRs must have passing CI and at least one approving reviewer in the owning team.
- Security scans must pass (no high severity findings) before deployment to production.

## Governance
- Constitution changes must be documented and ratified by the platform or architecture owners.
- Major amendments require a migration/rollback plan and communication to affected teams.

**Version**: 1.0 | **Ratified**: 2026-04-29 | **Last Amended**: 2026-04-29
