# MELI Order Management System

REST API for managing clients, items, and orders in the MELI e-commerce platform. Built with Spring Boot 3.2, Java 17, and PostgreSQL.

## Project Overview

This project was developed as part of the Digital NAO Backend Developer Certification program to solve technical issues in MELI's order management system. The system provides a robust and flexible approach to order processing with proper database management, environment configuration, comprehensive API documentation, and thorough testing.

## Table of Contents

- [Technologies Used](#technologies-used)
- [System Requirements](#system-requirements)
- [Database Schema](#database-schema)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Running with Profiles](#running-with-profiles)
- [API Documentation (Swagger)](#api-documentation-swagger)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Production Deployment](#production-deployment)
- [Documentation](#documentation)
- [License](#license)
- [Author](#author)

---

## Technologies Used

- **Java:** 17
- **Spring Boot:** 3.2.11
- **Spring Framework:** 6.1.x (Managed by Spring Boot)
- **Spring Data JPA / Hibernate:** For database operations
- **PostgreSQL:** 14+ (Database)
- **Maven:** Build tool
- **springdoc-openapi:** 2.5.0 (Swagger/OpenAPI Documentation)
- **JUnit 5 / Mockito / AssertJ:** Unit & Integration Testing
- **JaCoCo:** Code Coverage
- **Postman:** API testing (Manual)
- **Git:** Version control

---

## System Requirements

- Java Development Kit (JDK) 17 or higher
- PostgreSQL 14 or higher
- Maven 3.6+ (or use included Maven Wrapper)
- IDE: IntelliJ IDEA or VS Code with Java extensions recommended
- Postman (Optional, for manual API testing)

---

## Database Schema

### Entities

**Client**
- `id_client` (BIGSERIAL, PK)
- `name` (VARCHAR(100), NOT NULL, UNIQUE)
- `address` (VARCHAR(255), NOT NULL)
- `age` (INTEGER, NOT NULL, CHECK age >= 18)

**Item**
- `item_id` (BIGSERIAL, PK)
- `name` (VARCHAR(150), NOT NULL, UNIQUE)
- `description` (VARCHAR(500))
- `price` (NUMERIC(10,2), NOT NULL, CHECK price > 0)

**Order**
- `id_order` (BIGSERIAL, PK)
- `id_client` (BIGINT, FK -> Client)
- `purchase_date` (DATE, NOT NULL)
- `delivery_date` (DATE)
- `status` (VARCHAR(255), NOT NULL CHECK (status IN ('PENDING','PROCESSING','SHIPPED','DELIVERED','CANCELLED')))

**Order_Items** (Junction Table)
- `id_order` (BIGINT, FK -> Order)
- `item_id` (BIGINT, FK -> Item)
- Primary Key: (id_order, item_id)

### Relationships

- Client → Order: One-to-Many
- Order ↔ Item: Many-to-Many (via `order_items`)

---

## Installation

### 1. Clone the Repository

```bash
git clone [https://github.com/ramsalue/order-management-system.git](https://github.com/ramsalue/order-management-system.git)
cd order-management-system
````

### 2\. Set Up PostgreSQL Database

Connect to PostgreSQL (e.g., using `psql -U postgres`) and run the following SQL commands:

```sql
-- Create database for development
CREATE DATABASE meli_order_db;

-- Create database for testing
CREATE DATABASE meli_order_db_test;

-- Create user for development (adjust password as needed)
CREATE USER meli_user WITH PASSWORD 'your_dev_password';
GRANT ALL PRIVILEGES ON DATABASE meli_order_db TO meli_user;
GRANT ALL ON SCHEMA public TO meli_user;

-- Create user for testing (adjust password as needed)
CREATE USER meli_test_user WITH PASSWORD 'your_test_password';
GRANT ALL PRIVILEGES ON DATABASE meli_order_db_test TO meli_test_user;
GRANT ALL ON SCHEMA public TO meli_test_user;
```

### 3\. Configure Local Environment Variables

For local development, create a file named `.env` in the project root (this file is ignored by Git). Add your local database credentials:

```bash
# .env file content
DB_USERNAME=meli_user
DB_PASSWORD=your_dev_password
```

*(Similarly, you can set `DB_TEST_USERNAME` and `DB_TEST_PASSWORD` if you don't use the defaults in `application-test.properties`)*

### 4\. Build the Project

Using the Maven Wrapper (recommended):

```bash
# Windows
./mvnw.cmd clean install

# Mac/Linux
./mvnw clean install
```

-----

## Configuration

The application uses a profile-based configuration system:

  - **`application.properties`**: Contains settings shared across all profiles.
  - **`application-dev.properties`**: Settings for local development (DEBUG logging, auto-reload, local DB).
  - **`application-test.properties`**: Settings for automated testing (INFO logging, test DB, create-drop schema).
  - **`application-prod.properties`**: Settings for production (WARN logging, requires environment variables for credentials, secure defaults).

Sensitive information (like database passwords) is configured via **environment variables**, especially for the production profile.

See the guides below for full details:

  - [Configuration Guide](docs/CONFIGURATION_GUIDE.md)
  - [Environment Variables](docs/ENVIRONMENT_VARIABLES.md)
  - [Profile Comparison](docs/PROFILE_COMPARISON.md)

-----

## Running the Application

### Default (Development Profile)

If no profile is specified, the `dev` profile runs by default. You need to load your `.env` file first.

```bash
# Load variables (Mac/Linux - use 'source' for Git Bash on Windows)
source .env

# Run using Maven Wrapper
./mvnw spring-boot:run
```

### Running Specific Profiles

Use the `-Dspring-boot.run.profiles` flag:

```bash
# Run Development Profile
source .env
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run Testing Profile (Runs on Port 8081)
# (Test credentials have defaults, but can be set via env vars if needed)
./mvnw spring-boot:run -Dspring-boot.run.profiles=test

# Run Production Profile (Locally - Requires DB variables)
# Uses the reliable argument passing method
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod -Dspring-boot.run.arguments="--spring.datasource.username=your_prod_user --spring.datasource.password=your_prod_password --spring.datasource.url=jdbc:postgresql://your_prod_host:5432/your_prod_db"
```

*(Replace `your_prod_...` with actual values for local testing)*

See [Running with Profiles](#running-with-profiles) below for more details.

### Verify Application is Running

```bash
# Check the Info endpoint
curl http://localhost:8080/api/info

# Check the Health endpoint
curl http://localhost:8080/api/info/health
```

-----

## Running with Profiles

The application supports three environment profiles: `dev`, `test`, `prod`.

### Development Profile (`dev`)

Optimized for local development.

**Activation:**

```bash
# Load .env first
source .env
# Run command
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

*(Or simply `./mvnw spring-boot:run` after sourcing `.env`, as `dev` is the default if `spring.profiles.active` is not set in `application.properties`)*

**Features:** Port 8080, DEBUG logging, SQL logging, DevTools auto-reload, CORS enabled, uses `meli_order_db`.

### Testing Profile (`test`)

Used for automated testing.

**Activation (usually automatic via `./mvnw test`):**

```bash
# Manual start on port 8081
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

**Features:** Port 8081, INFO logging, uses `meli_order_db_test`, `create-drop` schema, transaction rollback.

### Production Profile (`prod`)

Optimized for deployment. Requires external configuration via environment variables or command-line arguments.

**Activation (Example using arguments for local simulation):**

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod -Dspring-boot.run.arguments="--spring.datasource.username=meli_user --spring.datasource.password=your_dev_password --spring.datasource.url=jdbc:postgresql://localhost:5432/meli_order_db"
```

**Features:** Port 8080, WARN/ERROR logging, secure defaults, `validate` schema, requires external credentials.

See [Profile Comparison](docs/PROFILE_COMPARISON.md) for a detailed breakdown.

-----

## API Documentation (Swagger)

### Interactive Documentation

Access the interactive Swagger UI when running the `dev` profile:
`http://localhost:8080/swagger-ui.html`

**Features:**

  - View all 40+ endpoints across 4 functional areas (Clients, Items, Orders, System)
  - Read detailed descriptions and parameter requirements
  - See request/response examples and schemas
  - Test endpoints directly using the "Try it out" feature

### OpenAPI Specification

The raw OpenAPI 3.0 specification is available for integration with other tools:

  - **JSON:** `http://localhost:8080/api-docs`
  - **YAML:** `http://localhost:8080/api-docs.yaml`

See the [Swagger Guide](docs/SWAGGER_GUIDE.md) and [API Documentation](postman/API_DOCUMENTATION.md) for more details.

-----

## Testing

The application includes a comprehensive test suite using JUnit 5, Mockito, and Spring Boot Test.

### Test Types

1.  **Unit Tests:** (`src/test/java/.../service/`) Test service layer logic in isolation using Mockito. (\~64 tests)
2.  **Integration Tests:** (`src/test/java/.../integration/`) Test the full application stack (Controller -\> Service -\> Repository -\> Test Database) using MockMvc. (\~49 tests)

### Running Tests

```bash
# Run all tests (Unit + Integration)
./mvnw test

# Run only Unit Tests
./mvnw test -Dtest=*ServiceTest

# Run only Integration Tests
./mvnw test -Dtest=*IntegrationTest
```

### Code Coverage (JaCoCo)

Code coverage is measured using the JaCoCo plugin.

```bash
# Run tests AND generate coverage report
./mvnw clean test jacoco:report

# View the HTML report
# Open target/site/jacoco/index.html in your browser
```

**Targets:** Line Coverage \>= 70%, Branch Coverage \>= 60%.

See the [Unit Testing Guide](docs/UNIT_TESTING_GUIDE.md), [Integration Tests Documentation](docs/INTEGRATION_TESTS_DOCUMENTATION.md), and [Test Coverage Report](docs/TEST_COVERAGE_REPORT.md) for full details.

-----

## Project Structure

```
.
|____.env                           # Local environment variables (Gitignored)
|____.env.production.template      # Template for production variables
|____.env.template                 # Template for local variables
|____.gitattributes
|____.gitignore
|____.mvn
|____docs                           # Project Documentation Files
| |____CONFIGURATION_GUIDE.md
| |____ENVIRONMENT_VARIABLES.md
| |____INTEGRATION_TESTS_DOCUMENTATION.md
| |____PROFILE_COMPARISON.md
| |____Screenshots_Sprint1         # Example Screenshots
| |____SPRINT1_SUMMARY.md
| |____SPRINT2_SUMMARY.md
| |____SPRINT2_TEST_PLAN.md
| |____SPRINT3_SUMMARY.md
| |____SWAGGER_GUIDE.md
| |____TEST_COVERAGE_REPORT.md
| |____UNIT_TESTING_GUIDE.md
|____mvnw
|____mvnw.cmd
|____pom.xml                        # Maven Project Configuration
|____postman                        # Postman Collection & Guides
| |____API_DOCUMENTATION.md
| |____Local_Development.postman_environment.json
| |____MELI_Order_Management_System.postman_collection.json
| |____OpenAPI Specification
| |____POSTMAN_GUIDE.md
|____project_structure.txt          # Generated structure file
|____README.md                      # This file
|____scripts                        # Deployment & Utility Scripts
| |____deploy-prod.sh
| |____health-check-prod.sh
| |____test-stats.sh
| |____validate-db-schema.sh
|____src
| |____main
| | |____java
| | | |____com/meli/ordermanagementsystem # Main application package
| | | | |____config              # Spring configuration, Swagger, Validation
| | | | |____controller          # REST API Controllers
| | | | |____dto                 # Data Transfer Objects
| | | | |____exception           # Custom Exceptions & Global Handler
| | | | |____model               # JPA Entities & Enum
| | | | |____repository          # Spring Data JPA Repositories
| | | | |____service             # Business Logic Services
| | | | |____util                # Utility classes (e.g., Mappers)
| | | | |____OrderManagementSystemApplication.java # Main class
| | |____resources               # Configuration, static files
| | | |____application.properties    # Shared properties
| | | |____application-dev.properties # Dev profile
| | | |____application-prod.properties # Prod profile (Gitignored)
| | | |____application-test.properties # Test profile
| | | |____banner-dev.txt            # Custom banners
| | | |____banner-prod.txt
| | | |____banner-test.txt
| | | |____static
| | | |____templates
| |____test                         # Test code
| | |____java
| | | |____com/meli/ordermanagementsystem # Test packages mirror main
| | | | |____integration         # Integration Tests (Controller/DB level)
| | | | |____service             # Unit Tests (Service level)
| | | | |____TestConfig.java     # Test constants
| | |____resources
| | | |____application.properties    # Test profile activation
|____startup.bat                    # Windows startup script (example)

```

*(Note: Some files like `startup.sh`, specific integration tests, etc., might be present based on previous steps but kept brief here)*

-----

## Production Deployment

This application is configured for production deployment using environment variables.

### Prerequisites

1.  A server environment with Java 17+ and PostgreSQL.
2.  Production database created and accessible.
3.  Environment variables set for database credentials (`DB_HOST`, `DB_USERNAME`, `DB_PASSWORD`, `DB_NAME`, `DB_PORT`).

### Steps

1.  **Build the JAR:** `./mvnw clean package -DskipTests`
2.  **Set Environment Variables:** Configure the required variables on the server.
3.  **Run the JAR:** `java -jar target/order-management-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod`

Deployment scripts (`deploy-prod.sh`, `health-check-prod.sh`, `validate-db-schema.sh`) are provided as examples and may need adjustment for your specific production environment.

-----

## Documentation

Comprehensive project documentation is located in the `docs/` directory, covering:

  - Configuration and Profiles
  - Environment Variables
  - API Usage (Static and Swagger)
  - Testing Strategy and Coverage
  - Project Structure
  - Sprint Summaries

-----

## License

This project is created for educational purposes as part of the Digital NAO In-Mexico Program.

-----

## Author

Luis E Ramirez\
Digital NAO Backend Developer Certification\
Date: October 22, 2025