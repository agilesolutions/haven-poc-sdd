# Info Microservices OIDC

## Overview

Design and implement a cloud-native microservices system using Java 25, Spring Boot 4.x, Kubernetes, GitOps via FluxCD, and Terraform-based infrastructure provisioning, strictly adhering to the HAVEN Architecture Constitution.

## User Scenarios & Testing

1. A client calls Service A’s `/info` endpoint. Service A authenticates and calls Service B’s `/info` endpoint using OIDC client credentials. Service B returns info from its PostgreSQL database.
2. Service B persists and retrieves info entities (name, description, version) in its own database.
3. Unauthorized or improperly scoped requests to either service are rejected.
4. System emits metrics, logs, and traces for all requests and failures.
5. System is resilient to failures (timeouts, retries, circuit breakers).

## Functional Requirements

1. Service A exposes a REST API endpoint `/info` that returns info by calling Service B’s `/info` endpoint.
2. Service B exposes a REST API endpoint `/info` that returns info from its PostgreSQL database.
3. Service B persists info entities with fields: name, description, version.
4. Service A authenticates to Service B using OIDC client credentials (Keycloak).
5. Both services validate JWTs and enforce role/scope-based authorization.
6. Both services expose OpenAPI 3.1-compliant API documentation.
7. Both services are stateless and independently deployable on Kubernetes.
8. Both services are packaged as Helm charts and deployed via FluxCD.
9. Both services use Flyway for DB migrations (Service B).
10. Both services implement timeouts, retries, and circuit breakers for inter-service calls.
11. Both services provide Prometheus metrics, structured JSON logging, and distributed tracing.
12. All infrastructure is provisioned via Terraform; no manual changes allowed.
13. All endpoints are secured; no unsecured internal endpoints.
14. System is observable and supports failure analysis.

## Success Criteria

- 100% of REST API endpoints are protected by OIDC and role/scope-based authorization.
- Service A can successfully retrieve info from Service B via OIDC-protected call.
- Service B persists and retrieves info entities as specified.
- All services are deployed and managed via FluxCD and Helm on Kubernetes.
- All services emit Prometheus metrics, structured logs, and traces.
- All inter-service calls are resilient (timeouts, retries, circuit breakers).
- No unsecured endpoints exist in the system.
- All infrastructure is provisioned via Terraform, with no manual changes.
- OpenAPI 3.1 documentation is available for all APIs.
- System passes failure mode analysis and observability checks.

## Key Entities

- Info (name, description, version)
- JWT token (with roles/scopes)
- Keycloak realm/client
- Helm chart, FluxCD HelmRelease, Terraform module

## Assumptions

- PostgreSQL is the database for Service B, provisioned via Terraform.
- Keycloak is deployed and managed via FluxCD and Helm.
- NGINX Ingress Controller, cert-manager, and Let’s Encrypt are used for ingress and TLS.
- All services follow 15-factor principles and are stateless by default.
- No direct DB access between services; all communication is via REST APIs.
- All observability and resilience requirements are implemented using industry-standard Spring Boot libraries and patterns.

## Dependencies

- Keycloak for OIDC
- PostgreSQL for Service B
- cert-manager and Let’s Encrypt for TLS
- NGINX Ingress Controller for edge routing
- FluxCD for GitOps
- Terraform for infrastructure

## Out of Scope

- Manual infrastructure changes
- Non-Kubernetes deployment models
- Non-OIDC authentication methods

## [NEEDS CLARIFICATION: What roles/scopes are required for Service A to call Service B?]

---

# End of Specification
