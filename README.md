# haven-poc-sdd

Monorepo for INFO microservices (Service A and Service B).

See `specs/001-info-microservices/quickstart.md` for local development instructions.

## Observability

Both services emit Prometheus metrics, structured JSON logs, and distributed traces via Micrometer and OpenTelemetry. To connect to the OTel Collector and LGTM:

- Metrics: Exposed at `/actuator/prometheus` (if enabled)
- Traces: Exported to the OTel Collector (see `charts/otel-collector/` and `flux/otel-collector-helmrelease.yaml`)
- Logs: Structured JSON by default (see `application.yml`)

See `documentation/deployment.md` for more details.
