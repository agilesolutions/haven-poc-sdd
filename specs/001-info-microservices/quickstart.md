# quickstart.md

This quickstart shows how to run the INFO microservices locally for development.

Prerequisites
- JDK 25
- Gradle
- Docker (for Postgres and Keycloak if using containers)

Run Postgres locally (example):

```bash
docker run --name info-postgres -e POSTGRES_PASSWORD=pass -e POSTGRES_USER=info -e POSTGRES_DB=infodb -p 5432:5432 -d postgres:15
```

Run Keycloak (dev) locally (example Helm or container):

```bash
docker run --name keycloak -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin -p 8080:8080 quay.io/keycloak/keycloak:22.0.0 start-dev
```

Build in the monorepo:

```bash
./gradlew build
```

Start Service B (INFO service):

```bash
# from services/service-b
./gradlew :service-b:bootRun
```

Start Service A (client-facing):

```bash
# from services/service-a
./gradlew :service-a:bootRun
```

Use curl to test (example):

```bash
# obtain token via client credentials from Keycloak
TOKEN=$(curl -s -X POST "http://localhost:8080/realms/master/protocol/openid-connect/token" -d "grant_type=client_credentials&client_id=service-a&client_secret=SECRET" | jq -r .access_token)

curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/info
```

Notes
- This quickstart is for development only. Production uses Docker images, Helm charts, FluxCD, and platform-managed Postgres and secrets.
