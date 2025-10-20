#!/bin/bash

# ========================================
# MELI Order Management System
# Production Deployment Script
# ========================================

echo "========================================
"
echo "MELI Production Deployment"
echo "========================================
"

# Check if .env.production exists
if [ ! -f .env.production ]; then
    echo "ERROR: .env.production file not found!"
    echo "Copy .env.production.template and fill with real values"
    exit 1
fi

# Load environment variables
export $(cat .env.production | grep -v '^#' | xargs)

# Verify required variables
if [ -z "$DB_HOST" ] || [ -z "$DB_USERNAME" ] || [ -z "$DB_PASSWORD" ]; then
    echo "ERROR: Required environment variables not set!"
    echo "Please check .env.production file"
    exit 1
fi

echo "Building application..."
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "ERROR: Build failed!"
    exit 1
fi

echo "Starting application with production profile..."
java -jar target/order-management-system-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  > /var/log/meli/application-startup.log 2>&1 &

# Get PID
PID=$!
echo "Application started with PID: $PID"

# Wait for application to start
sleep 10

# Check if application is running
if ps -p $PID > /dev/null; then
    echo "========================================
"
    echo "Application is running successfully!"
    echo "PID: $PID"
    echo "Check logs at: /var/log/meli/"
    echo "========================================
"
else
    echo "ERROR: Application failed to start!"
    echo "Check logs at: /var/log/meli/application-startup.log"
    exit 1
fi