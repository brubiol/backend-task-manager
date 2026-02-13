# Interview Cheat Sheet (Everything Except `practice/`)

This is a concise, interview‑focused summary of the project in `src/main/java/com/example/demo`, excluding `practice/`.

**Tech stack**
- Spring Boot REST API
- Spring Data JPA + Hibernate
- Spring Security + JWT
- Validation via `jakarta.validation`
- H2/SQL‑style persistence (implied by JPA usage)

**High‑level flow**
- Controller receives HTTP request → Service handles business logic → Repository talks to DB → DTOs shape API responses
- Security layer validates JWT and authorizes access before controllers run
- Global exception handler converts errors into consistent JSON

---

**Entry Point**

`DemoApplication.java`
- `@SpringBootApplication` bootstraps Spring Boot.
- `main(...)` starts the app.

What interviewers ask:
- “What does `@SpringBootApplication` do?”
- “Where does the application start?”

---

**Entities (Database Models)**

`Task.java`
- Table: `tasks`
- Key fields: `id`, `title`, `description`, `status`, `priority`, `assignee`, `dueDate`, `createdAt`, `updatedAt`, `deleted`
- Soft delete: `deleted` flag instead of removing rows
- Relationships:
  - `@OneToMany` comments (Task → Comment)
  - `@ManyToMany` tags (Task ↔ Tag) via `task_tags` join table
- Enums stored as strings with `@Enumerated(EnumType.STRING)`

Interview Q/A:
- “Why use `EnumType.STRING`?” → Safer than ordinal if enum order changes.
- “What is soft delete?” → Mark as deleted, keep row for audit/history.
- “How do you avoid N+1 when loading tags/comments?” → Use `JOIN FETCH` or entity graphs.

`Comment.java`
- Table: `comments`
- Fields: `id`, `content`, `author`, `task`, `createdAt`
- Relationship: `@ManyToOne(fetch = LAZY)` to Task

Interview Q/A:
- “Why LAZY on `@ManyToOne`?” → Avoid loading parent unless needed.
- “Where is the foreign key?” → `comments.task_id`

`Tag.java`
- Table: `tags`
- Fields: `id`, `name` (unique)
- Relationship: `@ManyToMany(mappedBy = "tags")` to Task

Interview Q/A:
- “Who owns the many‑to‑many?” → `Task` owns it because it defines `@JoinTable`.
- “Why join table?” → Many‑to‑many needs a third table to map pairs.

`User.java`
- Table: `users`
- Fields: `id`, `username` (unique), `email` (unique), `password`, `role`, `createdAt`
- Role stored as enum string

Interview Q/A:
- “Why store password hashed?” → Never store plaintext, use BCrypt.

---

**DTOs (API Request/Response Models)**

`TaskDTO`
- Read model for API responses
- Contains computed `overdue` flag
- Includes `comments` and `tagNames` (not raw entities)

---

## Cheap AWS Deploy (EC2 + Docker Compose)

Goal: keep AWS cost under ~$25/month by avoiding ALB, NAT, Redis, and RDS.

**What you run**
- 1 small EC2 instance (t4g.micro or t3.micro)
- Docker Compose running the app + Postgres

**Steps (high level)**
1. Launch EC2 (Amazon Linux 2023), open inbound `8080` in the security group.
2. Install Docker + docker compose plugin.
3. Clone this repo on the instance.
4. Create `.env.aws-lite` from `.env.aws-lite.example` with real secrets.
5. Run:

```bash
docker compose -f docker-compose.aws-lite.yml --env-file .env.aws-lite up -d --build
```

**Why this is cheaper**
- No ALB (~$16/mo), no NAT (~$32/mo), no Redis (~$10–15/mo), no RDS (~$20+).

`CommentDTO`
- Read model for comments
- Includes `taskId` rather than full Task object

`CreateTaskRequest` / `UpdateTaskRequest`
- Request models with validation annotations
- `CreateTaskRequest.toEntity()` converts to Task

`CreateCommentRequest`
- Request model for comments

`AuthRequest` / `RegisterRequest` / `AuthResponse`
- Auth request/response payloads for login/register

`TaskStatusReport`
- DTO for aggregation results (status + count)

Interview Q/A:
- “Why use DTOs instead of entities in controllers?” → Security, stability, avoid exposing internals.
- “What do validation annotations do?” → Enforce input constraints before service layer.

---

**Repositories (Data Access)**

`TaskRepository`
- Extends `JpaRepository<Task, Long>`
- Supports soft delete queries (`findByIdAndDeletedFalse`)
- Uses `@Query` with `JOIN FETCH` to load tags/comments in one query
- Supports pagination and dynamic filters
- Includes native SQL for reporting

Interview Q/A:
- “What’s the difference between JPQL and SQL?” → JPQL uses entity names/fields, SQL uses tables/columns.
- “What does `JOIN FETCH` solve?” → N+1 query problem.
- “Why return `Page<T>`?” → Built‑in pagination and metadata.

`CommentRepository`
- `findByTaskIdOrderByCreatedAtDesc` shows derived query naming

`TagRepository`
- `findByNameIn(Set<String>)` is used to resolve tags when creating tasks

`UserRepository`
- `findByUsername`, `existsByUsername`, `existsByEmail` support auth logic

HackerRank‑style tasks they could ask:
- Write a derived query method with multiple conditions
- Add a `@Query` filter with optional params
- Add pagination + sorting
- Add a native query for reporting

---

**Services (Business Logic)**

`TaskService`
- `createTask` converts request → entity, resolves tags
- `getTaskById` fetches task and maps to DTO
- `getAllTasks` paginated list of non‑deleted tasks
- `updateTask` partial updates
- `deleteTask` soft delete
- `getTasksWithFilters` uses dynamic JPQL
- `getOverdueTasks` uses due date filter
- `getTaskCountByStatus` maps native query results to DTO

Interview Q/A:
- “Why use `@Transactional(readOnly = true)` on service?” → Performance optimization for read operations.
- “Why not return entities from service?” → Use DTOs for clean API boundaries.
- “How do you handle soft delete?” → Filter on `deleted=false` and set flag instead of delete.

`CommentService`
- Adds comment to existing task
- Validates task existence before listing comments
- Deletes comment by ID

`AuthService`
- Registers user with hashed password
- Logs in user using `AuthenticationManager`
- Issues JWT on success

---

**Controllers (HTTP Endpoints)**

`TaskController`
- CRUD endpoints under `/api/tasks`
- Supports pagination, sorting, filters
- Reporting endpoint `/api/tasks/reports/by-status`

`CommentController`
- Nested routes under `/api/tasks/{taskId}/comments`

`AuthController`
- `/api/auth/register` and `/api/auth/login`

Interview Q/A:
- “Why use `ResponseEntity`?” → Control status codes and response body.
- “Why use `@Valid` on requests?” → Trigger validation before service logic.

---

**Security (JWT)**

`SecurityConfig`
- Stateless session with JWT
- Permits `/api/auth/**`, `/h2-console/**`, Swagger
- Requires `ADMIN` role for DELETE on tasks
- Adds JWT filter before `UsernamePasswordAuthenticationFilter`

`JwtUtil`
- Generates and validates JWT
- Stores username + role as claims

`JwtAuthenticationFilter`
- Extracts `Authorization: Bearer <token>`
- Validates token
- Loads user from DB and sets `SecurityContext`

`CustomUserDetailsService`
- Loads user by username for authentication

`CustomUserDetails`
- Maps `User` to Spring Security’s `UserDetails`

Interview Q/A:
- “Why is JWT stateless?” → No server session; token carries claims.
- “What does the filter do?” → Authenticates requests before controllers.
- “How are roles mapped?” → `ROLE_` prefix via `SimpleGrantedAuthority`.

---

**Exceptions**

`ResourceNotFoundException`
- Thrown when entity isn’t found → mapped to 404

`GlobalExceptionHandler`
- Converts exceptions to consistent JSON responses
- Handles validation errors, auth errors, and unknown errors

Interview Q/A:
- “Why use `@RestControllerAdvice`?” → Centralized error handling.
- “How do you return validation errors?” → Build a map of field → message.

---

**Common Interview Questions You Can Answer From This Repo**

1. “Explain the controller‑service‑repository pattern.”
- Controllers handle HTTP, services handle business logic, repositories handle DB queries.

2. “Why DTOs?”
- Avoid leaking entity structure, control API responses, and reduce coupling.

3. “How do you avoid N+1 problems?”
- Use `JOIN FETCH` or entity graphs to load relationships in one query.

4. “What is soft delete and why use it?”
- Mark `deleted=true` to keep history and avoid hard deletes.

5. “Why store enums as strings?”
- Safer for migrations; ordinal can break if you reorder enum values.

6. “What is the difference between JPQL and native SQL?”
- JPQL uses entities and fields; native SQL uses table/column names.

7. “How does JWT authentication flow work?”
- Login returns token → client sends Bearer token → filter validates and sets auth.

8. “How does validation work?”
- `@Valid` triggers `jakarta.validation` annotations on DTOs before hitting service.

9. “How is pagination done?”
- Controllers accept page/size/sort and pass a `Pageable` to repository.

10. “How do you handle authorization?”
- Security config + role‑based rules (DELETE requires ADMIN role).

---

**Potential Interview‑Level Improvements (Talking Points)**

- Add unique constraint validation at service level for tags
- Add entity graphs or fetch joins for comments/tags where needed
- Add soft‑delete filters globally (e.g., Hibernate filters)
- Add integration tests for security and repository queries
- Avoid duplicate method signatures (if present, keep only one `getTaskById`)

---

If you want, I can also generate:
- A 1‑page printable version
- Flashcard‑style Q/A
- “30‑second summary” answers per file
