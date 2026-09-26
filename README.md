# Enterprise Web Solutions & Distributed Backend Architecture

[![Java 17](https://img.shields.io/badge/Java-17%20LTS-orange.svg?logo=openjdk)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot 3.2](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/JPA-Hibernate%206-blue.svg?logo=hibernate)](https://spring.io/projects/spring-data-jpa)
[![Spring Security](https://img.shields.io/badge/Security-Stateless%20JWT-red.svg?logo=springsecurity)](https://spring.io/projects/spring-security)
[![OpenAPI 3 / Swagger](https://img.shields.io/badge/OpenAPI-Swagger%20UI%203-green.svg?logo=swagger)](http://localhost:8080/swagger-ui/index.html)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> **Enterprise-grade modular backend framework built with Java 17 LTS and Spring Boot 3.**
> Designed with decoupled N-Tier Clean Architecture, dual persistence strategies (Spring Data JPA & manual EntityManager), declarative Bean Validation (JSR-380), centralized structured exception handling, stateless authentication with JWT tokens, and interactive OpenAPI 3 documentation.

---

## System Architecture

The system implements a strictly decoupled N-Tier architecture designed for maximum maintainability, testability, and enterprise scalability:

```text
+---------------------------------------------------------------------------------+
| Presentation & Client Layer (Browser / cURL / Postman / Automated Clients)     |
+---------------------------------------------------------------------------------+
                                        |
                                        v
+---------------------------------------------------------------------------------+
| Security & Filter Layer (CORS Filter / SecurityFilterChain Stateless)          |
+---------------------------------------------------------------------------------+
                                        |
                                        v
+---------------------------------------------------------------------------------+
| REST Controller Layer (@RestController)                                         |
| - SaludController (/api/saludo)         - AuthController (/api/auth)            |
| - PacienteController (/api/pacientes)   - ProductoController (/api/productos)  |
| - ItemGenericoController (/api/items)   - LegacyEntityManagerController         |
| - GlobalExceptionHandler (@RestControllerAdvice)                                |
+---------------------------------------------------------------------------------+
                                        |
                                        v
+---------------------------------------------------------------------------------+
| Service Layer (@Service - GoF Patterns, Validation & Business Logic)            |
| - PacienteServiceImpl                   - ProductoServiceImpl                   |
| - ItemGenericoServiceImpl               - ServiceFactory (GoF Factory Pattern)  |
+---------------------------------------------------------------------------------+
                                        |
                                        v
+---------------------------------------------------------------------------------+
| Dual Persistence Layer                                                          |
| [Spring Data JPA Interfaces]             [Low-Level EntityManager Repository]   |
| - PacienteRepository (JPQL :param)       - PacienteEntityManagerRepository      |
| - ProductoRepository (@NamedQuery)       - ProductoEntityManagerRepository      |
| - ItemGenericoRepository                                                        |
+---------------------------------------------------------------------------------+
                                        |
                                        v
+---------------------------------------------------------------------------------+
| Relational Storage Tier (H2 In-Memory Default / MySQL Relational Profile)       |
+---------------------------------------------------------------------------------+
```

---

## Key Architectural Highlights

1. **Dual Persistence Capabilities:**
 - **High-Productivity Layer:** `JpaRepository<T, ID>` with derived query methods, parameterized JPQL (`@Query` with `@Param`), and declarative `@NamedQuery`.
 - **Low-Level Control Layer:** Dedicated `@Repository` classes using `EntityManager` directly (`persist()`, `find()`, `merge()`, `remove()`, `createQuery()`) for custom lifecycle hooks and high-performance transactional manipulation.
2. **Dynamic In-Memory Computations (`@Transient`):**
 - Business calculations are decoupled from the physical database schema. Computed values (`estadoAnemia`, `estadoStock`, `clasificacionCalculada`) are evaluated on runtime demand and seamlessly serialized into JSON output.
3. **Robust Declarative Validation:**
 - Input payloads are rigorously validated using Bean Validation (`@Valid`, `@NotNull`, `@NotBlank`, `@Size`, `@DecimalMin`, `@Min`, `@Max`).
 - Validation failures are intercepted globally by `@RestControllerAdvice`, returning RFC-compliant structured JSON:
 ```json
 {
 "timestamp": "2026-09-25T23:50:00",
 "status": 400,
 "error": "Bad Request",
 "message": "Error de validación en los campos enviados en la solicitud",
 "path": "/api/productos",
 "fieldErrors": {
 "nombre": "El nombre debe tener entre 2 y 100 caracteres",
 "precio": "El precio debe ser mayor a 0"
 }
 }
 ```
4. **Stateless Security & Cross-Origin Resource Sharing (CORS):**
 - Stateless JWT emission and authentication via `/api/auth/login`.
 - Pre-flight `OPTIONS` handling and full CORS permissions for frontends (e.g., Angular on `http://localhost:4200`).
5. **Interactive UI Verification Console:**
 - Embedded native HTML verification dashboard accessible at `http://localhost:8080/index.html` allowing real-time CRUD testing and validation feedback without external client software.

---

## API Contract Catalog

| Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :---: |
| **GET** | `/api/saludo` | Health check endpoint returning system status | `200 OK` |
| **GET** | `/api/saludo/detallado` | Detailed platform architecture telemetry (JSON) | `200 OK` |
| **POST** | `/api/auth/login` | Authenticate credentials and receive Bearer JWT | `200 OK` |
| **GET** | `/api/pacientes` | List all healthcare records with dynamic status | `200 OK` |
| **GET** | `/api/pacientes/{id}` | Find healthcare record by ID | `200 OK` / `404` |
| **GET** | `/api/pacientes/anemia` | Filter patients with detected critical condition | `200 OK` |
| **POST** | `/api/pacientes` | Register new healthcare record (`@Valid`) | `201 Created` / `400` |
| **PUT** | `/api/pacientes/{id}` | Update existing healthcare record | `200 OK` / `400` / `404` |
| **DELETE** | `/api/pacientes/{id}` | Delete healthcare record | `204 No Content` / `404` |
| **GET** | `/api/productos` | List all inventory products | `200 OK` |
| **GET** | `/api/productos/stock` | Named Query filter: products with minimum stock | `200 OK` |
| **GET** | `/api/productos/precio` | JPQL `@Query`: filter by price range (`min`, `max`) | `200 OK` |
| **GET** | `/api/productos/insuficientes` | Filter products with critical stock (`< 5`) | `200 OK` |
| **POST** | `/api/productos` | Register new inventory product | `201 Created` / `400` |
| **PUT** | `/api/productos/{id}` | Update existing inventory product | `200 OK` / `400` / `404` |
| **DELETE** | `/api/productos/{id}` | Delete product from inventory | `204 No Content` / `404` |
| **GET** | `/api/legacy/productos` | List products using manual `EntityManager` | `200 OK` |
| **POST** | `/api/legacy/productos` | Insert product using `entityManager.persist()` | `201 Created` |

---

## Developer Runbook & CLI Cheat Sheet

### 1. Environment Verification
```powershell
# Verify Java 17 LTS or higher
java -version

# Verify Maven Wrapper
.\mvnw.cmd -v
```

### 2. Compilation, Verification & Packaging
```powershell
# Clean build and compile all sources
.\mvnw.cmd clean compile

# Execute deterministic test suite (Unit & MockMvc Integration)
.\mvnw.cmd test

# Generate production-ready standalone executable JAR
.\mvnw.cmd package -DskipTests
```

### 3. Application Execution
```powershell
# Option A: In-Memory H2 Profile (Default, Zero-Setup, Instant Run)
.\mvnw.cmd spring-boot:run

# Option B: MySQL Relational Profile
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql

# Option C: Standalone Production Execution
java -jar target/enterprise-web-solutions-spring-boot-0.0.1-SNAPSHOT.jar
```

### 4. Remote Debugging Mode
Start the application with a remote JVM debugging socket on port **5005**:
```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```
*You can now attach any IDE debugger (VS Code Java Debugger, IntelliJ IDEA, or Eclipse) to `localhost:5005` to inspect breakpoints, memory heaps, and variables.*

### 5. Diagnostics & Operational Contingency
```powershell
# Check if port 8080 is in use
netstat -ano | findstr :8080

# Force-kill conflicting process on port 8080 in PowerShell
Stop-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess -Force

# Alternative CMD kill:
# taskkill /F /PID <PID_FOUND>

# Inspect dependency tree
.\mvnw.cmd dependency:tree
```

### 6. Smoke Tests with cURL (PowerShell / Windows CMD)
```powershell
# 1. Health check
curl.exe -i http://localhost:8080/api/saludo

# 2. List products (200 OK)
curl.exe -i http://localhost:8080/api/productos

# 3. Create valid product (201 Created)
curl.exe -i -X POST http://localhost:8080/api/productos `
 -H "Content-Type: application/json" `
 -d '{"codigo":"PROD-TEST","nombre":"Monitor 4K OLED","precio":1200.0,"stock":8,"categoria":"Monitores"}'

# 4. Trigger Bean Validation failure (400 Bad Request)
curl.exe -i -X POST http://localhost:8080/api/productos `
 -H "Content-Type: application/json" `
 -d '{"codigo":"","nombre":"","precio":-10.0,"stock":-2,"categoria":""}'

# 5. Query non-existent ID (404 Not Found)
curl.exe -i http://localhost:8080/api/productos/99999
```

---

## Interactive Interfaces

Once the application is running (`http://localhost:8080`):
- **Native Test Console:** [http://localhost:8080/index.html](http://localhost:8080/index.html)
- **OpenAPI 3 / Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **H2 Web Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console) *(JDBC URL: `jdbc:h2:mem:enterprise_db`, User: `sa`, Password: empty)*

---

## Technical Documentation & Engineering Transparency

For architectural specifications, domain entity modeling, and testing procedures:
- [Technical Documentation Index](docs/README.md): Master catalog of architecture and governance specifications.
- [Domain Extensibility & Architecture Runbook](docs/ARCHITECTURE_AND_EXTENSIBILITY_GUIDE.md): Developer runbook for rapid module modeling, DTOs, Bean Validation, and dual-persistence implementation.
- [API Contract Verification & Testing Guide](docs/API_TESTING_AND_VERIFICATION_GUIDE.md): Complete testing procedures with Thunder Client, VS Code REST Client (`docs/api-contracts.http`), and cURL.

---

## License
This project is licensed under the terms of the [MIT License](LICENSE).
