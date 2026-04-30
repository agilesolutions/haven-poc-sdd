# HAVEN Architecture Constitution
## Common Ground Initiative – Cloud-Native Microservices Platform

Version: 1.0  
Scope: All services, infrastructure, and deployment artifacts within the HAVEN ecosystem

---

# 1. Purpose

HAVEN defines a standardized, opinionated architecture for building **secure, interoperable, 15-factor compliant microservices** on Kubernetes.

The constitution ensures:

- Architectural consistency across services
- Security-by-default design
- GitOps-driven delivery
- Infrastructure reproducibility
- Operational excellence at scale

All systems within HAVEN MUST comply with this constitution.

---

# 2. Technology Baseline

All services and platform components MUST use:

- Java 25
- Spring Boot 4.x
- Kubernetes (production runtime)
- Gradle (Groovy DSL preferred)
- PostgreSQL (default persistence layer)

No alternative runtime stacks are permitted unless explicitly approved.

---

# 3. Platform Runtime Model

All workloads MUST:

- Run as containerized workloads (OCI-compliant images)
- Execute in Kubernetes only
- Be stateless by default
- Expose ports explicitly via container definitions

Forbidden:
- Bare-metal deployments
- VM-only deployments
- Non-containerized execution

---

# 4. GitOps Delivery Model

All deployment operations MUST follow GitOps principles:

- FluxCD is the only allowed reconciliation engine
- Git is the single source of truth for cluster state
- All changes MUST be declarative and version-controlled

Prohibited:
- Manual kubectl changes in production
- Direct cluster mutation outside Git reconciliation
- Drift from declared state

---

# 5. Infrastructure as Code

All infrastructure MUST be provisioned using Terraform.

This includes:

- Kubernetes clusters
- Networking components
- DNS configuration
- Identity and access integration
- Ingress and certificate systems

Rules:
- No manual cloud console modifications
- All infrastructure changes MUST be reviewed via Git

---

# 6. Identity and Security Model

All authentication and authorization MUST use:

- Keycloak (OIDC provider)
- OAuth2 / OpenID Connect standards
- JWT-based access tokens

### Requirements:

- Service-to-service authentication MUST use Client Credentials Flow
- User authentication MUST use Authorization Code Flow
- All services MUST validate JWT tokens
- All tokens MUST be validated for:
    - issuer
    - audience
    - expiration
    - scopes/roles

### Secrets Management:

- Secrets MUST NOT be stored in source code
- Secrets MUST NOT be committed to Git
- Secrets MUST be injected at runtime via secure mechanisms

---

# 7. Networking and Ingress

All external traffic MUST be routed through:

- NGINX Ingress Controller

TLS Requirements:

- TLS MUST be enforced for all external endpoints
- Certificates MUST be issued via cert-manager
- Let’s Encrypt MUST be used as certificate authority

Rules:

- No direct service exposure via NodePort or LoadBalancer unless explicitly justified
- HTTP traffic MUST redirect to HTTPS
- Internal service communication MUST follow least-privilege access rules

---

# 8. Service Design Principles

Each microservice MUST:

- Be independently deployable
- Be independently scalable
- Be independently testable
- Own its own runtime lifecycle

### Forbidden:

- Shared business logic libraries between services
- Tight runtime coupling between services
- Cross-service synchronous dependency chains without resilience patterns

---

# 9. Data Ownership Model

Each service MUST own its data store.

Rules:

- One service = one database ownership boundary
- PostgreSQL is the default datastore
- Flyway MUST be used for schema migrations

### Forbidden:

- Cross-service database access
- Shared writable schemas between services
- Direct table access across service boundaries

---

# 10. API Design Standards

All service interfaces MUST be:

- RESTful (HTTP/JSON only)
- Versioned (`/api/v1/...`)
- Documented using OpenAPI 3.1

Requirements:

- Explicit request/response schemas
- Backward compatibility for minor versions
- Standardized error model

---

# 11. 15-Factor Compliance

All services MUST comply with the 15-factor cloud-native principles:

- Externalized configuration
- Stateless execution
- Logs as event streams
- Strict dependency declaration
- Disposability
- Concurrency-aware design
- Dev/prod parity
- Observability-first design

Non-compliant services MUST be rejected.

---

# 12. Configuration Management

All configuration MUST be externalized.

Allowed sources:

- Environment variables
- Kubernetes ConfigMaps
- Kubernetes Secrets (encrypted at rest)

Forbidden:

- Hardcoded configuration
- Environment-specific branching logic in code
- Static configuration files for environment variation

---

# 13. Observability Standards

All services MUST expose:

- `/actuator/health`
- `/actuator/info`
- `/actuator/metrics`

Requirements:

- Structured JSON logging to stdout
- Correlation ID propagation across services
- Distributed tracing support (OpenTelemetry-compatible)
- Prometheus-compatible metrics

---

# 14. Resilience Requirements

All inter-service communication MUST implement:

- Request timeouts (mandatory)
- Retry policies (bounded)
- Circuit breakers where appropriate

Rules:

- No unbounded synchronous calls
- No blocking dependency chains without fallback strategies

---

# 15. Build and Deployment Model

All builds MUST produce:

- Immutable artifacts
- Versioned container images

Deployment MUST:

- Be automated via CI/CD pipelines
- Be reconciled via FluxCD
- Be fully declarative

Forbidden:

- Manual production deployments
- Mutable runtime artifacts

---

# 16. Certificate and Trust Management

All TLS certificates MUST:

- Be issued via cert-manager
- Use Let’s Encrypt as the certificate authority
- Be automatically rotated

Manual certificate handling is prohibited.

---

# 17. Network Security

Default posture:

- Deny all inter-service traffic
- Explicit allow rules only via Kubernetes NetworkPolicies

Principle:

- Least privilege networking at all layers

---

# 18. Compliance and Enforcement

All systems MUST explicitly declare compliance with this constitution.

Violations include:

- architectural drift
- security bypasses
- non-GitOps changes
- shared data violations

Enforcement priority:

1. Security
2. Data integrity
3. GitOps consistency
4. Observability
5. Operational simplicity

---

# 19. Evolution of Constitution

This constitution is:

- Versioned
- Reviewable
- Evolvable through formal architecture review

No informal deviation is permitted.