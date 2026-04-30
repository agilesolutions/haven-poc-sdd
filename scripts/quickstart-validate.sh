#!/bin/bash
# Quickstart validation script
set -e

# Build all services
./gradlew build

# Start Postgres (if not running)
docker ps | grep info-postgres || docker run --name info-postgres -e POSTGRES_PASSWORD=pass -e POSTGRES_USER=info -e POSTGRES_DB=infodb -p 5432:5432 -d postgres:15

# Start Keycloak (if not running)
docker ps | grep keycloak || docker run --name keycloak -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin -p 8080:8080 quay.io/keycloak/keycloak:22.0.0 start-dev

# Start Service B
./gradlew :service-b:bootRun &
SERVICE_B_PID=$!
sleep 10

# Start Service A
./gradlew :service-a:bootRun &
SERVICE_A_PID=$!
sleep 10

# Run smoke test
bash tests/integration/service-a-to-b-smoke.sh

# Cleanup
kill $SERVICE_A_PID $SERVICE_B_PID || true
docker stop info-postgres keycloak || true
docker rm info-postgres keycloak || true
