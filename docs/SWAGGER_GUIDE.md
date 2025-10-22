# Swagger API Documentation Guide

## Accessing Swagger UI

### Development Environment
**URL:** http://localhost:8080/swagger-ui.html

The Swagger UI is enabled in development profile and provides interactive API documentation.

### Testing Environment
Swagger UI is disabled in testing profile to avoid interference with automated tests.

### Production Environment
Swagger UI is disabled in production for security reasons.
If needed for internal use, it can be enabled with proper authentication.

## Using Swagger UI

### 1. Exploring Endpoints

**Tags:** Endpoints are organized by functional groups:
- **Client Management**: Client CRUD operations
- **Item Management**: Item catalog operations
- **Order Management**: Order processing
- **System Information**: Health and info endpoints

**Operations**: Click on any endpoint to expand and see:
- HTTP method and URL
- Summary and detailed description
- Parameters (path, query, body)
- Request body schema with examples
- Response codes and schemas
- Try it out functionality

### 2. Testing Endpoints

**Step 1:** Click on an endpoint to expand it

**Step 2:** Click "Try it out" button

**Step 3:** Fill in required parameters
- Path parameters appear as input fields
- Request body shows JSON editor with example

**Step 4:** Click "Execute"

**Step 5:** View response
- Response code (200, 201, 404, etc.)
- Response body with actual data
- Response headers
- Request duration

### 3. Understanding Schemas

**Bottom Section:** Scroll to "Schemas" section

**Available Models:**
- ClientDTO: Client request/response model
- ItemDTO: Item request/response model
- OrderDTO: Order request model
- OrderResponseDTO: Order response with full details

**Schema Details:**
- Field names and types
- Validation rules (min, max, required)
- Example values
- Descriptions

### 4. Server Selection

**Dropdown Menu:** Top of page shows server selector

**Available Servers:**
- Development: http://localhost:8080
- Testing: http://localhost:8081
- Production: https://api.meli-orders.com

Select the appropriate server for your environment.

## Common Use Cases

### Creating a Client

1. Expand "Client Management" tag
2. Find "POST /api/clients"
3. Click "Try it out"
4. Use example JSON or modify:
```json
{
  "name": "Test Client",
  "address": "123 Test Street",
  "age": 25
}
```
5. Click "Execute"
6. Check response (should be 201 Created)
7. Note the generated `idClient` for future use

### Creating an Order

1. First create a client and item (get their IDs)
2. Expand "Order Management" tag
3. Find "POST /api/orders"
4. Click "Try it out"
5. Fill in the request:
```json
{
  "clientId": 1,
  "purchaseDate": "2025-10-17",
  "deliveryDate": "2025-10-24",
  "itemIds": [1, 2]
}
```
6. Click "Execute"
7. Response shows complete order with client and items

### Handling Errors

**400 Bad Request:**
- Validation errors show field-specific messages
- Example: "Name is required", "Age must be at least 18"

**404 Not Found:**
- Resource doesn't exist
- Example: Getting client with ID 999

**500 Internal Server Error:**
- Unexpected server error
- Check application logs

## API Information

### Base URL
```
http://localhost:8080
```

### Content Type
All endpoints accept and return:
```
Content-Type: application/json
```

### Authentication
Currently, no authentication is required.
Future versions will implement JWT-based security.

### Rate Limiting
No rate limiting is currently implemented.

### Pagination
List endpoints (GET /api/clients, /api/items, /api/orders) return all results.
Future versions will implement pagination with query parameters.

## Best Practices

### 1. Test in Development First
Always test endpoints in development before using in production.

### 2. Use Valid Data
Follow validation rules shown in schemas:
- Client age must be 18-120
- Item price must be greater than 0
- Order must have at least one item

### 3. Check Response Codes
- 2xx: Success
- 4xx: Client error (fix your request)
- 5xx: Server error (check logs)

### 4. Save Swagger JSON
Download OpenAPI specification:
```
http://localhost:8080/api-docs
```

### 5. Generate Client Code
Use the OpenAPI spec to generate client libraries:
- Use Swagger Codegen
- Or OpenAPI Generator
- Supports Java, JavaScript, Python, etc.

## Troubleshooting

### Swagger UI Not Loading

**Check:**
1. Application is running
2. Profile is 'dev' (Swagger disabled in test/prod)
3. URL is correct: http://localhost:8080/swagger-ui.html
4. No firewall blocking port 8080

### Endpoints Not Showing

**Check:**
1. Controllers are in correct package
2. @RestController annotation present
3. @RequestMapping annotation present
4. Application restarted after code changes

### "Try it out" Not Working

**Check:**
1. Server URL is correct
2. Application is accessible
3. CORS is configured (if frontend on different port)
4. Request body is valid JSON

### Schemas Not Showing

**Check:**
1. DTOs are in correct package
2. @Schema annotations present
3. Getters and setters defined
4. Application recompiled

## Additional Resources

- OpenAPI Specification: https://swagger.io/specification/
- Springdoc Documentation: https://springdoc.org/
- Swagger UI Guide: https://swagger.io/docs/open-source-tools/swagger-ui/

---

**Document Version:** 1.0.0  
**Last Updated:** October 17, 2025