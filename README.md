# 🎓 Student Management System

A full-stack **Spring Boot MVC** web application for managing student registrations,
built with **Hibernate/JPA**, **H2/MySQL** database, and **Thymeleaf** templating.

---

## 📐 Architecture (MVC Pattern)

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT (Browser)                     │
└──────────────────────────┬──────────────────────────────────┘
                           │  HTTP Requests
┌──────────────────────────▼──────────────────────────────────┐
│                    CONTROLLER LAYER                          │
│  ┌──────────────────────┐  ┌──────────────────────────────┐ │
│  │  StudentViewController│  │   StudentRestController      │ │
│  │  GET  /              │  │   POST /api/students         │ │
│  │  POST /students      │  │   GET  /api/students         │ │
│  └──────────┬───────────┘  └──────────────┬───────────────┘ │
└─────────────┼────────────────────────────┼──────────────────┘
              │                            │
┌─────────────▼────────────────────────────▼──────────────────┐
│                     SERVICE LAYER                            │
│           StudentService / StudentServiceImpl                │
│   - Business logic          - Duplicate email check         │
│   - Input normalisation     - Transaction management        │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                   REPOSITORY LAYER                           │
│              StudentRepository (JpaRepository)              │
│   - findAll()   - findById()   - existsByEmail()            │
└──────────────────────────────┬──────────────────────────────┘
                               │  JPA / Hibernate
┌──────────────────────────────▼──────────────────────────────┐
│                      DATABASE                                │
│              H2 (dev) / MySQL (production)                  │
│                    students table                            │
└─────────────────────────────────────────────────────────────┘

VIEW LAYER (Thymeleaf)
  templates/index.html → Registration form + Student list table
  static/css/style.css → Styling
```

---

## 🗂️ Project Structure

```
student-management/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/student/management/
    │   │   ├── StudentManagementApplication.java   ← Entry point
    │   │   ├── model/
    │   │   │   └── Student.java                   ← @Entity (MODEL)
    │   │   ├── repository/
    │   │   │   └── StudentRepository.java          ← JPA Repository
    │   │   ├── service/
    │   │   │   ├── StudentService.java             ← Interface
    │   │   │   ├── StudentServiceImpl.java         ← Business Logic
    │   │   │   └── DuplicateEmailException.java    ← Custom Exception
    │   │   └── controller/
    │   │       ├── StudentViewController.java      ← MVC (HTML pages)
    │   │       └── StudentRestController.java      ← REST API (JSON)
    │   └── resources/
    │       ├── application.properties              ← Config (DB, JPA, etc.)
    │       ├── templates/
    │       │   └── index.html                     ← Thymeleaf (VIEW)
    │       └── static/css/
    │           └── style.css                      ← Stylesheet
    └── test/java/com/student/management/
        ├── StudentManagementIntegrationTest.java   ← Integration tests
        └── service/
            └── StudentServiceImplTest.java         ← Unit tests
```

---

## 🚀 How to Run

### Prerequisites

- Java 17+
- Maven 3.6+

### Switch to MySQL

1. Uncomment the MySQL dependency in `pom.xml`
2. Update `application.properties`:

```properties
# Comment out H2 section, uncomment MySQL section:
spring.datasource.url=jdbc:mysql://localhost:3306/studentdb?createDatabaseIfNotExist=true
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
```

---

## 🔌 REST API Reference

| Method | Endpoint                        | Description        | Request Body |
| ------ | ------------------------------- | ------------------ | ------------ |
| `POST` | `/api/students`                 | Create new student | JSON Student |
| `GET`  | `/api/students`                 | List all students  | —            |
| `GET`  | `/api/students/{id}`            | Get by ID          | —            |
| `GET`  | `/api/students/course/{course}` | Get by course      | —            |

### Example — Create Student (POST)

```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice Smith","email":"alice@example.com","course":"Computer Science"}'
```

Response (`201 Created`):

```json
{
  "id": 1,
  "name": "Alice Smith",
  "email": "alice@example.com",
  "course": "Computer Science"
}
```

### Example — Duplicate Email (400)

```json
{
  "error": "Duplicate Email",
  "message": "A student with email 'alice@example.com' is already registered.",
  "email": "alice@example.com"
}
```

---

## ✅ Run Tests

```bash
mvn test
```

---

## 🔑 Key Design Decisions

| Decision                        | Rationale                                                |
| ------------------------------- | -------------------------------------------------------- |
| PRG pattern (Post-Redirect-Get) | Prevents duplicate form submissions on browser refresh   |
| Constructor injection           | Explicit, testable, recommended over field injection     |
| Interface + Impl for Service    | Easier to mock in unit tests; follows SOLID principles   |
| `@Transactional(readOnly=true)` | Performance optimisation for SELECT queries              |
| Email normalised to lowercase   | Prevents `Alice@TEST.com` vs `alice@test.com` duplicates |
| H2 default, MySQL optional      | Zero-config for dev/grading; production-ready with MySQL |
