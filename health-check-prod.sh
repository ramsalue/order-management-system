#!/bin/bash

# ========================================
# Production Health Check Script
# ========================================

HEALTH_URL="http://localhost:8080/api/info/health"
MAX_RETRIES=3
RETRY_DELAY=5

echo "Checking application health..."

for i in $(seq 1 $MAX_RETRIES); do
    RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $HEALTH_URL)

    if [ "$RESPONSE" = "200" ]; then
        echo "✓ Application is healthy (HTTP $RESPONSE)"

        # Get detailed info
        curl -s $HEALTH_URL | python -m json.tool
        exit 0
    else
        echo "✗ Health check failed (HTTP $RESPONSE)"
        if [ $i -lt $MAX_RETRIES ]; then
            echo "Retrying in $RETRY_DELAY seconds..."
            sleep $RETRY_DELAY
        fi
    fi
done

echo "ERROR: Application health check failed after $MAX_RETRIES attempts"
exit 1