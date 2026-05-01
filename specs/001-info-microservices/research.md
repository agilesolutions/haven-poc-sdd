# research.md

## Decision: Frameworks and Platform
- Chosen: Spring Boot 4 on JDK 25 with Gradle multi-project.
- Rationale: Spring Boot 4 supports the latest Spring ecosystem with improved native support, JDK 25 provides the Java features required by modern libraries, and Gradle is a common choice for multi-module Java builds with good CI integration.
- Alternatives considered: Micronaut (faster startup), Quarkus (Graal-native focus). Rejected because the team requested Spring Boot specifically.

## Decision: OIDC Provider
- Chosen: Keycloak deployed via FluxCD HelmRelease.
- Rationale: Keycloak supports OIDC and can be deployed via Helm; FluxCD HelmReleases can manage lifecycle. Keycloak also supports client-credentials flows and has well-understood integration with Spring Security.
- Alternatives: Dex, Auth0 (managed). Rejected due to local manageability and the user's request.

## Decision: CI/CD
- Chosen: GitLab CI for build/publish; FluxCD for GitOps-based deployment.
- Rationale: GitLab CI integrates with GitLab Container Registry; FluxCD supports HelmRelease and GitOps flows.

## Decision: Observability
- Chosen: Micrometer with OpenTelemetry exporter and an OpenTelemetry Collector to export to LGTM stack.
- Rationale: Micrometer is native to Spring Boot, OTel provides a vendor-neutral telemetry path, and LGTM is the target exporter.

## Security and Secrets
- Secrets to be stored in Kubernetes Secrets. Client credentials for Keycloak are managed as K8s Secrets referenced by Helm values.

## Conclusion
All clarifications resolved based on the user's requested stack. Proceed to design artifacts (data model, contracts, quickstart).
