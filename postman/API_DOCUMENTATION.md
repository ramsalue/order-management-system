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
---

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