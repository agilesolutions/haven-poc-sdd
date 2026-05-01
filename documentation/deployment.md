# Deployment Notes

## Rollout
- Use GitLab CI/CD for build, test, and deploy stages.
- Deploy via FluxCD HelmRelease manifests in the flux/ directory.
- Use Helm charts in charts/ for each service.

## Secrets Management
- Store Keycloak client credentials and DB passwords in Kubernetes Secrets.
- Reference secrets in Helm values.yaml for each service.
- Do not commit secrets to the repository.

## Keycloak Client Setup
- Register Service A as a client in Keycloak.
- Assign required roles/scopes for Service A to call Service B.
- Configure client credentials grant for Service A.

## Observability
- Services emit metrics via Micrometer/OpenTelemetry.
- Logs are structured JSON.
- Traces are exported to the OTel Collector and LGTM.
