# Test Coverage Report

## Cover Page
**Full Name:** Luis E Ramírez  
**NAO ID:** 3317  
**Date:** October 22, 2025  
**Sprint:** Sprint 3 - Documentation and Testing  
**Challenge:** Spring and Spring Boot in Java for Web Applications

---

## Executive Summary

This report provides an overview of test coverage for the MELI Order Management System.

**Overall Coverage Statistics:**

| Metric | Coverage | Target | Status |
|--------|----------|--------|--------|
| Line Coverage | 62% | 70% | FAIL |
| Branch Coverage | 57% | 60% | FAIL |

**Test Execution Summary:**
- Total Tests: 113
- Unit Tests: 64 
- Integration Tests: 49
- Tests Passed: 113
- Tests Failed: 0
- Execution Time: 61 seconds

---

## Coverage by Package

### Controller Package (com.meli.ordermanagementsystem.controller)

**Coverage:** 45%

| Class | Line Coverage | Branch Coverage | Status |
|-------|---------------|-----------------|--------|
| ClientController | 73% | n/a | PASS |
| ItemController | 58% | n/a | FAIL|
| OrderController | 57% | n/a | FAIL |
| InfoController | 2% | 0% | FAIL |

**Key Findings:**
- There's a need for more tests

### Service Package (com.meli.ordermanagementsystem.service)

**Coverage:** 71%

| Class | Line Coverage | Branch Coverage | Status |
|-------|---------------|-----------------|--------|
| ClientService | 100% | 92% | PASS |
| ItemService | 85% | 83% |PASS |
| OrderService | 71% | 63% | PASS |
| EnvironmentService | 4% | 0% |PASS |

**Key Findings:**
- Business logic thoroughly tested
- Validation rules covered

### Model Package (com.meli.ordermanagementsystem.model)

**Coverage:** 71%

| Class | Line Coverage | Status |
|-------|---------------|--------|
| Client | 60% | FAIL |
| Item | 78% | PASS |
| Order | 65% | FAIL |
| OrderStatus | 86% | PASS |

**Key Findings:**
- Entity getters/setters covered through usage
- Relationship methods tested
- Validation annotations verified

---

## Test Strategy

### Unit Testing Approach

**Scope:** Service layer business logic
**Framework:** JUnit 5 + Mockito
**Coverage Target:** 85%

**Strategy:**
1. Mock repository dependencies
2. Test business logic in isolation
3. Verify exception handling
4. Test validation rules
5. Cover edge cases

**Sample Test Structure:**
```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock
    private Repository repository;
    
    @InjectMocks
    private Service service;
    
    @Test
    void testBusinessLogic() {
        // Given
        when(repository.method()).thenReturn(data);
        
        // When
        Result result = service.method();
        
        // Then
        assertThat(result).isNotNull();
    }
}
```

### Integration Testing Approach

**Scope:** Complete request/response cycles
**Framework:** Spring Boot Test + TestRestTemplate
**Coverage Target:** 75%

**Strategy:**
1. Test complete HTTP request flow
2. Verify database persistence
3. Test error responses
4. Validate JSON serialization
5. Test transaction boundaries

**Sample Test Structure:**
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class ControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testEndpoint() {
        // When
        ResponseEntity<DTO> response = restTemplate
            .postForEntity("/api/resource", request, DTO.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
```
---

### Future Improvements:
   - Add performance tests
   - Implement mutation testing
   - Add contract testing for API
   - Increase test data variety

---

## Test Execution Environment

**Test Profile Configuration:**
- Profile: test
- Database: meli_order_db_test (PostgreSQL)
- Port: 8081
- DDL Mode: create-drop
- Transaction Management: Rollback after each test

**Test Dependencies:**
- JUnit 5 (Jupiter)
- Mockito 5.x
- AssertJ 3.x
- Spring Boot Test
- JaCoCo 0.8.11

---

## Continuous Integration

**CI/CD Integration:**

JaCoCo reports are compatible with:
- Jenkins (JaCoCo plugin)
- GitHub Actions (coverage reports)
- SonarQube (quality gates)
- GitLab CI (coverage badges)

**Maven Command for CI:**
```bash
mvn clean verify
```

**Coverage Thresholds:**
- Minimum Line Coverage: 70%
- Minimum Branch Coverage: 60%
- Build fails if thresholds not met

---

## Appendix

### A. Test File Structure

```
src/test/java/
├── com/meli/ordermanagementsystem/
│   ├── service/
│   │   ├── ClientServiceTest.java
│   │   ├── ItemServiceTest.java
│   │   └── OrderServiceTest.java
│   ├── controller/
│   │   ├── ClientControllerIntegrationTest.java
│   │   ├── ItemControllerIntegrationTest.java
│   │   └── OrderControllerIntegrationTest.java
│   └── integration/
│       └── ClientIntegrationTest.java
```

### B. Coverage Report Locations

- HTML Report: `target/site/jacoco/index.html`
- XML Report: `target/site/jacoco/jacoco.xml`
- CSV Report: `target/site/jacoco/jacoco.csv`
- Execution Data: `target/jacoco.exec`

### C. Running Coverage Locally

```bash
# Generate coverage report
./mvnw clean test jacoco:report

# View report in browser
# Windows: start target/site/jacoco/index.html

# Check coverage thresholds
./mvnw jacoco:check
```

### D. Key Metrics Definitions

**Line Coverage:**
Percentage of executable code lines executed during tests.

**Branch Coverage:**
Percentage of decision branches (if/else, switch, etc.) executed.

**Method Coverage:**
Percentage of methods invoked during tests.

**Class Coverage:**
Percentage of classes instantiated during tests.

---

## Conclusion

The MELI Order Management System has achieved 62% overall test coverage, trying to reach the target threshold of 70%. The test suite provides comprehensive coverage of business logic, API endpoints, and data persistence, but there is a need for more tests to meet the target.

---

**Prepared By:** Luis E Ramírez  
**Date:** October 22, 2025  
**Report Version:** 1.0.0