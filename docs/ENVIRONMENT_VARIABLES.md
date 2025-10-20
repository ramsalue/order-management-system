# Environment Variables Reference

## Overview

This document lists all environment variables used by the MELI Order Management System.

## Required Variables

### Production Only

These variables MUST be set for production profile:

| Variable | Description | Example | Required |
|----------|-------------|---------|----------|
| DB_HOST | Database server hostname | db.example.com | Yes |
| DB_PORT | Database port | 5432 | No (default: 5432) |
| DB_NAME | Database name | meli_order_db_prod | Yes |
| DB_USERNAME | Database username | meli_prod_user | Yes |
| DB_PASSWORD | Database password | StrongPassword123! | Yes |

### Optional Variables

| Variable | Description | Default | Profiles |
|----------|-------------|---------|----------|
| SERVER_PORT | Application server port | 8080 | All |
| MANAGEMENT_PORT | Actuator management port | 9090 | prod |
| LOG_FILE_PATH | Log file location | /var/log/meli/application.log | prod |
| APP_VERSION | Application version | 1.0.0 | All |
| CORS_ENABLED | Enable CORS | false | prod |
| CORS_ALLOWED_ORIGINS | Allowed CORS origins | (empty) | prod |

## Development Profile Variables

Development profile has defaults for all variables, but they can be overridden:

```bash
# Optional overrides for development
export DB_USERNAME=custom_dev_user
export DB_PASSWORD=custom_dev_password
export SERVER_PORT=8081
```

## Testing Profile Variables

Testing profile has separate variables to avoid conflicts:

```bash
# Test database configuration
export DB_TEST_USERNAME=meli_test_user
export DB_TEST_PASSWORD=meli_test_password_2025
```

## Setting Environment Variables

### Windows - Permanent

1. Open System Properties
2. Advanced > Environment Variables
3. Add New System Variable
4. Name: `DB_HOST`, Value: `localhost`

### Windows - Temporary (Command Prompt)

```cmd
set DB_HOST=localhost
set DB_USERNAME=meli_user
set DB_PASSWORD=my_password
```

### Windows - Temporary (PowerShell)

```powershell
$env:DB_HOST="localhost"
$env:DB_USERNAME="meli_user"
$env:DB_PASSWORD="my_password"
```

### Using .env File

Create `.env` file:
```bash
DB_HOST=localhost
DB_USERNAME=meli_user
DB_PASSWORD=my_password
```

Load and run:
```bash
export $(cat .env | xargs) && ./mvnw spring-boot:run
```

## Variable Validation

The application validates all required variables at startup:

**Success:**
```
========================================
CONFIGURATION VALIDATION
========================================
Active Profile: [prod]
Database URL: ****@db.example.com:5432
Database Username: meli_prod_user
Database Password: ******** (masked)
========================================
CONFIGURATION VALIDATION PASSED
========================================
```

**Failure:**
```
========================================
CONFIGURATION ERRORS FOUND: 2
========================================
1. Database host is not set. Set DB_HOST environment variable.
2. Database password is not set. Set DB_PASSWORD environment variable.
========================================

Application failed to start. Fix the configuration errors above.
```

## Security Best Practices

### DO:
- Use strong passwords (16+ characters)
- Use different passwords per environment
- Rotate passwords regularly (quarterly)
- Keep production variables secret
- Use .gitignore for .env files
- Document required variables
- Validate variables at startup

### DON'T:
- Commit passwords to Git
- Use simple passwords
- Share production credentials
- Log sensitive values
- Hardcode credentials in code
- Use same password across environments
- Leave default passwords in production

## Variable Naming Conventions

**Follow these conventions:**
- Use UPPERCASE for variable names
- Use underscores to separate words
- Be descriptive but concise
- Group related variables with prefixes

**Examples:**
- Good: `DB_HOST`, `DB_USERNAME`, `MAIL_SERVER_HOST`
- Bad: `dbhost`, `user`, `mailserver`

## Troubleshooting

### Variable not being used

**Check:**
1. Variable is exported: `echo $DB_HOST`
2. Variable name matches properties file: `${DB_HOST}`
3. Application restarted after setting variable
4. No typos in variable name

### Application fails to start

**Check:**
1. All required variables are set
2. Values are not empty
3. Database is accessible with provided credentials
4. Port is not already in use

### Variable has wrong value

**Check:**
1. No quotes in .env file: Use `DB_HOST=localhost` not `DB_HOST="localhost"`
2. No spaces around `=`: Use `DB_HOST=localhost` not `DB_HOST = localhost`
3. Variable is exported before running application
4. IDE/terminal has the variable in environment

## Testing Variables

```bash
# List all environment variables
env

# Check specific variable
echo $DB_HOST

# Test variable in application
curl http://localhost:8080/api/info/environment
```

---

**Document Version:** 1.0.0  
**Last Updated:** October 19, 2025