# Mid-Level Backend Interview Prep — Spring Boot Task API

A focused Spring Boot project covering the topics most commonly asked in mid-level backend interviews. Every file exists to teach a specific concept you'll need to explain.

## Quick Start

```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080` with an H2 in-memory database and seed data.

- **H2 Console:** http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:taskdb`, user: `sa`, no password)

## Running Tests

```bash
mvn clean test
```

33 tests across 3 layers: repository (integration), service (unit/Mockito), controller (MockMvc).

## Authentication

Register, then use the JWT token for all other requests.

```bash
# Register
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","email":"demo@test.com","password":"password123"}' | jq

# Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"password123"}' | jq -r .token)

# Use token
curl -s http://localhost:8080/api/tasks -H "Authorization: Bearer $TOKEN" | jq
```

## API Endpoints

| Method | Path | Description | Auth |
|--------|------|-------------|------|
| POST | `/api/auth/register` | Register new user | Public |
| POST | `/api/auth/login` | Login, get JWT | Public |
| **Tasks** |||
| GET | `/api/tasks` | List tasks (paginated) | User |
| GET | `/api/tasks/{id}` | Get task with comments and tags | User |
| POST | `/api/tasks` | Create task | User |
| PUT | `/api/tasks/{id}` | Update task (partial) | User |
| DELETE | `/api/tasks/{id}` | Soft-delete task | Admin |
| GET | `/api/tasks/status/{status}` | Filter by status | User |
| GET | `/api/tasks/filter?status=&priority=&assignee=` | Multi-filter | User |
| GET | `/api/tasks/overdue` | Overdue tasks | User |
| GET | `/api/tasks/reports/by-status` | Group-by report | User |
| **Comments** |||
| GET | `/api/tasks/{id}/comments` | List comments for task | User |
| POST | `/api/tasks/{id}/comments` | Add comment to task | User |
| DELETE | `/api/tasks/{id}/comments/{commentId}` | Delete comment | User |

## Study Map — What Each File Teaches

### Entity Layer
- **Task.java** — JPA entity, enums, `@OneToMany`, `@ManyToMany`, `@JoinTable`, soft delete pattern, `@CreationTimestamp`/`@UpdateTimestamp`
- **Comment.java** — `@ManyToOne(fetch = LAZY)`, `@JoinColumn`, bidirectional relationship
- **Tag.java** — `@ManyToMany(mappedBy)`, inverse side of relationship
- **User.java** — Security entity, BCrypt password, role enum

### Repository Layer
- **TaskRepository.java** — Derived queries, JPQL with optional filters, native SQL GROUP BY, JOIN FETCH (N+1 prevention), existence checks, pagination
- **CommentRepository.java** — Simple derived query with ordering
- **TagRepository.java** — Collection-based query (`findByNameIn`)

### Service Layer
- **TaskService.java** — `@Transactional(readOnly=true)` class-level, write methods override, constructor injection, DTO conversion, soft delete
- **CommentService.java** — Nested resource pattern (comment belongs to task)
- **AuthService.java** — Registration/login, BCrypt, JWT token generation

### Controller Layer
- **TaskController.java** — REST conventions (GET/POST/PUT/DELETE), `@Valid`, `@PathVariable`, `@RequestParam`, pagination/sorting, status codes (201/204)
- **CommentController.java** — Nested resource URL pattern (`/tasks/{id}/comments`)
- **AuthController.java** — Public endpoints, `@Valid` request validation

### Security
- **SecurityConfig.java** — `SecurityFilterChain`, stateless sessions, BCrypt, role-based access (`hasRole("ADMIN")`)
- **JwtAuthenticationFilter.java** — `OncePerRequestFilter`, extract/validate JWT from Authorization header
- **JwtUtil.java** — HMAC-SHA token generation/validation with JJWT
- **CustomUserDetailsService.java** — `UserDetailsService` implementation, loads user from DB

### Exception Handling
- **GlobalExceptionHandler.java** — `@RestControllerAdvice`, consistent error JSON (`timestamp`, `status`, `message`, `fieldErrors`)
- **ResourceNotFoundException.java** — Custom exception with proper HTTP 404 mapping

### DTOs
- **TaskDTO.java** — Entity to DTO conversion with computed `overdue` field, relationship mapping
- **CreateTaskRequest.java** — `@NotBlank`, `@NotNull`, `@Size` validation, `toEntity()` conversion
- **UpdateTaskRequest.java** — All-optional fields for partial updates

### Tests
- **TaskRepositoryTest.java** — `@DataJpaTest`, real H2 queries, pagination test
- **TaskServiceTest.java** — `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`, verify interactions
- **CommentServiceTest.java** — Mock nested resource operations
- **TaskControllerTest.java** — `@WebMvcTest`, `MockMvc`, `@MockBean`, JSON assertions
- **AuthControllerTest.java** — Validation error testing
- **JwtUtilTest.java** — Plain unit test with reflection for `@Value` fields
