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
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Business Rules](#business-rules)
- [Contributing](#contributing)
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

### Using Maven Wrapper

```bash
# Windows (this was used for current project)
mvnw.cmd spring-boot:run

# Mac/Linux
./mvnw spring-boot:run
```

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

### Base URL

```
http://localhost:8080
```

### Available Endpoints

#### Client Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/clients` | Create a new client |
| GET | `/api/clients` | Get all clients |
| GET | `/api/clients/{id}` | Get client by ID |
| PUT | `/api/clients/{id}` | Update client |
| DELETE | `/api/clients/{id}` | Delete client |
| GET | `/api/clients/search?name={name}` | Search clients by name |
| GET | `/api/clients/age-range?minAge={min}&maxAge={max}` | Filter by age range |
| GET | `/api/clients/with-orders` | Get clients with orders |
| GET | `/api/clients/without-orders` | Get clients without orders |
| GET | `/api/clients/{id}/order-count` | Get client's order count |

#### Item Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/items` | Create a new item |
| GET | `/api/items` | Get all items |
| GET | `/api/items/{id}` | Get item by ID |
| PUT | `/api/items/{id}` | Update item |
| DELETE | `/api/items/{id}` | Delete item |
| GET | `/api/items/search?name={name}` | Search items by name |
| GET | `/api/items/price-range?minPrice={min}&maxPrice={max}` | Filter by price |
| GET | `/api/items/within-budget?maxPrice={max}` | Items within budget |
| GET | `/api/items/sorted/price-asc` | Sort by price ascending |
| GET | `/api/items/sorted/price-desc` | Sort by price descending |
| GET | `/api/items/in-orders` | Items in orders |
| GET | `/api/items/never-ordered` | Items never ordered |
| GET | `/api/items/{id}/order-count` | Get item's order count |
| GET | `/api/items/top-expensive?limit={n}` | Top N expensive items |

#### Order Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Create a new order |
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{id}` | Get order by ID |
| PUT | `/api/orders/{id}` | Update order |
| DELETE | `/api/orders/{id}` | Delete order |
| PATCH | `/api/orders/{id}/status?status={status}` | Update order status |
| POST | `/api/orders/{id}/items` | Add items to order |
| DELETE | `/api/orders/{orderId}/items/{itemId}` | Remove item from order |
| POST | `/api/orders/{id}/cancel` | Cancel order |
| GET | `/api/orders/client/{clientId}` | Get client's orders |
| GET | `/api/orders/status/{status}` | Get orders by status |
| GET | `/api/orders/date-range?startDate={start}&endDate={end}` | Filter by date |
| GET | `/api/orders/recent?days={n}` | Recent orders |
| GET | `/api/orders/in-transit` | Orders in transit |
| GET | `/api/orders/overdue` | Overdue orders |
| GET | `/api/orders/containing-item/{itemId}` | Orders with specific item |
| GET | `/api/orders/count-by-status?status={status}` | Count by status |

### Request/Response Examples

#### Create Client

**Request:**
```bash
POST /api/clients
Content-Type: application/json

{
  "name": "John Smith",
  "address": "123 Main Street, New York, NY 10001",
  "age": 30
}
```

**Response (201 Created):**
```json
{
  "idClient": 1,
  "name": "John Smith",
  "address": "123 Main Street, New York, NY 10001",
  "age": 30
}
```

#### Create Order

**Request:**
```bash
POST /api/orders
Content-Type: application/json

{
  "clientId": 1,
  "purchaseDate": "2025-10-14",
  "deliveryDate": "2025-10-21",
  "itemIds": [1, 2]
}
```

**Response (201 Created):**
```json
{
  "idOrder": 1,
  "client": {
    "idClient": 1,
    "name": "John Smith",
    "address": "123 Main Street, New York, NY 10001",
    "age": 30
  },
  "purchaseDate": "2025-10-14",
  "deliveryDate": "2025-10-21",
  "status": "PENDING",
  "items": [
    {
      "itemId": 1,
      "name": "Wireless Mouse",
      "description": "Ergonomic wireless mouse",
      "price": 29.99
    },
    {
      "itemId": 2,
      "name": "USB Keyboard",
      "description": "Mechanical keyboard",
      "price": 79.99
    }
  ]
}
```

### Error Responses

**Validation Error (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2025-10-14T10:30:00",
  "fieldErrors": {
    "name": "Name is required",
    "age": "Age must be at least 18"
  }
}
```

**Resource Not Found (404 Not Found):**
```json
{
  "status": 404,
  "message": "Client not found with id: '999'",
  "timestamp": "2025-10-14T10:35:00"
}
```

**Business Rule Violation (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Cannot delete client with existing orders. Client has 5 orders.",
  "timestamp": "2025-10-14T10:40:00"
}
```

## Testing

### Using Postman

1. Import the Postman collection:
   - File: `postman/MELI_Order_Management_System.postman_collection.json`
2. Import the environment:
   - File: `postman/Local_Development.postman_environment.json`
3. Select "Local Development" environment
4. Run requests in order:
   - Create Client
   - Create Items
   - Create Order
   - Test other endpoints

See `postman/POSTMAN_GUIDE.md` for detailed instructions.

**Get all clients:**
```bash
curl http://localhost:8080/api/clients
```

### Manual Testing Workflow

1. Start the application
2. Create at least one client
3. Create at least one item
4. Create an order with the client and item IDs
5. Update order status: PENDING → PROCESSING → SHIPPED → DELIVERED
6. Test query endpoints
7. Test validation (try creating invalid data)
8. Test business rules (try deleting client with orders)

## Project Structure

```
order-management-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/meli/ordermanagementsystem/
│   │   │       ├── controller/          # REST controllers
│   │   │       │   ├── ClientController.java
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
│   │   │       │   ├── ItemService.java
│   │   │       │   └── OrderService.java
│   │   │       ├── util/                # Utility classes
│   │   │       │   └── EntityMapper.java
│   │   │       └── OrderManagementSystemApplication.java
│   │   └── resources/
│   │       ├── application.properties   # Configuration
│   │       └── static/                  # Static resources
│   └── test/
│       └── java/                        # Test classes (Sprint 3)
├── postman/                             # Postman collection
│   ├── MELI_Order_Management_System.postman_collection.json
│   ├── Local_Development.postman_environment.json
│   └── POSTMAN_GUIDE.md
├── docs/                                # Additional documentation
│   ├── Screenshots_Sprint1              # Screenshots sprint 1   
│   └── SPRINT1_SUMMARY.md           
├── .gitignore
├── pom.xml                              # Maven configuration
├── README.md                            # This file
└── startup.sh / startup.bat             # Startup scripts
```

## Business Rules

### Client Management

1. Client names must be unique
2. Client age must be between 18 and 120
3. Cannot delete clients who have existing orders
4. All client fields are required

### Item Management

1. Item names must be unique
2. Item price must be greater than zero
3. Cannot delete items that exist in orders
4. Price uses two decimal places

### Order Management

1. Orders must have at least one item
2. Orders must be associated with an existing client
3. Purchase date cannot be in the future
4. Delivery date must be after purchase date
5. Cannot modify completed orders (DELIVERED or CANCELLED)
6. Valid status transitions:
   - PENDING → PROCESSING or CANCELLED
   - PROCESSING → SHIPPED or CANCELLED
   - SHIPPED → DELIVERED
7. Can only cancel PENDING or PROCESSING orders
8. Can only delete PENDING or CANCELLED orders

## Architecture and Design Patterns

### Layered Architecture

- **Controller Layer:** Handles HTTP requests/responses
- **Service Layer:** Contains business logic and validations
- **Repository Layer:** Database access operations
- **Model Layer:** Entity definitions

### Design Patterns Used

- **Repository Pattern:** Data access abstraction
- **Service Layer Pattern:** Business logic separation
- **Data Transfer Object (DTO):** API request/response objects
- **Dependency Injection:** Loose coupling between components
- **Exception Handling Pattern:** Centralized error handling

## Troubleshooting

### Application won't start

**Problem:** Port 8080 already in use
```
Solution: Change server.port in application.properties or stop other application
```

**Problem:** Cannot connect to database
```
Solution: 
1. Verify PostgreSQL is running
2. Check database credentials in application.properties
3. Ensure database exists
```

### API errors

**Problem:** 404 on all endpoints
```
Solution: Verify application is running and check base URL
```

**Problem:** Validation errors
```
Solution: Check request body matches required format and constraints
```

**Problem:** Business rule violations
```
Solution: Read error message carefully and follow business rules
```
## License

This project is created for educational purposes as part of the Digital NAO In-Mexico Program.

## Author

Luis E Ramirez  
Digital NAO Backend Developer Certification  
Date: October 16, 2025