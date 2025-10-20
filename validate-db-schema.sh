#!/bin/bash

# ========================================
# Database Schema Validation Script
# ========================================
# Validates that database schema matches application entities
# Run this before deploying to production
# ========================================

echo "========================================
"
echo "Database Schema Validation"
echo "========================================
"

# Load production environment variables
if [ -f .env.production ]; then
    export $(cat .env.production | grep -v '^#' | xargs)
else
    echo "ERROR: .env.production not found!"
    exit 1
fi

echo "Connecting to database: $DB_NAME on $DB_HOST"

# Check if tables exist
TABLES=$(psql -h $DB_HOST -U $DB_USERNAME -d $DB_NAME -t -c "
SELECT table_name
FROM information_schema.tables
WHERE table_schema='public'
AND table_type='BASE TABLE';
")

echo "Found tables:"
echo "$TABLES"

# Expected tables
EXPECTED_TABLES=("clients" "items" "orders" "order_items")

for table in "${EXPECTED_TABLES[@]}"; do
    if echo "$TABLES" | grep -q "$table"; then
        echo "✓ Table '$table' exists"
    else
        echo "✗ Table '$table' is MISSING!"
        echo "ERROR: Database schema is incomplete"
        exit 1
    fi
done

echo "========================================
"
echo "Database schema validation passed!"
echo "========================================
"