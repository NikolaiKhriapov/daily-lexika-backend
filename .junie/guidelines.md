# Developer Guidelines <!-- KEEP THIS FILE SHORT, ACTIONABLE & ALWAYS UP-TO-DATE -->

This document provides essential project information. It covers the project structure, tech stack, and guidelines for development, testing, and deployment.

## Project Overview

The project is organized as a multi-module Maven project:

- **daily-lexika**: Main application module for language learning features
- **admin**: Administration module
- **library**: Shared library module with common code used by other modules

Each module has its own database, with database initialization handled by the `init-databases.sql` script.

## Tech Stack

- **Java 17**: Core programming language
- **Spring Boot 3.2.2**: Application framework
- **Spring Web**: REST API support
- **Spring Data JPA**: Database access
- **Spring Security**: Authentication and authorization
- **PostgreSQL**: Database
- **Flyway**: Database migrations
- **Maven**: Build tool
- **Docker**: Containerization
- **Lombok**: Reduces boilerplate code
- **MapStruct**: Object mapping
- **Apache POI**: Excel file handling

## Best Practices

1. **Code Organization**:
   - Follow the domain-based package structure
   - Keep controllers, services, and repositories in their respective packages
   - Use DTOs for data transfer between layers
   - Keep modules independent; only `library` may be imported by others.

2. **Database & Migrations**:
   - Use Flyway migrations for database changes
   - Never alter or delete migrations; add a new one instead

3. **Security**:
   - Follow Spring Security best practices
   - Use proper authentication and authorization

4. **Error Handling**:
   - Use custom exceptions with meaningful messages
   - Handle errors gracefully in GraphQL resolvers
   - Log errors with appropriate levels
   - Provide user-friendly error messages
   - Use proper HTTP status codes
   - Use `I18nUtil` for all user-facing text
   - Store messages in resource bundles, maintain parity between bundles
   - Support multiple languages** (EN/RU)
   - Format dates/numbers** according to locale

5. **Define Clear Transaction Boundaries**:
   - Define each Service-layer method as a transactional unit.
   - Annotate query-only methods with @Transactional(readOnly = true).
   - Annotate data-modifying methods with @Transactional.
   - Limit the code inside each transaction to the smallest necessary scope.

6. **Actuator**:
   - Expose only essential actuator endpoints (such as /health, /info, /metrics) without requiring authentication. All the other actuator endpoints must be secured.

7. **Documentation**:
   - Keep this guide updated as the project evolves
   - Keep README files updated

8. **Testing** (for now - skipping tests)
   - Write integration tests endpoints
   - Test both success and error scenarios**
   - Use Testcontainers for database testing
   - Mock external dependencies appropriately
   - Maintain good test coverage
