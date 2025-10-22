# API Documentation Guide

## Overview

This document provides a comprehensive guide to using the MELI Order Management System API.

## Base URL

```
http://localhost:8080
```

## Authentication

Currently, no authentication is required. This will be added in future versions.

## Content Type

All requests and responses use `application/json` content type.

## Swagger Documentation

### Access

**Swagger UI:** http://localhost:8080/swagger-ui.html  
**OpenAPI Spec (JSON):** http://localhost:8080/v3/api-docs  
**OpenAPI Spec (YAML):** http://localhost:8080/v3/api-docs.yaml

### Using Swagger UI

1. Navigate to http://localhost:8080/swagger-ui.html
2. Browse API endpoints by tag (Client, Item, Order)
3. Click on an endpoint to see details
4. Click "Try it out" to test endpoints
5. Fill in parameters
6. Click "Execute"
7. View response

## API Endpoints

### Client Management API

**Base Path:** `/api/clients`

#### Create Client
```http
POST /api/clients
Content-Type: application/json

{
  "name": "John Smith",
  "address": "123 Main Street",
  "age": 30
}
```

**Response (201 Created):**
```json
{
  "idClient": 1,
  "name": "John Smith",
  "address": "123 Main Street",
  "age": 30
}
```

#### Get All Clients
```http
GET /api/clients
```

#### Get Client by ID
```http
GET /api/clients/{id}
```

#### Update Client
```http
PUT /api/clients/{id}
Content-Type: application/json

{
  "name": "John Smith Updated",
  "address": "456 Oak Avenue",
  "age": 31
}
```

#### Delete Client
```http
DELETE /api/clients/{id}
```

#### Search Clients
```http
GET /api/clients/search?name=John
```

#### Get Clients by Age Range
```http
GET /api/clients/age-range?minAge=25&maxAge=40
```

### Item Management API

**Base Path:** `/api/items`

#### Create Item
```http
POST /api/items
Content-Type: application/json

{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99
}
```

#### Get All Items
```http
GET /api/items
```

#### Get Item by ID
```http
GET /api/items/{id}
```

#### Update Item
```http
PUT /api/items/{id}
```

#### Delete Item
```http
DELETE /api/items/{id}
```

#### Search Items
```http
GET /api/items/search?name=Laptop
```

#### Get Items by Price Range
```http
GET /api/items/price-range?minPrice=100&maxPrice=1000
```

### Order Management API

**Base Path:** `/api/orders`

#### Create Order
```http
POST /api/orders
Content-Type: application/json

{
  "clientId": 1,
  "purchaseDate": "2025-10-17",
  "deliveryDate": "2025-10-24",
  "itemIds": [1, 2]
}
```

**Response (201 Created):**
```json
{
  "idOrder": 1
  "client": {
    "idClient": 1,
    "name": "John Smith",
    "address": "123 Main Street",
    "age": 30
  },
  "purchaseDate": "2025-10-17",
  "deliveryDate": "2025-10-24",
  "status": "PENDING",
  "items": [
    {
      "itemId": 1,
      "name": "Laptop",
      "description": "High-performance laptop",
      "price": 999.99
    },
    {
      "itemId": 2,
      "name": "Mouse",
      "description": "Wireless mouse",
      "price": 29.99
    }
  ]
}
```

#### Get All Orders
```http
GET /api/orders
```

#### Get Order by ID
```http
GET /api/orders/{id}
```

#### Update Order Status
```http
PATCH /api/orders/{id}/status?status=PROCESSING
```

#### Cancel Order
```http
POST /api/orders/{id}/cancel
```

#### Add Items to Order
```http
POST /api/orders/{id}/items
Content-Type: application/json

[2, 3, 4]
```

#### Get Orders by Client
```http
GET /api/orders/client/{clientId}
```

#### Get Orders by Status
```http
GET /api/orders/status/PENDING
```

## Error Responses

### 400 Bad Request

**Validation Error:**
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2025-10-17T10:30:00",
  "fieldErrors": {
    "name": "Name is required",
    "age": "Age must be at least 18"
  }
}
```

**Business Rule Violation:**
```json
{
  "status": 400,
  "message": "Cannot delete client with existing orders. Client has 5 orders.",
  "timestamp": "2025-10-17T10:35:00"
}
```

### 404 Not Found

```json
{
  "status": 404,
  "message": "Client not found with id: '999'",
  "timestamp": "2025-10-17T10:40:00"
}
```

### 500 Internal Server Error

```json
{
  "status": 500,
  "message": "An unexpected error occurred",
  "timestamp": "2025-10-17T10:45:00"
}
```

## Status Codes

| Code | Meaning | Usage |
|------|---------|-------|
| 200 | OK | Successful GET, PUT, PATCH |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Validation error, business rule violation |
| 404 | Not Found | Resource doesn't exist |
| 500 | Internal Server Error | Server error |

## Order Status Workflow

Valid status transitions:
```
PENDING → PROCESSING → SHIPPED → DELIVERED
   ↓           ↓
CANCELLED   CANCELLED
```

**Rules:**
- Cannot cancel SHIPPED or DELIVERED orders
- Cannot update completed orders (DELIVERED, CANCELLED)
- Status must follow valid transition path

## Rate Limiting

Currently no rate limiting implemented. Will be added in future versions.

## Versioning

Current version: 1.0.0

API versioning will be implemented in future releases.

## Examples

### Complete Order Creation Flow

**Step 1: Create Client**
```bash
curl -X POST http://localhost:8080/api/clients \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Johnson",
    "address": "789 Pine Street",
    "age": 28
  }'
```

**Step 2: Create Items**
```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Keyboard",
    "description": "Mechanical keyboard",
    "price": 79.99
  }'
```

**Step 3: Create Order**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "purchaseDate": "2025-10-17",
    "deliveryDate": "2025-10-24",
    "itemIds": [1]
  }'
```

**Step 4: Update Order Status**
```bash
curl -X PATCH http://localhost:8080/api/orders/1/status?status=PROCESSING
```

## Postman Collection

Import the Postman collection for easy testing:
- File: `postman/MELI_Order_Management_System.postman_collection.json`
- Environment: `postman/Local_Development.postman_environment.json`

## Support

For issues or questions:
- Review Swagger documentation
- Check error messages
- Consult README.md
- Review test cases for examples

---

**Document Version:** 1.0.0  
**Last Updated:** October 22, 2025