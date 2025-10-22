# Integration Tests Documentation

## Overview

Integration tests verify that all components of the application work together correctly, including controllers, services, repositories, and the database.

## Test Structure

```
src/test/java/com/meli/ordermanagementsystem/integration/
├── BaseIntegrationTest.java           # Base class with common setup
├── ClientControllerIntegrationTest.java
├── ItemControllerIntegrationTest.java
├── OrderControllerIntegrationTest.java
├── ErrorHandlingIntegrationTest.java
└── IntegrationTestSuite.java          # Test suite runner
```

## Test Coverage

### ClientControllerIntegrationTest (15 tests)
- Create client (success and validation failures)
- Get client by ID (success and not found)
- Get all clients (with data and empty)
- Update client (success and not found)
- Delete client (success and not found)
- Search clients by name
- Get clients by age range
- Complete CRUD workflow

### ItemControllerIntegrationTest (10 tests)
- Create item (success and validation failures)
- Get item by ID
- Get all items
- Update item
- Delete item
- Search items by name
- Get items by price range
- Get items sorted by price

### OrderControllerIntegrationTest (18 tests)
- Create order (success, no items, non-existent client)
- Get order by ID with full details
- Get all orders
- Update order status (success and invalid transition)
- Cancel order (success and already shipped)
- Add items to order
- Remove item from order (success and last item)
- Get orders by client
- Get orders by status
- Delete order (success and shipped)
- Complete order workflow

### ErrorHandlingIntegrationTest (6 tests)
- 404 error response structure
- Validation error response structure
- Business exception error response
- Malformed JSON error
- Method not allowed error
- Unsupported media type error

## Running Tests

### Run All Integration Tests
```bash
./mvnw test -Dtest="*IntegrationTest"
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=ClientControllerIntegrationTest
```

### Run Specific Test Method
```bash
./mvnw test -Dtest=ClientControllerIntegrationTest#testCreateClient_Success
```

### Run Test Suite
```bash
./mvnw test -Dtest=IntegrationTestSuite
```

### Run with Coverage Report
```bash
./mvnw clean test jacoco:report
```

## Test Configuration

Integration tests use:
- **Profile:** test
- **Database:** meli_order_db_test (separate from dev)
- **Port:** 8081
- **Transaction:** Rollback after each test
- **DDL Mode:** create-drop (clean slate)

## Test Utilities

### BaseIntegrationTest
Provides common utilities:
- `mockMvc` - For HTTP request testing
- `objectMapper` - For JSON conversion
- `asJsonString()` - Convert object to JSON
- `fromJsonString()` - Convert JSON to object

### Annotations Used
- `@SpringBootTest` - Loads full Spring context
- `@AutoConfigureMockMvc` - Configures MockMvc
- `@ActiveProfiles("test")` - Uses test profile
- `@Transactional` - Rollback after each test
- `@BeforeEach` - Setup before each test

## Test Patterns

### Testing HTTP Endpoints
```java
mockMvc.perform(get("/api/clients"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$", hasSize(3)));
```

### Testing POST with JSON
```java
mockMvc.perform(post("/api/clients")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(clientDTO)))
    .andExpect(status().isCreated())
    .andExpect(jsonPath("$.idClient").exists());
```

### Verifying Database State
```java
Client client = clientRepository.findById(id).orElse(null);
assertThat(client).isNotNull();
assertThat(client.getName()).isEqualTo("Expected Name");
```

## Best Practices

1. **Clean State:** Use `@BeforeEach` to clean data before each test
2. **Isolation:** Each test should be independent
3. **Transactions:** Use `@Transactional` for automatic rollback
4. **Meaningful Names:** Test names describe what is being tested
5. **AAA Pattern:** Arrange, Act, Assert structure
6. **Verify Database:** Check database state after operations
7. **Test Edge Cases:** Test success, failure, and boundary conditions

## Common Assertions

### HTTP Status
```java
.andExpect(status().isOk())           // 200
.andExpect(status().isCreated())      // 201
.andExpect(status().isNoContent())    // 204
.andExpect(status().isBadRequest())   // 400
.andExpect(status().isNotFound())     // 404
```

### JSON Response
```java
.andExpect(jsonPath("$.name").value("John"))
.andExpect(jsonPath("$.items", hasSize(2)))
.andExpect(jsonPath("$.status").exists())
```

### Database State
```java
assertThat(entity).isNotNull();
assertThat(list).hasSize(3);
assertThat(repository.findById(id)).isEmpty();
```

## Troubleshooting

### Test Fails with "Table does not exist"
- Ensure test profile is active
- Check `ddl-auto=create-drop` in application-test.properties
- Verify test database exists

### Test Fails with "Connection refused"
- Ensure PostgreSQL is running
- Check test database credentials
- Verify port 5432 is accessible

### Tests Pass Individually but Fail Together
- Check for data dependencies between tests
- Ensure `@BeforeEach` cleans all data
- Use `@Transactional` for isolation

### Transaction Not Rolling Back
- Verify `@Transactional` annotation is present
- Check test class extends `BaseIntegrationTest`
- Ensure test profile is active

---

**Total Integration Tests:** 48  
**Expected Pass Rate:** 100%  
**Coverage Target:** 80%+

**Document Version:** 1.0.0  
**Last Updated:** October 22, 2025