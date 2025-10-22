# MELI Order Management System

REST API for managing clients, items, and orders in the MELI e-commerce platform. Built with Spring Boot 3.0, Java 17, and PostgreSQL.

## Project Overview

This project was developed as part of the Digital NAO Backend Developer Certification program to solve technical issues in MELI's order management system. The system provides a robust and flexible approach to order processing with proper database management, environment configuration, and comprehensive API documentation.

## Table of Contents

- [Technologies Used](#technologies-used)
- [System Requirements](#system-requirements)
- [Database Schema](#database-schema)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Running with profiles](#running-with-profiles)
- [Production deployment](#production-deployment)
- [Environment variables](#environment-profiles)
- [Configuration validation](#configuration-validation)
- [Documentation](#documentation)
- [License](#license)

## Technologies Used

- **Java:** 17
- **Spring Boot:** 3.2.0
- **Spring Data JPA:** For database operations
- **PostgreSQL:** 14+ (Database)
- **Maven:** Build tool
- **Postman:** API testing
- **Git:** Version control

## System Requirements

- Java Development Kit (JDK) 17 or higher
- PostgreSQL 14 or higher
- Maven 3.6+ (or use included Maven Wrapper)
- Postman (optional, for API testing)
- IDE: IntelliJ IDEA or VS Code with Java extensions

## Database Schema

### Entities

**Client**
- `id_client` (BIGSERIAL, Primary Key)
- `name` (VARCHAR(100), NOT NULL, UNIQUE)
- `address` (VARCHAR(255), NOT NULL)
- `age` (INTEGER, NOT NULL, CHECK age >= 18)

**Item**
- `item_id` (BIGSERIAL, Primary Key)
- `name` (VARCHAR(150), NOT NULL, UNIQUE)
- `description` (VARCHAR(500))
- `price` (NUMERIC(10,2), NOT NULL, CHECK price > 0)

**Order**
- `id_order` (BIGSERIAL, Primary Key)
- `id_client` (BIGINT, Foreign Key -> Client)
- `purchase_date` (DATE, NOT NULL)
- `delivery_date` (DATE)
- `status` (VARCHAR(255), NOT NULL)

**Order_Items** (Junction Table)
- `id_order` (BIGINT, Foreign Key -> Order)
- `item_id` (BIGINT, Foreign Key -> Item)
- Primary Key: (id_order, item_id)

### Relationships

- Client → Order: One-to-Many
- Order ↔ Item: Many-to-Many (through order_items)

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/ramsalue/order-management-system.git
cd order-management-system
```

### 2. Set Up PostgreSQL Database

```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database
CREATE DATABASE meli_order_db;

-- Create user (optional)
CREATE USER'db_user' WITH PASSWORD 'db_password';
GRANT ALL PRIVILEGES ON DATABASE meli_order_db TO meli_user;
GRANT ALL ON SCHEMA public TO meli_user;
```

### 3. Configure Application Properties

Change name and edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/meli_order_db
spring.datasource.username=meli_user
spring.datasource.password=db_password

# Other configurations are already set
```

### 4. Build the Project

Using installed Maven:

```bash
mvn clean install
```

## Configuration

### Application Properties

The application uses the following default configuration:

- **Server Port:** 8080
- **Database:** PostgreSQL on localhost:5432
- **JPA DDL Auto:** update (automatically creates/updates schema)
- **Logging Level:** DEBUG for application, INFO for root


## Running the Application

### Using Java

```bash
java -jar target/order-management-system-0.0.1-SNAPSHOT.jar
```
### Verify Application is Running

Open a browser or use curl:

```bash
curl http://localhost:8080/api/clients
```

You should receive an empty array `[]` or a list of clients.

## API Documentation

### Interactive Documentation

The application provides comprehensive API documentation using Swagger/OpenAPI 3.0.

**Access Swagger UI:**
```
http://localhost:8080/swagger-ui.html
```
### API Overview

**Clients API** - 11 endpoints for customer management  
**Items API** - 14 endpoints for product catalog  
**Orders API** - 17 endpoints for order processing  
**System API** - 3 endpoints for application information

### OpenAPI Specification

**JSON Format:**
```
http://localhost:8080/api-docs
```

**YAML Format:**
```
http://localhost:8080/api-docs.yaml
```

**Usage:**
- Import into Postman for testing
- Generate client SDKs
- API contract validation
- Share with frontend team

### Quick Start

1. Start the application in development mode
2. Access Swagger UI
3. Select an endpoint section (Clients, Items, Orders, System)
4. Click "Try it out" on any endpoint
5. Modify the example request if needed
6. Click "Execute" to test

For detailed API documentation, see [API Documentation](postman/API_DOCUMENTATION.md)
---

# Updates added with Sprint 2
## Project structure

```
order-management-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/meli/ordermanagementsystem/
│   │   │       ├── config/              # Configuration classes
│   │   │       │   ├── SwaggerConfig.java
│   │   │       │   ├── DatabaseProperties.java
│   │   │       │   ├── ApplicationProperties.java
│   │   │       │   ├── ConfigurationValidator.java
│   │   │       │   ├── DevelopmentConfig.java
│   │   │       │   ├── ProductionConfig.java
│   │   │       │   ├── TestConfig.java
│   │   │       │   └── StartupInfoLogger.java
│   │   │       ├── controller/          # REST Controllers
│   │   │       │   ├── ClientController.java
│   │   │       │   ├── ItemController.java
│   │   │       │   ├── OrderController.java
│   │   │       │   └── InfoController.java
│   │   │       ├── dto/                 # Data Transfer Objects
│   │   │       │   ├── ClientDTO.java
│   │   │       │   ├── ItemDTO.java
│   │   │       │   ├── OrderDTO.java
│   │   │       │   └── OrderResponseDTO.java
│   │   │       ├── exception/           # Exception handling
│   │   │       │   ├── BusinessException.java
│   │   │       │   ├── ResourceNotFoundException.java
│   │   │       │   └── GlobalExceptionHandler.java
│   │   │       ├── model/               # Entity classes
│   │   │       │   ├── Client.java
│   │   │       │   ├── Item.java
│   │   │       │   ├── Order.java
│   │   │       │   └── OrderStatus.java
│   │   │       ├── repository/          # JPA Repositories
│   │   │       │   ├── ClientRepository.java
│   │   │       │   ├── ItemRepository.java
│   │   │       │   └── OrderRepository.java
│   │   │       ├── service/             # Business logic
│   │   │       │   ├── ClientService.java
│   │   │       │   ├── ItemService.java
│   │   │       │   ├── OrderService.java
│   │   │       │   └── EnvironmentService.java
│   │   │       ├── util/                # Utility classes
│   │   │       │   └── EntityMapper.java
│   │   │       └── OrderManagementSystemApplication.java
│   │   └── resources/
│   │       ├── application.properties           # Shared config
│   │       ├── application-dev.properties       # Dev profile
│   │       ├── application-test.properties      # Test profile
│   │       ├── application-prod.properties      # Prod profile
│   │       ├── banner-dev.txt                   # Dev banner
│   │       ├── banner-test.txt                  # Test banner
│   │       ├── banner-prod.txt                  # Prod banner
│   │       ├── static/                          # Static resources
│   │       └── templates/                       # Templates
│   └── test/
│       ├── java/
│       │   └── com/meli/ordermanagementsystem/
│       │       ├── service/             # Unit tests
│       │       │   ├── ClientServiceTest.java
│       │       │   ├── ItemServiceTest.java
│       │       │   └── OrderServiceTest.java
│       │       ├── controller/          # Integration tests
│       │       │   ├── ClientControllerIntegrationTest.java
│       │       │   ├── ItemControllerIntegrationTest.java
│       │       │   └── OrderControllerIntegrationTest.java
│       │       └── integration/         # Database tests
│       │           └── ClientIntegrationTest.java
│       └── resources/
│           └── application.properties   # Test config
├── docs/                                # Documentation
│   ├── CONFIGURATION_GUIDE.md
│   ├── ENVIRONMENT_VARIABLES.md
│   ├── PROFILE_COMPARISON.md
│   ├── PRODUCTION_CHECKLIST.md
│   ├── SPRINT1_SUMMARY.md
│   ├── SPRINT2_SUMMARY.md
│   ├── SPRINT3_SUMMARY.md
│   ├── TEST_COVERAGE_REPORT.md
│   ├── TEST_STRATEGY.md
│   ├── API_DOCUMENTATION.md
│   ├── PROJECT_STRUCTURE.md
│   └── screenshots/
│       ├── coverage/
│       └── swagger/
├── postman/                             # Postman collection
│   ├── MELI_Order_Management_System.postman_collection.json
│   ├── Local_Development.postman_environment.json
│   └── POSTMAN_GUIDE.md
├── scripts/                             # Utility scripts
│   ├── deploy-prod.sh
│   ├── health-check-prod.sh
│   ├── validate-db-schema.sh
│   ├── test-profile-switching.sh
│   └── test-stats.sh
├── target/                              # Build output
│   ├── site/jacoco/                     # Coverage reports
│   └── surefire-reports/                # Test reports
├── .env.template                        # Env var template (dev)
├── .env.production.template             # Env var template (prod)
├── .gitignore                           # Git ignore rules
├── pom.xml                              # Maven configuration
├── README.md                            # Main documentation
├── startup.sh                           # Startup script (Unix)
└── startup.bat                          # Startup script (Windows)
```

## Running with Profiles

### Development Profile (Recommended for Local Development)

The development profile is optimized for local development with:
- Detailed logging (DEBUG level)
- SQL query logging
- Automatic restart on code changes
- Local PostgreSQL database
- All error details visible

**Start with Development Profile:**

Using Maven:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Using JAR:
```bash
java -jar target/order-management-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

Using Environment Variable:
```bash
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run
```

**Verify Active Profile:**
```bash
curl http://localhost:8080/api/info
```

Should return: `"activeProfiles": "[dev]"`

**Development Database Setup:**

The development profile uses local PostgreSQL. Environment variables with fallback values:
- Username: `DB_USERNAME` (default: meli_user)
- Password: `DB_PASSWORD` (default: 'your_db_password')

To use custom credentials, set environment variables:
```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
```
--- 
## Production Deployment

### Prerequisites

1. PostgreSQL database server
2. Production environment variables configured
3. Application built and tested
4. Database schema created and validated

### Deployment Steps

**Step 1: Prepare Environment**

```bash
# Copy template and fill with real credentials
cp .env.production.template .env.production

# Edit .env.production with production values
nano .env.production
```

**Step 2: Validate Database Schema**

```bash
./validate-db-schema.sh
```

**Step 3: Deploy Application**

```bash
./deploy-prod.sh
```

**Step 4: Verify Deployment**

```bash
./health-check-prod.sh
```

### Production Configuration

**Environment Variables Required:**
- `DB_HOST`: Production database host
- `DB_PORT`: Database port (default: 5432)
- `DB_NAME`: Database name
- `DB_USERNAME`: Database user
- `DB_PASSWORD`: Database password
- `SERVER_PORT`: Application port (default: 8080)
- `MANAGEMENT_PORT`: Actuator management port (default: 9090)
- `LOG_FILE_PATH`: Log file location

**Security Notes:**
- Never commit `.env.production` to Git
- Use strong passwords for production database
- Ensure proper file permissions on production server
- Regularly rotate credentials
- Monitor logs for security issues

**Monitoring:**
- Health check: `http://server:9090/actuator/health`
- Metrics: `http://server:9090/actuator/metrics`
- Application info: `http://server:8080/api/info`

**Troubleshooting:**
- Check logs at: `/var/log/meli/application.log`
- Verify environment variables are set
- Ensure database is accessible
- Check firewall rules
- Verify database schema matches application

## Environment Profiles

The application supports three environment profiles:

### Development Profile (dev)
**Use for:** Local development and debugging

**Activation:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Features:**
- Detailed DEBUG logging
- SQL queries logged
- CORS enabled for local frontend
- DevTools auto-reload enabled
- Local PostgreSQL database
- Port: 8080

**Configuration:** `application-dev.properties`

### Testing Profile (test)
**Use for:** Automated testing and CI/CD

**Activation:**
```bash
./mvnw test -Dspring.profiles.active=test
```

**Features:**
- INFO level logging
- Separate test database
- Transaction rollback after tests
- Schema create-drop mode
- Port: 8081

**Configuration:** `application-test.properties`

### Production Profile (prod)
**Use for:** Production deployment

**Activation:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod -Dspring-boot.run.arguments="--spring.datasource.username=meli_user --spring.datasource.password='your_db_password'"
```

**Features:**
- WARN/ERROR logging only
- All credentials from environment variables
- Error details hidden
- Optimized connection pool
- DDL validate mode (no schema changes)
- Port: 8080 (configurable)

**Configuration:** `application-prod.properties`

**Required Environment Variables:**
- `DB_HOST` - Database server hostname
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- See [Environment Variables Guide](docs/ENVIRONMENT_VARIABLES.md) for complete list

## Configuration Validation

The application validates configuration at startup:

```
========================================
CONFIGURATION VALIDATION
========================================
Active Profile: [dev]
Database URL: localhost:5432
...
========================================
CONFIGURATION VALIDATION PASSED
========================================
```

If configuration is invalid, the application will fail to start with clear error messages.

## Profile Switching

To switch between profiles:

1. Stop the application
2. Set the desired profile
3. Start the application

**Example:**
```bash
# Development
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Testing  
./mvnw spring-boot:run -Dspring-boot.run.profiles=test

# Production (with env vars)
export DB_HOST=prod-server
export DB_USERNAME=prod_user
export DB_PASSWORD=prod_pass
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

# Updates coming with Sprint 3
## API Documentation
### Swagger/OpenAPI

The application provides interactive API documentation using Swagger UI.

**Access Swagger UI:**
`http://localhost:8080/swagger-ui.html`

**Access OpenAPI Specification:**
`http://localhost:8080/api-docs`

**Features:**
- Interactive API testing
- Complete endpoint documentation
- Request/response examples
- Model schemas
- Try it out functionality

**Available in:**
- Development profile: Enabled
- Testing profile: Disabled (for performance)
- Production profile: Disabled (enable with SWAGGER_ENABLED=true)

For detailed Swagger usage, see [Swagger Guide](docs/SWAGGER_GUIDE.md)

## Testing

The application includes comprehensive test coverage:

### Test Types

1. **Unit Tests** - Test individual components in isolation
2. **Integration Tests** - Test complete workflows with database

### Running Tests

#### Run All Tests
```bash
./mvnw test
```

#### Run Only Unit Tests
```bash
./mvnw test -Dtest="*ServiceTest"
```

#### Run Only Integration Tests
```bash
./mvnw test -Dtest="*IntegrationTest"
```

### Test Configuration

Tests use the `test` profile with:
- Separate test database
- Transaction rollback
- Schema create-drop mode
- Port 8081

See [Integration Tests Documentation](docs/INTEGRATION_TESTS_DOCUMENTATION.md) for details.

## Testing and Coverage

### Running Tests

**Run all tests:**
```bash
./mvnw test
```

**Run with coverage report:**
```bash
./mvnw clean test jacoco:report
```

**Run specific test:**
```bash
./mvnw test -Dtest=ClientServiceTest
```

**Run integration tests only:**
```bash
./mvnw test -Dtest=*IntegrationTest
```

### Test Coverage

The project uses JaCoCo for code coverage analysis.

**View Coverage Report:**

After running tests, open the HTML report:
```bash
# Windows
start target/site/jacoco/index.html

# Mac
open target/site/jacoco/index.html

# Linux
xdg-open target/site/jacoco/index.html
```

**Coverage Targets:**
- Line Coverage: 70% minimum
- Branch Coverage: 60% minimum
- Service Layer: 85% target
- Controller Layer: 75% target

**Coverage Reports:**
- HTML: `target/site/jacoco/index.html`
- XML: `target/site/jacoco/jacoco.xml` (for CI/CD)
- CSV: `target/site/jacoco/jacoco.csv`

### Test Structure

```
src/test/java/
├── service/          # Unit tests for services
├── controller/       # Integration tests for controllers
└── integration/      # Integration tests for database
```

### Test Profiles

Tests run with the `test` profile automatically:
- Uses separate test database
- Transactions rollback after tests
- Schema created and dropped automatically

See [Test Coverage Report](docs/TEST_COVERAGE_REPORT.md) for detailed analysis.
## Swagger API Documentation

### Access Swagger UI

**URL:** http://localhost:8080/swagger-ui.html

**Features:**
- Interactive API documentation
- Try-it-out functionality for all endpoints
- Request/response examples
- Schema definitions
- Error response documentation

### OpenAPI Specification

- **JSON:** http://localhost:8080/v3/api-docs
- **YAML:** http://localhost:8080/v3/api-docs.yaml

### Swagger Sections

**Client Management API:**
- Create, read, update, delete clients
- Search and filter clients
- Client order history

**Item Management API:**
- Create, read, update, delete items
- Search and filter items
- Price range queries
- Sort by price

**Order Management API:**
- Create, read, update, delete orders
- Order status management
- Cancel orders
- Add/remove items from orders
- Query orders by various criteria

---

## Testing

### Running Tests

**All tests:**
```bash
./mvnw test
```

**With coverage:**
```bash
./mvnw clean test jacoco:report
```

**Specific test:**
```bash
./mvnw test -Dtest=ClientServiceTest
```

**Unit tests only:**
```bash
./mvnw test -Dtest=*ServiceTest
```

**Integration tests only:**
```bash
./mvnw test -Dtest=*IntegrationTest
```

### Test Coverage

**View report:**
```bash
# Windows
start target/site/jacoco/index.html

# Mac
open target/site/jacoco/index.html

# Linux
xdg-open target/site/jacoco/index.html
```

**Coverage targets:**
- Overall: 75%+
- Service Layer: 85%+
- Controller Layer: 75%+
- Repository Layer: 70%+

**Coverage reports:**
- HTML: `target/site/jacoco/index.html`
- XML: `target/site/jacoco/jacoco.xml` (CI/CD)
- CSV: `target/site/jacoco/jacoco.csv`

### Test Structure

```
src/test/java/
├── service/          # Unit tests
│   ├── ClientServiceTest.java
│   ├── ItemServiceTest.java
│   └── OrderServiceTest.java
├── controller/       # Integration tests
│   ├── ClientControllerIntegrationTest.java
│   ├── ItemControllerIntegrationTest.java
│   └── OrderControllerIntegrationTest.java
└── integration/      # Database tests
    └── ClientIntegrationTest.java
```

See [Test Coverage Report](docs/TEST_COVERAGE_REPORT.md) for details.


## Documentation

Complete documentation available in `docs/` folder:
- [Configuration Guide](docs/CONFIGURATION_GUIDE.md)
- [Environment Variables](docs/ENVIRONMENT_VARIABLES.md)
- [Profile Comparison](docs/PROFILE_COMPARISON.md)
- [Swagger guide](docs/SWAGGER_GUIDE.md)
- [Unit testing guide](docs/UNIT_TESTING_GUIDE.md)
---

## License

This project is created for educational purposes as part of the Digital NAO In-Mexico Program.

## Author

Luis E Ramirez  
Digital NAO Backend Developer Certification  
Date: October 22, 2025