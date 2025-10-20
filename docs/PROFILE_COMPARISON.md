# Spring Profile Comparison

## Overview

This document compares the three application profiles: Development, Testing, and Production.

## Quick Reference Table

| Feature | Development | Testing | Production |
|---------|-------------|---------|------------|
| Profile Name | dev | test | prod |
| Server Port | 8080 | 8081 | 8080 |
| Database | meli_order_db | meli_order_db_test | meli_order_db_prod |
| DDL Mode | update | create-drop | validate |
| Logging Level | DEBUG | INFO | WARN/ERROR |
| Show SQL | true | false | false |
| Connection Pool | 5 | 3 | 20 |
| DevTools | enabled | disabled | disabled |
| Error Details | full | full | none |
| CORS | enabled | disabled | disabled |
| Actuator Endpoints | all | limited | minimal |
| Log File Size | 10MB | 5MB | 50MB |
| Log Retention | 10 days | 5 days | 30 days |

## Detailed Comparison

### Database Configuration

**Development:**
- Local PostgreSQL instance
- Auto-updates schema (ddl-auto=update)
- Can use hardcoded credentials (with env var fallback)
- Small connection pool (5 connections)
- Sample data allowed

**Testing:**
- Separate test database
- Creates and drops schema (create-drop)
- Isolated from other environments
- Minimal connection pool (3 connections)
- Transaction rollback after tests

**Production:**
- Production database server
- Only validates schema (validate)
- All credentials from environment variables
- Large connection pool (20 connections)
- Real user data only

### Logging Configuration

**Development:**
- DEBUG level for application code
- SQL queries logged with parameters
- Detailed Spring framework logs
- Console and file logging
- Log files: 10MB max, 10 days retention

**Testing:**
- INFO level for most components
- DEBUG level for test-specific logging
- SQL at INFO level (queries only, no params)
- File logging to test directory
- Log files: 5MB max, 5 days retention

**Production:**
- WARN/ERROR level only
- No SQL logging
- Minimal Spring framework logs
- File logging only (no console in production)
- Log files: 50MB max, 30 days retention, 1GB total cap

### Security Configuration

**Testing:**
- Full error details for debugging tests
- Stack traces on parameter
- CORS disabled
- Limited actuator endpoints
- DevTools disabled

**Production:**
- No error details exposed
- No stack traces
- CORS disabled (or properly configured)
- Minimal actuator endpoints
- DevTools disabled
- Secure cookies enabled
- Management port separate

### Performance Configuration

**Development:**
- Small connection pool (quick startup)
- No performance optimizations
- Focus on debugging
- Auto-reload enabled

**Testing:**
- Minimal connection pool (fast tests)
- No caching (test isolation)
- Transaction rollback
- Fast startup/shutdown

**Production:**
- Large connection pool (handle load)
- Batch operations enabled
- Connection leak detection
- Response compression
- Optimized thread pool
- Monitoring enabled

## When to Use Each Profile

### Development Profile (dev)
**Use when:**
- Developing locally
- Debugging issues
- Testing new features
- Learning the system

**Command:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Testing Profile (test)
**Use when:**
- Running automated tests
- Integration testing
- CI/CD pipeline
- Quality assurance

**Command:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

### Production Profile (prod)
**Use when:**
- Deploying to production
- Serving real users
- Production environment
- Performance testing at scale

**Command:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod -Dspring-boot.run.arguments="--spring.datasource.username=meli_user --spring.datasource.password=meli_password_2025"
```

## Profile Activation Priority

Spring Boot uses the following priority (highest to lowest):

1. Command line argument: `--spring.profiles.active=prod`
2. Environment variable: `SPRING_PROFILES_ACTIVE=prod`
3. System property: `-Dspring.profiles.active=prod`
4. application.properties: `spring.profiles.active=prod`

## Environment Variables by Profile

### Development
```bash
DB_USERNAME=meli_user
DB_PASSWORD=meli_password_2025
```

### Testing
```bash
DB_TEST_USERNAME=meli_test_user
DB_TEST_PASSWORD=meli_test_password_2025
```

### Production
```bash
DB_HOST=prod-db-server.example.com
DB_PORT=5432
DB_NAME=meli_order_db_prod
DB_USERNAME=meli_prod_user
DB_PASSWORD=strong-production-password
SERVER_PORT=8080
MANAGEMENT_PORT=9090
LOG_FILE_PATH=/var/log/meli/application.log
```

## Common Issues and Solutions

### Issue: Wrong profile activated
**Solution:**
- Check environment variable: `echo $SPRING_PROFILES_ACTIVE`
- Verify command line argument
- Check application.properties default

### Issue: Database connection fails
**Solution:**
- Verify profile-specific database exists
- Check credentials for that profile
- Ensure database service is running

### Issue: Properties not loading
**Solution:**
- Verify file name: `application-{profile}.properties`
- Check file location: `src/main/resources/`
- Ensure no typos in property names

### Issue: Environment variables not working
**Solution:**
- Use correct syntax: `${VAR_NAME:default}`
- Export variables before running
- Check variable names (case-sensitive)

---

**Document Version:** 1.0.0  
**Last Updated:** October 19, 2025