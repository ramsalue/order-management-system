#!/bin/bash

echo "========================================="
echo "Testing Production Profile Validation"
echo "========================================="

# Clean build
./mvnw clean package -DskipTests -q

echo ""
echo "Test 1: No environment variables set"
echo "-------------------------------------"

# Unset all
unset DB_HOST DB_PORT DB_NAME DB_USERNAME DB_PASSWORD

# Rename .env files
mv .env .env.disabled 2>/dev/null
mv .env.production .env.production.disabled 2>/dev/null
mv .env.production.local .env.production.local.disabled 2>/dev/null

# Try to run - use -D instead of --
java -Dspring.profiles.active=prod -jar target/order-management-system-0.0.1-SNAPSHOT.jar 2>&1 | head -30 &
PID=$!

sleep 8

if ps -p $PID > /dev/null 2>&1; then
    echo "ERROR: Application started (it shouldn't have!)"
    kill $PID

    # Restore
    mv .env.disabled .env 2>/dev/null
    mv .env.production.disabled .env.production 2>/dev/null
    mv .env.production.local.disabled .env.production.local 2>/dev/null

    exit 1
else
    echo "SUCCESS: Application failed to start as expected"
fi

# Restore
mv .env.disabled .env 2>/dev/null
mv .env.production.disabled .env.production 2>/dev/null
mv .env.production.local.disabled .env.production.local 2>/dev/null

echo ""
echo "========================================="
echo "All tests passed!"
echo "========================================="