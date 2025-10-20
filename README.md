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
- [API Documentation](#api-endpoints)
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

## API endpoints
For testin, you can review complete documentation in `postman/` folder:
- [API documentation](postman/API_DOCUMENTATION.md)
---

# Updates added with Sprint 2
## Project structure
```
order-management-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/meli/ordermanagementsystem/
│   │   │       ├── controller/
│   │   │       │   ├── ApplicationProperties.java
│   │   │       │   ├── ConfigurationValidator.java
│   │   │       │   ├── DatabaseConnection.java
│   │   │       │   ├── DabatabaseProperties.java
│   │   │       │   ├── DevelopmentConfig.java
│   │   │       │   ├── ProductionConfig.java
│   │   │       │   ├── RepositoryTest.java
│   │   │       │   ├── ServiceTest.java
│   │   │       │   ├── StartupInfoLogger.java
│   │   │       │   └── TestConfig.java
│   │   │       ├── controller/          # REST controllers
│   │   │       │   ├── ClientController.java
│   │   │       │   ├── InfoController.java
│   │   │       │   ├── ItemController.java
│   │   │       │   └── OrderController.java
│   │   │       ├── dto/                 # Data Transfer Objects
│   │   │       │   ├── ClientDTO.java
│   │   │       │   ├── ItemDTO.java
│   │   │       │   ├── OrderDTO.java
│   │   │       │   └── OrderResponseDTO.java
│   │   │       ├── exception/           # Custom exceptions
│   │   │       │   ├── BusinessException.java
│   │   │       │   ├── ResourceNotFoundException.java
│   │   │       │   └── GlobalExceptionHandler.java
│   │   │       ├── model/               # Entity classes
│   │   │       │   ├── Client.java
│   │   │       │   ├── Item.java
│   │   │       │   ├── Order.java
│   │   │       │   └── OrderStatus.java
│   │   │       ├── repository/          # JPA repositories
│   │   │       │   ├── ClientRepository.java
│   │   │       │   ├── ItemRepository.java
│   │   │       │   └── OrderRepository.java
│   │   │       ├── service/             # Business logic
│   │   │       │   ├── ClientService.java
│   │   │       │   ├── EnivironmentService.java
│   │   │       │   ├── ItemService.java
│   │   │       │   └── OrderService.java
│   │   │       ├── util/                # Utility classes
│   │   │       │   └── EntityMapper.java
│   │   │       └── OrderManagementSystemApplication.java
│   │   └── resources/                   # Includes application-*.properties files and banner-*.txt for every profile
│   │       ├── application.properties   # Configuration
│   │       └── static/                  # Static resources
│   └── test/
│       └── java/                        # Test classes (Sprint 3)
│       │   └── com/meli/ordermanagementsystem/
│       │       ├── integration/                
│       │       │   └── ClientIntegrationTest.java
│       │       └── OrderManagementSystemApplicationTests.java
│       └── resources/
│           └── application.properties
├── postman/                             # Postman collection
│   ├── MELI_Order_Management_System.postman_collection.json
│   ├── Local_Development.postman_environment.json
│   └── POSTMAN_GUIDE.md
├── docs/                                # Additional documentation
│   ├── Screenshots_Sprint1              # Screenshots sprint 1   
│   ├── SPRINT1_SUMMARY.md           
│   ├── SPRINT2_SUMMARY.md           
│   ├── SPRINT2_TEST_PLAN.md   
│   ├── PROFILE_COMPARISON.md
│   ├── ENVIRONMENT_VARIABLES.md 
│   └── CONFIGURATION_GUIDE.md          
├── .gitignore
├── pom.xml                              # Maven configuration
├── README.md                            # This file
└── startup.sh / startup.bat             # Startup scripts
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

## Documentation

Complete documentation available in `docs/` folder:
- [Configuration Guide](docs/CONFIGURATION_GUIDE.md)
- [Environment Variables](docs/ENVIRONMENT_VARIABLES.md)
- [Profile Comparison](docs/PROFILE_COMPARISON.md)
---

## License

This project is created for educational purposes as part of the Digital NAO In-Mexico Program.

## Author

Luis E Ramirez  
Digital NAO Backend Developer Certification  
Date: October 20, 2025