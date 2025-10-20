# Configuration Guide

## Overview

This guide explains how to configure the MELI Order Management System for different environments.

## Configuration Files

### application.properties
Shared configuration across all profiles.

### application-dev.properties
Development environment configuration.
- Detailed logging
- Local database
- DevTools enabled
- CORS enabled

### application-test.properties
Testing environment configuration.
- Moderate logging
- Test database
- Transaction rollback
- CORS disabled

### application-prod.properties
Production environment configuration.
- Minimal logging
- Production database (via env vars)
- Security hardened
- CORS disabled by default

## Environment Variables

### Required for All Profiles

No environment variables are strictly required for dev and test profiles as they have defaults. Just for production profile is needed an specific command line to run.

### Required for Production Profile

**Database:**
- `DB_HOST` - Database server hostname
- `DB_PORT` - Database port (default: 5432)
- `DB_NAME` - Database name
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password

**Application:**
- `SERVER_PORT` - Application server port (default: 8080)
- `APP_VERSION` - Application version (default: 1.0.0)

**Optional:**
- `MANAGEMENT_PORT` - Actuator management port (default: 9090)
- `LOG_FILE_PATH` - Log file location
- `CORS_ENABLED` - Enable CORS (default: false)
- `CORS_ALLOWED_ORIGINS` - Allowed CORS origins

## Setting Environment Variables

### Windows (Command Prompt)

```cmd
set DB_HOST=localhost
set DB_USERNAME=meli_user
set DB_PASSWORD=my_password
```

### Windows (PowerShell)

```powershell
$env:DB_HOST="localhost"
$env:DB_USERNAME="meli_user"
$env:DB_PASSWORD="my_password"
```

## Profile Activation

### Method 1: Command Line
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Method 2: Environment Variable
```bash
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run
```

### Method 3: Application Properties
```properties
spring.profiles.active=dev
```

### Method 4: JAR Execution
```bash
java -jar app.jar --spring.profiles.active=prod
```

## Configuration Validation

The application validates configuration at startup:

**Validation Checks:**
- Active profile is set and valid
- Required properties are configured
- Database credentials are provided
- Connection pool settings are appropriate
- Security settings are correct for environment

**Validation Output:**

```
========================================
CONFIGURATION VALIDATION
========================================
Active Profile: [dev]
Database URL: localhost:5432
Database Username: meli_user
Database Password: ******** (masked)
Connection Pool - Max Size: 10
Connection Pool - Min Idle: 5
Application Name: MELI Order Management System
Application Version: 1.0.0
Server Port: 8080
========================================
CONFIGURATION VALIDATION PASSED
========================================
```

**If Errors Found:** Application crashes as ConfigurationValidator.java is not reached.

## Troubleshooting

### Issue: Configuration validation fails

**Check:**
- All required environment variables are set
- Variable names are spelled correctly (case-sensitive)
- Values are not empty
- Database is accessible

### Issue: Wrong profile activated

**Check:**
- `SPRING_PROFILES_ACTIVE` environment variable
- Command line arguments
- application.properties default profile
- IDE run configuration

### Issue: Properties not loading

**Check:**
- File name: `application-{profile}.properties`
- File location: `src/main/resources/`
- Property syntax: `key=value` (no spaces around =)
- Profile is activated

### Issue: Environment variables not working

**Check:**
- Variables are exported: `export VAR_NAME=value`
- Variable names match properties: `${VAR_NAME}`
- Application restarted after setting variables
- Terminal/IDE has variables in environment

## Security Best Practices

1. **Never commit:**
   - Production passwords
   - API keys
   - Secret tokens
   - .env files with real credentials

2. **Always use:**
   - Environment variables for sensitive data
   - Strong passwords in production
   - .gitignore for sensitive files
   - Separate credentials per environment

3. **Regular maintenance:**
   - Rotate passwords quarterly
   - Review access logs
   - Update dependencies
   - Audit configuration files

## Monitoring Configuration

### View Active Configuration

```bash
# Get environment info
curl http://localhost:8080/api/info/environment

# Get health status
curl http://localhost:8080/api/info/health

# Get application info
curl http://localhost:8080/api/info
```


---

**Document Version:** 1.0.0  
**Last Updated:** October 19, 2025