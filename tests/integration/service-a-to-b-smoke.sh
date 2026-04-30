#!/bin/bash
# End-to-end smoke test: Service A -> Service B
set -e

# Obtain token from Keycloak (assumes Keycloak running locally)
TOKEN=$(curl -s -X POST "http://localhost:8080/realms/master/protocol/openid-connect/token" -d "grant_type=client_credentials&client_id=service-a&client_secret=SECRET" | jq -r .access_token)

# Call Service A /info endpoint
RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/info)
echo "Service A /info response: $RESPONSE"

# Check response fields
if echo "$RESPONSE" | jq -e '.name and .description and .version' > /dev/null; then
  echo "Smoke test PASSED"
  exit 0
else
  echo "Smoke test FAILED"
  exit 1
fi
