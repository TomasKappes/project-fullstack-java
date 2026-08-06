# PC Builder — AGENTS.md

## Commands (run from `backend/`)

| Action | Command |
|--------|---------|
| Build | `.\mvnw.cmd clean package` |
| Run dev server | `.\mvnw.cmd spring-boot:run` (port 8080) |
| Run all tests | `.\mvnw.cmd test` |
| Run single test | `.\mvnw.cmd test -Dtest=UsuarioServiceTest` |

No linter/formatter config is set up. Frontend has no build step — open `frontend/login.html` directly (served by WebStorm on port 63342 or any static server).

## Project structure

```
backend/          Spring Boot 3.3.3, Java 17, Maven
  src/main/java/com/tomas/backend/
    entity/           JPA entities
    repository/       Spring Data JPA
    service/          Business logic + compatibility validation
    controller/       REST endpoints
    DTOs/             Request/response DTOs (one sub-package per domain)
    mappers/          Entity ↔ DTO converters
    config/           SecurityConfig, CorsConfig, DataSeeder
    security/         JwtService, JwtAuthenticationFilter, CustomUserDetailsService
    enums/            Roles, EstadoPedido, TipoCategoria
    excetions/        Typo — package is `excetions`, not `exceptions`
  src/main/resources/
    application.properties   DB + JWT config
frontend/         Static HTML/CSS/JS (vanilla, no framework)
  login.html, register.html, menu.html
  js/       Login.js, Registrar.js, Build.js, Actualizar.js, Presupuestar.js
  css/      styles.css, stylesMenu.css
```

## Architecture notes

- **Auth flow**: `POST /auth/login` → returns JWT + userId. Frontend stores both in `localStorage`.
- **PC Build flow**: Frontend tracks selected components in `window.pcBuild` (client-side). On submit → `POST /pedidos/crear` with token → then `PUT /pedidos/confirmar/{id}`.
- **Compatibility validation**: `CompatibilidadService` enforces CPU/Motherboard brand matching (AMD↔AMD, Intel↔Intel) and requires CPU + Motherboard + RAM minimum.

## Quirks & gotchas

- **Package typo**: `excetions` is intentional but misspelled — do not "fix" it without updating all imports across the codebase.
- **Lombok is inconsistent**: DTOs under `DTOs/auth/` use `@Data` / `@AllArgsConstructor` / `@NoArgsConstructor`. All other classes (entities, other DTOs, mappers, services) use hand-written getters/setters/constructors. Follow existing pattern per file.
- **CategoriasController returns DTOs** — `CategoriaService` was migrated to `CategoriaResponseDTO`/`CategoriaCreateDTO`/`CategoriaRequestDTO` and uses `CategoriaMapper`. It no longer returns raw `Categoria` entities.
- **Wrong HTTP verbs in ProductosController**: Endpoints like `POST /actualizar/{id}`, `POST /activar/{id}`, `POST /desactivar/{id}` should semantically be `PUT` / `PATCH` but currently use `POST`. Follow the existing convention when editing.
- **CORS**: `CorsConfig` defines a `CorsConfigurationSource` bean and `SecurityConfig` now wires it via `.cors(cors -> cors.configurationSource(...))`.
- **Lombok version**: Project uses Lombok 1.18.46 (for JDK 25 compat), declared in both dependency and `maven-compiler-plugin` annotation processor path.
- **Byte Buddy**: `maven-surefire-plugin` uses `-Dnet.bytebuddy.experimental=true` for JDK 25 compatibility with Mockito.
- **JWT secret key**: In `application.properties` as plaintext (`security.jwt.secret-key=myverysecuresecretkeyforjwttokens123`). Not production-safe. `JwtService` uses `secretKey.getBytes()` + `parserBuilder()` (deprecated in jjwt 0.12+).
- **Database**: MySQL on `localhost:3306/projectdb` with `ddl-auto=create-drop` — all data resets on restart. App must be running before frontend can log in.
- **menu.html has hardcoded product data** — it does not fetch from the API (`GET /productos` is implemented but unused by frontend).

## Testing

- Test strategy lives in `ESTRATEGIA_DE_TESTING.md` (FASE 1 = services, FASE 2 = security, FASE 3 = controllers).
- Existing test files: `BackendApplicationTests` (context load), `UsuarioServiceTest` (16), `CategoriaServiceTest` (13), `AuthServiceTest` (6), `CompatibilidadServiceTest` (12), `ProductoServiceTest` (32). FASE 1 services coverage: only `PedidoServiceTest` still pending.
- Run a subset: `.\mvnw.cmd test "-Dtest=Clase1,Clase2"` (quotes required in PowerShell because of the comma).
- **JDK note**: `java` in PATH is a JRE 1.8 — set `$env:JAVA_HOME = "C:\Users\GScom\.jdks\temurin-17.0.19"` before running Maven.
- Integration tests require a running MySQL instance matching `application.properties`.