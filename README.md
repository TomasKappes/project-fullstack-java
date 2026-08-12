# 🖥️ PC Builder — Aplicación Full Stack

Aplicación full stack de e-commerce de componentes de PC: los usuarios se registran, inician sesión y arman configuraciones de PC seleccionando componentes compatibles.

El proyecto combina un **backend robusto en Java / Spring Boot** (arquitectura por capas, seguridad JWT, DTOs, manejo global de excepciones y 130 tests automatizados) con un **frontend funcional en JavaScript vanilla** (sin frameworks ni build step).

Es un proyecto de portafolio: más allá de que funcione, el objetivo es documentar las **decisiones técnicas tomadas y sus porqués**, incluyendo la deuda técnica aceptada con honestidad.

---

## 🚀 Tecnologías

### Backend

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17 | Lenguaje |
| Spring Boot | 3.3.3 | Framework base |
| Spring Security | (incluido en Boot 3.3.3) | Autenticación y protección de endpoints |
| jjwt | 0.11.5 | Generación y validación de tokens JWT |
| BCrypt | (Spring Security) | Hasheo de contraseñas |
| Spring Data JPA / Hibernate | (incluido en Boot 3.3.3) | Persistencia |
| MySQL | — | Base de datos (`localhost:3306/projectdb`) |
| Maven | wrapper incluido (`mvnw`) | Build y ejecución de tests |
| JUnit 5 + Mockito + spring-security-test | — | Tests unitarios, de seguridad y de controladores |
| Lombok | 1.18.46 | Solo en algunos DTOs (ver "Convenciones") |

### Frontend

* HTML5 + CSS3 (tema oscuro unificado con variables CSS)
* JavaScript vanilla (sin frameworks, sin build step)
* Bootstrap 5.3 + Bootstrap Icons 1.11.1 (vía CDN)

---

## 🧩 Funcionalidades principales

* Registro y login de usuarios con JWT y contraseñas hasheadas con BCrypt.
* **Flujo de armado de PC**: crear pedido (`PRESUPUESTADO`) → confirmar pedido (`CONFIRMADO`, descuenta stock).
* **Validación de compatibilidad** (`CompatibilidadService`): CPU ↔ Motherboard por marca (AMD ↔ AMD, Intel ↔ Intel) y mínimo obligatorio de CPU + Motherboard + RAM.
* CRUD de productos y categorías: creación, actualización, activar/desactivar, ajuste de stock y precio.
* DTOs + mappers que desacoplan las entidades JPA de la API.
* Manejo global de excepciones con respuestas estructuradas.
* `DataSeeder`: si las tablas están vacías, crea las categorías desde el enum `TipoCategoria` y 40 productos de ejemplo.
* Frontend: login, registro y menú de armado con validación visual de inputs, spinners de carga, carrito con precios, badge contador y nombre real del usuario cargado desde la API.

---

## 🏗️ Arquitectura

El backend sigue una arquitectura por capas dentro de `com.tomas.backend`:

```
controller → service → repository → entity
                 ↕
             DTOs + mappers
```

* **entity / repository** → modelo de datos y acceso a MySQL.
* **service** → lógica de negocio y validaciones de compatibilidad.
* **controller** → endpoints REST.
* **DTOs / mappers** → contrato de la API desacoplado de las entidades.
* **security** → `JwtService`, `JwtAuthenticationFilter`, `CustomUserDetailsService`.
* **config** → `SecurityConfig`, `CorsConfig`, `DataSeeder`.
* **enums / excetions** → enums de dominio y excepciones propias (el paquete `excetions` conserva un typo intencional; ver "Convenciones").

---

## 🔐 Seguridad

* Autenticación basada en JWT (jjwt 0.11.5) validado por request en `JwtAuthenticationFilter`.
* Contraseñas hasheadas con BCrypt.
* Endpoints protegidos con Spring Security; `/auth/register` y `/auth/login` son públicos.
* `JwtService` es **defensivo por contrato**: un token expirado o inválido nunca genera un HTTP 500 (ver "Decisiones técnicas").
* ⚠️ La clave secreta del JWT está en `application.properties` en texto plano: aceptable para un proyecto de portafolio, **no apto para producción** (ver "Mejoras futuras").

---

## 📡 API — Endpoints principales

Base: `http://localhost:8080`

### Autenticación (`/auth`)

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/auth/register` | Registro de usuario (público) |
| POST | `/auth/login` | Login; devuelve JWT + `usuarioId` (público) |

### Usuarios (`/users`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/users` | Listar todos los usuarios |
| GET | `/users/{id}` | Obtener usuario por ID |
| PUT | `/users/Update/{id}` | Actualizar usuario |
| DELETE | `/users/Delete/{id}` | Eliminar usuario |

### Productos (`/productos`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/productos` | Listar todos los productos |
| GET | `/productos/id/{id}` | Obtener producto por ID |
| GET | `/productos/{idCategoria}` | Listar productos por categoría |
| GET | `/productos/activo/{id}` | Consultar si un producto está activo |
| POST | `/productos/crear` | Crear producto |
| POST | `/productos/actualizar/{id}` | Actualizar producto |
| POST | `/productos/activar/{id}` / `/desactivar/{id}` | Activar / desactivar producto |
| POST | `/productos/stock/aumento/{id}/{cantidad}` | Aumentar stock |
| POST | `/productos/stock/disminuir/{id}/{cantidad}` | Disminuir stock |
| PUT | `/productos/actualizarPrecio/{id}/{precio}` | Actualizar precio |

### Categorías (`/categorias`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/categorias` | Listar todas las categorías |
| GET | `/categorias/id/{id}` | Obtener categoría por ID |
| POST | `/categorias/crear` | Crear categoría |
| POST | `/categorias/actualizar/{id}` | Actualizar categoría |
| POST | `/categorias/activar/{id}` / `/desactivar/{id}` | Activar / desactivar categoría |

### Pedidos (`/pedidos`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/pedidos/id/{id}` | Obtener pedido por ID |
| POST | `/pedidos/crear` | Crear pedido (estado `PRESUPUESTADO`, valida compatibilidad) |
| PUT | `/pedidos/agregar-producto/{id}` | Agregar un producto al pedido |
| PUT | `/pedidos/confirmar/{id}` | Confirmar pedido (estado `CONFIRMADO`, descuenta stock) |

> Nota: en `ProductosController` y `CategoriasController` algunos endpoints usan `POST` donde semánticamente correspondería `PUT`/`PATCH`. Es una convención heredada que se mantiene por consistencia (ver "Decisiones técnicas").

---

## 🧪 Testing

**Estado actual: 130 tests automatizados, BUILD SUCCESS.** La estrategia completa y el registro de decisiones viven en [`ESTRATEGIA_DE_TESTING.md`](ESTRATEGIA_DE_TESTING.md).

| Fase | Alcance | Tests | Clases |
|---|---|---|---|
| **FASE 1** | Unitarios de servicios | 96 | `UsuarioServiceTest` (16), `CategoriaServiceTest` (13), `AuthServiceTest` (6), `CompatibilidadServiceTest` (12), `ProductoServiceTest` (32), `PedidoServiceTest` (17) |
| **FASE 2** | Seguridad | 13 | `JwtServiceTest` (6), `CustomUserDetailsServiceTest` (2), `JwtAuthenticationFilterTest` (5) |
| **FASE 3** | Controladores | 20 | `AuthControllerTest` (3), `PedidosControllerTest` (5), `ProductosControllerTest` (5), `UsuariosControllerTest` (4), `CategoriasControllerTest` (3) |
| — | Carga de contexto | 1 | `BackendApplicationTests` |

Patrón usado en los controller tests: `@WebMvcTest` + `@Import({SecurityConfig.class, CorsConfig.class})` + `@MockBean` de servicios + `@WithMockUser` (el filtro JWT se mockea como pass-through).

```bash
# desde backend/
.\mvnw.cmd test                                  # todos los tests
.\mvnw.cmd test "-Dtest=UsuarioServiceTest"      # una clase (comillas requeridas en PowerShell)
```

Salvo `BackendApplicationTests` (que levanta el contexto completo y necesita MySQL corriendo), el resto de los tests funciona sin base de datos.

---

## ⚖️ Decisiones técnicas

Cada decisión documenta **qué** se decidió y **por qué**. El registro detallado también vive en `ESTRATEGIA_DE_TESTING.md` §8.4.

1. **`JwtService` defensivo por contrato (2026-08-08)** — `extractUsername()` devuelve `null` ante `JwtException`/`IllegalArgumentException` e `isTokenValid()` devuelve `false` en vez de lanzar.
   *Por qué*: fue un bug real de producción — un JWT expirado causaba HTTP 500 en `JwtAuthenticationFilter` en vez de 401/403, y el frontend no podía distinguir "sesión expirada" de "servidor caído". No se debe revertir al comportamiento de "dejar que jjwt lance la excepción".

2. **Atomicidad transaccional en `reCrearPedido`** — `@Transactional` + `ApiException` extendiendo `RuntimeException`: si `crear()` falla, la transacción revierte y el pedido original queda `PRESUPUESTADO`.
   *Por qué*: antes el pedido original se cancelaba en memoria sin persistir; si la creación del nuevo fallaba, esa cancelación se perdía y el estado quedaba inconsistente.

3. **Deuda aceptada — race condition en `confirmarPedido` (TOCTOU)** — el guard `if (estado == CONFIRMADO)` protege contra llamadas secuenciales, pero no concurrentes: dos requests paralelos pueden leer `PRESUPUESTADO` a la vez y descontar stock dos veces. No hay `@Version` ni `@Lock`.
   *Por qué se acepta*: es un proyecto de portafolio/MVP, sin ventas reales. Si se llevara a producción, el primer paso sería `@Version` (optimistic locking).

4. **Deuda aceptada — catálogo frontend hardcodeado** — `menu.html` tiene 40 productos hardcodeados con `data-id` 1–40 que dependen del orden de inserción del `DataSeeder`. El backend ya expone `GET /productos`, pero el frontend no lo consume.
   *Por qué se acepta*: deshardcodearlo requería tocar muchas piezas (DTO, render dinámico, `Build.js`) y el `DataSeeder` ya funciona como fuente de verdad del catálogo. No es prioridad en un proyecto de portafolio.

5. **Deuda diferida — test de integración de `PedidoService`** — el flujo crear → confirmar → descuento de stock solo está cubierto por unit tests con mocks.
   *Por qué se difiere*: los 130 tests actuales cubren la lógica de negocio y el contrato HTTP de los controladores; el riesgo residual es bajo para un proyecto de portafolio.

6. **Deuda aceptada — `System.out.println` en `PedidosController.editarPedido`** — log de debug a consola.
   *Por qué se acepta*: se agregó para debuggear un bug y no genera impacto negativo. Si se retoma el proyecto, reemplazar por SLF4J/Logback.

7. **Refactor de UI del frontend (2026-08-10/11)** — tema oscuro gaming unificado con variables CSS en `:root`, mensajes extraídos a `css/mensajes.css` (DRY), navbar reemplazada por badges flotantes con `position: fixed` (para no cortar la imagen de fondo), validación visual de inputs, spinners de carga, carrito con precios y nombre real del usuario cargado desde `GET /users/{id}`.
   *Por qué el "fetch del perfil al cargar"*: el `AuthResponse` solo devuelve token + `usuarioId`, así que el nombre se obtiene en una segunda llamada al entrar al menú.

8. **Convenciones del proyecto (no "arreglar" sin contexto)**
   * Paquete `excetions`: el typo es intencional a estas alturas; corregirlo exige actualizar todos los imports del codebase.
   * Lombok inconsistente: los DTOs bajo `DTOs/auth/` usan `@Data`/`@AllArgsConstructor`/`@NoArgsConstructor`; el resto del proyecto usa getters/setters manuales. Se sigue el patrón existente por archivo.
   * Verbos HTTP "incorrectos" en `ProductosController` (`POST /actualizar/{id}` debería ser `PUT`/`PATCH`): se mantienen por convención existente al editar.

---

## 📌 Deuda técnica conocida

Resumen de la deuda aceptada y qué se haría si se retoma el proyecto:

| Deuda | Por qué se acepta hoy | Si se retoma |
|---|---|---|
| Race condition en `confirmarPedido` (TOCTOU, sin `@Version`/`@Lock`) | MVP de portafolio sin concurrencia real | Implementar `@Version` (optimistic locking) como primer paso antes de producción |
| Catálogo de `menu.html` hardcodeado (`data-id` 1–40 atados al orden del `DataSeeder`) | El `DataSeeder` ya es la fuente de verdad; deshardcodear tocaba DTO + render + `Build.js` | Render dinámico consumiendo `GET /productos` |
| Sin test de integración del flujo crear → confirmar → stock | Los 130 tests cubren lógica y contrato HTTP; riesgo residual bajo | Test de integración con base de datos real |
| `System.out.println` en `PedidosController.editarPedido` | Debug de un bug, sin impacto negativo | Reemplazar por SLF4J/Logback |
| Secreto JWT en texto plano en `application.properties` | Proyecto de portafolio, sin despliegue público | Mover a variable de entorno |

---

## 🚧 Estado del proyecto

**Estado actual:**

* ✅ Backend completo: capas, seguridad JWT, DTOs, manejo de excepciones y datos semilla.
* ✅ Testing completo en 3 fases: 130 tests automatizados, BUILD SUCCESS.
* ✅ Frontend funcional y refaccionado: flujo login → menú → armado → confirmación, con tema oscuro unificado, carrito con precios y nombre real del usuario.
* ✅ Decisiones técnicas y deuda técnica documentadas (`ESTRATEGIA_DE_TESTING.md` §8.4, `AGENTS.md`, este README).

**Mejoras futuras (honestas, en este orden):**

* Deshardcodear el catálogo del frontend consumiendo `GET /productos`.
* Implementar roles USER / ADMIN.
* Test de integración del flujo de pedidos (crear → confirmar → stock).
* Mover el secreto JWT a una variable de entorno.
* Deploy de la aplicación.

---

## 🛠️ Cómo correr el proyecto

**Requisitos:** JDK 17 y MySQL corriendo en `localhost:3306`.

### Backend (desde `backend/`)

```bash
.\mvnw.cmd spring-boot:run    # levanta la API en http://localhost:8080
```

> ⚠️ La configuración usa `ddl-auto=create-drop`: **todos los datos se resetean al reiniciar**. El `DataSeeder` vuelve a crear las categorías y los 40 productos en cada arranque.

### Frontend

No hay build step: alcanza con servir la carpeta `frontend/` con cualquier servidor estático y abrir `login.html` (por ejemplo, el servidor integrado de WebStorm o `python -m http.server`). El frontend consume la API en `http://localhost:8080`, así que el backend debe estar corriendo antes de iniciar sesión.

---

## 🎯 Objetivo

Proyecto de portafolio de un desarrollador junior en camino a semi-senior. Lo que empezó como un ejercicio de backend hoy cubre el ciclo completo de una aplicación real:

* Arquitectura por capas y separación de responsabilidades.
* Seguridad en aplicaciones web (JWT, BCrypt, filtros, manejo de tokens expirados).
* Buenas prácticas en APIs REST (DTOs, mappers, excepciones estructuradas).
* Testing automatizado por fases (servicios, seguridad, controladores).
* Frontend funcional sin frameworks.
* **Documentación de decisiones técnicas y deuda técnica con sus porqués** — porque saber qué se decidió, y por qué, es tan importante como el código.
