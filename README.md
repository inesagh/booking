# Booking system

A backend system for managing unit availability, bookings, and payments, built with Java and Spring Boot.

## Getting Started

Before you begin, ensure you're using:

- **Java 21.0.8 (2025-07-15 LTS)**

### Prerequisites

- Java 21.0.8 LTS
- Docker + Docker Compose
- Gradle (wrapper included)

---
##  Step-by-Step Setup

### 1. Start PostgreSQL using Docker Compose
```bash
docker-compose up
```

### 2. Build the project
```bash
./gradlew clean build
```

### 3. Start the project
```bash
./gradlew bootRun 
```
### 4. Access the Swagger API Documentation
Open your browser and navigate to:
http://localhost:8080/swagger-ui/index.html 

> Note: 
> All tests are currently passing. If any test fails and you want to skip tests during build, use:
> ``` ./gradlew build -x test```

---

## Tech Stack
- Java 21
- Spring Boot
- PostgreSQL (via Docker)
- Liquibase
- Gradle
- Swagger (OpenAPI)
- Redis
- JUnit, Spring Test Framework, H2
