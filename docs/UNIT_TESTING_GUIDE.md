# Unit Testing Guide

## Overview

This document explains the unit testing strategy for the MELI Order Management System.

## Testing Framework

**Technologies Used:**
- **JUnit 5 (Jupiter)**: Testing framework
- **Mockito**: Mocking framework
- **AssertJ**: Fluent assertions

## Test Structure

### Package Organization

```
src/test/java/
└── com/meli/ordermanagementsystem/
    ├── TestConfig.java (test utilities)
    ├── service/
    │   ├── ClientServiceTest.java
    │   ├── ItemServiceTest.java
    │   └── OrderServiceTest.java
    └── integration/
        └── ClientIntegrationTest.java
```

### Test Naming Convention

**Format:** `test[MethodName]_[Scenario]`

**Examples:**
- `testCreateClient_Success`
- `testCreateClient_DuplicateName`
- `testGetClientById_NotFound`

### Test Structure (AAA Pattern)

```java
@Test
@DisplayName("Should create client successfully")
void testCreateClient_Success() {
    // ARRANGE: Set up test data and mocks
    when(clientRepository.existsByName("John")).thenReturn(false);
    when(clientRepository.save(any())).thenReturn(testClient);
    
    // ACT: Execute the method being tested
    Client result = clientService.createClient(testClient);
    
    // ASSERT: Verify the results
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("John");
    
    // VERIFY: Check mock interactions
    verify(clientRepository, times(1)).save(testClient);
}
```

## Running Tests

### Run All Tests

```bash
./mvnw test
```

### Run Specific Test Class

```bash
./mvnw test -Dtest=ClientServiceTest
```

### Run Specific Test Method

```bash
./mvnw test -Dtest=ClientServiceTest#testCreateClient_Success
```

### Run All Service Tests

```bash
./mvnw test -Dtest=*ServiceTest
```

## Test Coverage

### By Service

**ClientService:**
- Create: 4 tests
- Read: 4 tests
- Update: 3 tests
- Delete: 3 tests
- Search: 2 tests
- Business Logic: 3 tests
- **Total: 19 tests**

**ItemService:**
- Create: 4 tests
- Read: 3 tests
- Update: 2 tests
- Delete: 2 tests
- Search: 3 tests
- Business Logic: 4 tests
- **Total: 18 tests**

**OrderService:**
- Create: 6 tests
- Read: 3 tests
- Update Status: 3 tests
- Cancel: 4 tests
- Add/Remove Items: 4 tests
- Delete: 3 tests
- Query: 4 tests
- **Total: 27 tests**

**Total Unit Tests: 64 tests**

## Testing Best Practices

### 1. Test One Thing at a Time

```java
// Good: Tests one specific scenario
@Test
void testCreateClient_DuplicateName() {
    when(clientRepository.existsByName("John")).thenReturn(true);
    assertThatThrownBy(() -> clientService.createClient(client))
        .isInstanceOf(BusinessException.class);
}

// Bad: Tests multiple scenarios
@Test
void testCreateClient() {
    // Tests both success and failure
}
```

### 2. Use Descriptive Test Names

```java
// Good
@DisplayName("Should throw BusinessException when client age is less than 18")
void testCreateClient_InvalidAge()

// Bad
void test1()
```

### 3. Use AssertJ for Readable Assertions

```java
// Good: Fluent and readable
assertThat(client.getName()).isEqualTo("John");
assertThat(clients).hasSize(2);
assertThat(price).isGreaterThan(BigDecimal.ZERO);

// Less readable
assertEquals("John", client.getName());
assertTrue(clients.size() == 2);
```

### 4. Verify Mock Interactions

```java
// Verify method was called
verify(repository, times(1)).save(client);

// Verify method was never called
verify(repository, never()).delete(any());

// Verify with specific arguments
verify(repository).findById(1L);
```

### 5. Test Edge Cases

Always test:
- Null values
- Empty collections
- Boundary values (min/max)
- Invalid inputs
- Exception scenarios

## Common Patterns

### Testing Exceptions

```java
@Test
void testMethod_ThrowsException() {
    assertThatThrownBy(() -> service.method(invalidInput))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("expected text");
}
```

### Testing with Multiple Items

```java
@Test
void testGetAll_MultipleItems() {
    List<Client> clients = Arrays.asList(client1, client2, client3);
    when(repository.findAll()).thenReturn(clients);
    
    List<Client> result = service.getAllClients();
    
    assertThat(result).hasSize(3);
    assertThat(result).contains(client1, client2, client3);
}
```

### Testing Void Methods

```java
@Test
void testDeleteClient() {
    when(repository.findById(1L)).thenReturn(Optional.of(client));
    when(repository.countOrdersByClientId(1L)).thenReturn(0L);
    doNothing().when(repository).delete(client);
    
    service.deleteClient(1L);
    
    verify(repository, times(1)).delete(client);
}
```

## Troubleshooting

### Test Fails with NullPointerException

**Problem:** Mock not initialized
**Solution:** Check @Mock and @InjectMocks annotations, ensure @ExtendWith(MockitoExtension.class) is present

### Test Fails: Wanted but not invoked

**Problem:** Method not called on mock
**Solution:** Check the method is actually called in the service, verify argument matchers

### Test Fails: Unnecessary stubbings

**Problem:** Mock defined but never used
**Solution:** Remove unused `when()` statements or verify method is called

### All Tests Pass Locally but Fail in CI

**Problem:** Test interdependencies or state leakage
**Solution:** Ensure `@BeforeEach` properly resets state, avoid static variables

---

**Document Version:** 1.0.0  
**Last Updated:** October 22, 2025