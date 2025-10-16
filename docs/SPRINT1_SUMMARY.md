# Sprint 1 Summary Report

## Cover Page
**Full Name:** Luis E Ramirez  
**NAO ID:** 3317   
**Date:** October 16, 2025  
**Sprint:** Sprint 1 - Foundation Setup  
**Challenge:** Spring and Spring Boot in Java for Web Applications

---

## Sprint Overview

**Duration:** 3 days  
**Start Date:** 14-10-2025  
**End Date:** 16-10-2025  
**Status:** Completed

## Sprint Objectives

1. Create a web project using Spring Boot 3.0
2. Implement order management system with database connection
3. Develop CRUD operations for clients, items, and orders
4. Document code and upload to GitHub
5. Create Postman collection for API testing

## Deliverables Completed

### 1. Project Setup
- Spring Boot 3.0 project initialized
- PostgreSQL database configured
- Maven build configuration complete
- Project structure established

### 2. Database Implementation
- Three entities created: Client, Item, Order
- Entity relationships implemented (One-to-Many, Many-to-Many)
- PostgreSQL database schema generated automatically
- Connection pooling configured

### 3. Repository Layer
- ClientRepository with custom queries
- ItemRepository with custom queries
- OrderRepository with custom queries
- Total: 50+ repository methods

### 4. Service Layer
- ClientService with business logic
- ItemService with business logic
- OrderService with business logic
- Custom exception handling
- Comprehensive validation

### 5. REST API
- ClientController: 10 endpoints
- ItemController: 14 endpoints
- OrderController: 17 endpoints
- Total: 41 endpoints
- Global exception handler implemented

### 6. Documentation
- Comprehensive README.md
- JavaDoc comments on all classes
- API endpoint documentation
- Postman collection created
- Startup scripts provided

### 7. Testing
- Postman collection with 41 requests
- Environment variables configured
- Automated tests in Postman
- Manual testing completed

### 8. Version Control
- GitHub repository created
- .gitignore configured
- Code committed with meaningful messages
- Repository structure organized

## Technical Achievements

### Technologies Used
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL 14
- Maven
- Postman

### Architecture Implemented
- Layered architecture (Controller-Service-Repository)
- RESTful API design
- DTO pattern for API layer
- Global exception handling
- Validation framework

### Design Patterns Applied
- Repository Pattern
- Service Layer Pattern
- Data Transfer Object (DTO)
- Dependency Injection
- Factory Pattern (EntityMapper)

### SOLID Principles
- Single Responsibility
- Open/Closed
- Liskov Substitution
- Interface Segregation
- Dependency Inversion

## Business Rules Implemented

### Client Management
1. Unique client names
2. Age validation (18-120)
3. Cannot delete clients with orders

### Item Management
1. Unique item names
2. Price validation (> 0)
3. Cannot delete items in orders

### Order Management
1. Minimum one item per order
2. Date validations
3. Status transition rules
4. Cannot modify completed orders
5. Cancellation restrictions

## Challenges and Solutions

### Challenge 1: Database Relationships
**Problem:** Complex Many-to-Many relationship between Order and Item  
**Solution:** Implemented junction table (order_items) with proper JPA annotations

### Challenge 2: Circular References in JSON
**Problem:** Infinite recursion when serializing relationships  
**Solution:** Used DTOs to control response structure and avoid circular references

### Challenge 3: Transaction Management
**Problem:** Ensuring data consistency across operations  
**Solution:** Implemented @Transactional annotations with proper propagation

### Challenge 4: Validation Logic
**Problem:** Complex business rules validation  
**Solution:** Created service layer with helper methods for reusable validation

## Lessons Learned

1. **Planning is crucial:** Proper entity design prevented major refactoring
2. **Layer separation:** Clear separation of concerns made code maintainable
3. **Documentation early:** Writing JavaDoc while coding saved time
4. **Testing continuously:** Early testing caught issues quickly
5. **Version control:** Frequent commits made tracking changes easier

## Next Sprint Preview

Sprint 2 will focus on:
- Environment profile configuration (dev, test, prod)
- Environment variables for sensitive data
- Profile-specific database connections
- Security enhancements
- Deployment preparation

## Conclusion

Sprint 1 has been completed with all objectives met. The foundation for the MELI Order Management System is solid, with proper architecture, comprehensive documentation, and thorough testing. The system is ready for Sprint 2 enhancements.

---

**Developer:** Luis E Ramirez   
**Date:** 16-10-2025 
---

**Document Version:** 1.0.0  
**Status:** Final  
**Next Review:** Sprint 2 Completion