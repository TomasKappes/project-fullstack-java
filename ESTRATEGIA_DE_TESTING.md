# Estrategia de Testing — PC Builder

| Campo | Valor |
|-------|-------|
| **Proyecto** | PC Builder (Spring Boot 3.3.3 + Java 17 + Vanilla JS) |
| **Versión del documento** | 1.0 |
| **Fecha** | 30 de julio de 2026 |
| **Responsable** | QA Engineering |
| **Audiencia** | Equipo de desarrollo |

---

## Índice

1. [Enfoque general de testing](#1-enfoque-general-de-testing)
2. [Stack tecnológico y herramientas](#2-stack-tecnológico-y-herramientas)
3. [Matriz de prioridades](#3-matriz-de-prioridades)
4. [Convenciones de nomenclatura y estructura](#4-convenciones-de-nomenclatura-y-estructura)
5. [Plan detallado de pruebas — ALTA PRIORIDAD](#5-plan-detallado-de-pruebas--alta-prioridad)
   - [5.1 UsuarioServiceTest](#51-usuarioservicetest)
   - [5.2 CompatibilidadServiceTest](#52-compatibilidadservicetest)
   - [5.3 PedidoServiceTest](#53-pedidoservicetest)
   - [5.4 ProductoServiceTest](#54-productoservicetest)
   - [5.5 CategoriaServiceTest](#55-categoriaservicetest)
   - [5.6 AuthServiceTest](#56-authservicetest)
6. [Plan detallado de pruebas — PRIORIDAD MEDIA](#6-plan-detallado-de-pruebas--prioridad-media)
   - [6.1 JwtServiceTest](#61-jwtservicetest)
   - [6.2 JwtAuthenticationFilterTest](#62-jwtauthenticationfiltertest)
   - [6.3 CustomUserDetailsServiceTest](#63-customuserdetailsservicetest)
7. [Plan detallado de pruebas — PRIORIDAD BAJA](#7-plan-detallado-de-pruebas--prioridad-baja)
   - [7.1 PedidosDetalleServiceTest](#71-pedidosdetalleservicetest)
   - [7.2 Controladores (integración)](#72-controladores-integración)
8. [Cobertura actual y brechas](#8-cobertura-actual-y-brechas)
9. [Instrucciones de ejecución](#9-instrucciones-de-ejecución)
10. [Buenas prácticas para el equipo](#10-buenas-prácticas-para-el-equipo)
11. [Glosario](#11-glosario)

---

## 1. Enfoque general de testing

El proyecto actual tiene **2 archivos de prueba** y una cobertura casi nula. Esta estrategia busca establecer una base sólida de pruebas siguiendo la **pirámide de testing**:

```
        ╱╲
       ╱  ╲          ← Tests de integración (controladores)
      ╱    ╲            (Prioridad baja — 5-10 tests)
     ╱      ╲
    ╱────────╲
   ╱          ╲       ← Tests de seguridad (filtros JWT, tokens)
  ╱            ╲         (Prioridad media — 10-15 tests)
 ╱──────────────╲
╱                  ╲   ← Tests unitarios de servicios (lógica de negocio)
╱                    ╲   (Prioridad alta — 60-80+ tests)
━━━━━━━━━━━━━━━━━━━━━━
```

### 1.1 Enfoque por capa

| Capa | Tipo de test | Framework | Mocking |
|------|-------------|-----------|---------|
| **Services** | Unitario | JUnit 5 + Mockito | Repositorios, Mappers, Servicios dependientes |
| **Security** | Unitario (+ integración ligera) | JUnit 5 + Mockito | UserDetailsService, Repository, Request/Response |
| **Controllers** | Integración | `@WebMvcTest` + MockMvc | Servicios completos |
| **Config** | Integración (opcional) | `@SpringBootTest` | — |

### 1.2 Regla de oro

> Cada método público de un **Service** debe tener al menos **un test de happy path** y **un test de cada escenario de error** (excepción documentada en el código).

---

## 2. Stack tecnológico y herramientas

| Herramienta | Versión | Propósito |
|-------------|---------|-----------|
| **JUnit 5** (Jupiter) | 5.10+ | Framework base de testing |
| **Mockito** | 5.x | Mocking de dependencias |
| **Mockito Extension** | `@ExtendWith(MockitoExtension.class)` | Inicialización de mocks |
| **Spring Test** | 3.3.3 | `@WebMvcTest`, `@SpringBootTest`, MockMvc |
| **Maven Surefire** | 3.x | Ejecutor de tests |
| **ByteBuddy** | (experimental) | Compatibilidad JDK 25 con Mockito |

> **Importante:** El `pom.xml` ya incluye `spring-boot-starter-test` y la configuración `-Dnet.bytebuddy.experimental=true` en `maven-surefire-plugin`. No es necesario agregar nada adicional.

---

## 3. Matriz de prioridades

### 3.1 Resumen

| Prioridad | Clase | Tipo de test | Escenarios estimados | Dependencias a mockear |
|-----------|-------|-------------|---------------------|------------------------|
| **ALTA** | UsuarioServiceTest | Unitario (Mockito) | ~14 | repository, mapper, passwordEncoder |
| **ALTA** | CompatibilidadServiceTest | Unitario (Mockito) | ~8 | productoRepository |
| **ALTA** | PedidoServiceTest | Unitario (Mockito) | ~14 | pedidoRepository, usuarioRepository, productoRepository, pedidoMapper, pedidoDetalleMapper, compatibilidadService |
| **ALTA** | ProductoServiceTest | Unitario (Mockito) | ~18 | productoRepository, categoriaRepository, productoMapper |
| **ALTA** | CategoriaServiceTest | Unitario (Mockito) | ~10 | categoriaRepository, categoriaMapper |
| **ALTA** | AuthServiceTest | Unitario (Mockito) | ~6 | usuarioRepository, jwtService, authenticationManager, passwordEncoder |
| **MEDIA** | JwtServiceTest | Unitario (Mockito) | ~5 | — (usa secretKey, solo asserts) |
| **MEDIA** | JwtAuthenticationFilterTest | Integración ligera | ~5 | jwtService, userDetailsService, request/response/chain |
| **MEDIA** | CustomUserDetailsServiceTest | Unitario (Mockito) | ~2 | usuarioRepository |
| **BAJA** | PedidosDetalleServiceTest | Unitario (Mockito) | ~2 | pedidoDetalleRepository, pedidoDetalleMapper |
| **BAJA** | AuthControllerTest | Integración (`@WebMvcTest`) | ~4 | authService |
| **BAJA** | PedidosControllerTest | Integración (`@WebMvcTest`) | ~5 | pedidoService |
| **BAJA** | ProductosControllerTest | Integración (`@WebMvcTest`) | ~6 | productoService |
| **BAJA** | UsuariosControllerTest | Integración (`@WebMvcTest`) | ~4 | usuarioService |
| **BAJA** | CategoriasControllerTest | Integración (`@WebMvcTest`) | ~4 | categoriaService |

### 3.2 Prioridades en orden de implementación

```
FASE 1 (ALTA)  → Semana 1: Services de negocio
FASE 2 (MEDIA) → Semana 2: Security
FASE 3 (BAJA)  → Semana 3+: Controladores, limpieza
```

---

## 4. Convenciones de nomenclatura y estructura

### 4.1 Ubicación de los tests

Todos los tests deben ir en:

```
backend/src/test/java/com/tomas/backend/
```

Respetando el subpaquete de la clase bajo prueba.

### 4.2 Nomenclatura de archivos

| Clase bajo prueba | Archivo de test |
|-------------------|-----------------|
| `UsuarioService` | `UsuarioServiceTest.java` |
| `CompatibilidadService` | `CompatibilidadServiceTest.java` |
| `PedidoService` | `PedidoServiceTest.java` |
| `ProductoService` | `ProductoServiceTest.java` |
| `CategoriaService` | `CategoriaServiceTest.java` |
| `AuthService` | `AuthServiceTest.java` |
| `JwtService` | `JwtServiceTest.java` |
| `JwtAuthenticationFilter` | `JwtAuthenticationFilterTest.java` |
| `CustomUserDetailsService` | `CustomUserDetailsServiceTest.java` |
| `PedidosDetalleService` | `PedidosDetalleServiceTest.java` |
| `AuthController` | `AuthControllerTest.java` |
| `PedidosController` | `PedidosControllerTest.java` |
| `ProductosController` | `ProductosControllerTest.java` |
| `UsuariosController` | `UsuariosControllerTest.java` |
| `CategoriasController` | `CategoriasControllerTest.java` |

### 4.3 Nomenclatura de métodos de test

Usar **español descriptivo** (el proyecto está en español) con el patrón:

```
{verboEnIndicativo}_{quéSePrueba}_{resultadoEsperado}
```

Ejemplos:

- `deberiaObtenerUsuarioPorId()` ✅ (ya existente)
- `deberiaLanzarExcepcionCuandoUsuarioNoExiste()` ✅ (ya existente)
- `deberiaCrearPedidoCorrectamente()`
- `deberiaLanzarConflictExceptionPorComponentesIncompatibles()`
- `deberiaLanzarBadRequestPorFaltarCpuYMotherboard()`

### 4.4 Estructura interna de cada test (AAA)

Seguir el patrón **Arrange-Act-Assert** con separación visual:

```java
@Test
void deberia…() {
    // Arrange
    …

    // Act
    …

    // Assert
    …
}
```

Para métodos que lanzan excepción, usar `assertThrows`:

```java
// Arrange
…

// Act & Assert
assertThrows(XXXException.class, () -> servicio.metodo(…));
```

---

## 5. Plan detallado de pruebas — ALTA PRIORIDAD

### 5.1 UsuarioServiceTest

**Archivo:** `backend/src/test/java/com/tomas/backend/service/usuarios/UsuarioServiceTest.java`

> ⚠️ **Estado actual:** Existe en `com.tomas.backend` (raíz). Debe moverse al subpaquete correcto `service.usuarios`.

**Mocks necesarios:** `UsuarioRepository`, `UsuarioMapper`, `PasswordEncoder`

**Escenarios:**

#### `crearUsuario(UsuarioCreateDTO)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Creación exitosa con password encodeado | `UsuarioResponseDTO` con datos correctos; `passwordEncoder.encode()` invocado; `repository.save()` invocado |
| 2 | Error del repositorio al guardar | La excepción se propaga (o se lanza `RuntimeException`) |

#### `obtenerUsuario(Long id)` — ✅ PARCIALMENTE CUBIERTO
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Usuario existe | `UsuarioResponseDTO` con datos correctos |
| 2 | Usuario no existe | `ResourceNotFoundException` |

#### `listaUsuarios()`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Hay usuarios en BD | `List<UsuarioResponseDTO>` con todos los usuarios |
| 2 | BD vacía | Lista vacía |

#### `obtenerUsuarioPorEmail(String email)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Email existe | `UsuarioResponseDTO` con datos correctos |
| 2 | Email no existe | `ResourceNotFoundException` |

#### `eliminarUsuario(Long id)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Usuario existe | Eliminación exitosa; `repository.deleteById()` invocado |
| 2 | Usuario no existe | `ResourceNotFoundException` |

#### `actualizarUsuario(Long id, UsuarioUpdateDTO)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Usuario existe, actualización exitosa | `UsuarioResponseDTO` con campos actualizados; `repository.save()` invocado |
| 2 | Usuario no existe | `ResourceNotFoundException` |
| 3 | Algunos campos nulos en DTO | Se actualizan solo los campos presentes; no se sobrescriben con null (según implementación actual se setean directamente — revisar si hay validación) |

#### `loginUsuario(UsuarioRequestDTO)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Credenciales válidas | `UsuarioResponseDTO` con datos del usuario |
| 2 | Email no registrado | `InvalidCredentialsException` |
| 3 | Email existe pero password incorrecta | `InvalidCredentialsException` |

#### `registrarUsuario(UsuarioCreateDTO)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Registro exitoso (email único) | `UsuarioResponseDTO` con datos del nuevo usuario |
| 2 | Email ya registrado | `ConflictException` |

> **Total escenarios:** ~14 tests

---

### 5.2 CompatibilidadServiceTest

**Archivo:** `backend/src/test/java/com/tomas/backend/service/pedidos/CompatibilidadServiceTest.java`

**Mocks necesarios:** `ProductoRepository`

**Nota:** `validarDetalle()` itera sobre los DTOs, busca cada producto en BD y llama a `validarComponentes()`. Debemos mockear `productoRepository.findById()` para devolver productos con las categorías adecuadas.

#### `validarDetalle(List<PedidoDetallesCreateDTO>)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | 1 CPU AMD + 1 Motherboard AMD + otros componentes | No lanza excepción |
| 2 | 1 CPU Intel + 1 Motherboard Intel + otros componentes | No lanza excepción |
| 3 | 1 CPU AMD + 1 Motherboard Intel | `ConflictException` ("cpu amd no es compatible con motherboard intel") |
| 4 | 1 CPU Intel + 1 Motherboard AMD | `ConflictException` ("cpu intel no es compatible con motherboard amd") |
| 5 | Detalle sin CPU (solo Motherboard + RAM + GPU) | `BadRequestException` ("debe incluir si o si una motherBoard y un cpu") |
| 6 | Detalle sin Motherboard (solo CPU + RAM + GPU) | `BadRequestException` |
| 7 | Detalle con 2 CPUs (AMD duplicado) | `ConflictException` ("Solo puede haber un cpu por pedido") |
| 8 | Detalle con 2 Motherboards | `ConflictException` ("Solo puede haber una motherBoard por pedido") |
| 9 | ID de producto no existe en BD | `ResourceNotFoundException` |

> **Total escenarios:** ~9 tests

---

### 5.3 PedidoServiceTest

**Archivo:** `backend/src/test/java/com/tomas/backend/service/pedidos/PedidoServiceTest.java`

**Mocks necesarios:** `PedidoRepository`, `UsuarioRepository`, `ProductoRepository`, `PedidoMapper`, `PedidoDetalleMapper`, `CompatibilidadService`

Este es el servicio más complejo. Requiere atención especial al mockear el flujo transaccional.

#### `crear(PedidosCreateDTO)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Pedido válido con 3+ componentes, stock suficiente | `PedidosResponseDTO` con total calculado correctamente, estado `PRESUPUESTADO`, fecha no nula |
| 2 | Usuario no existe | `ResourceNotFoundException` ("Usuario no Valido") |
| 3 | Menos de 3 componentes en el detalle | `BadRequestException` ("El pedido debe tener almenos 3 componentes.") |
| 4 | Producto con stock < cantidad (incluye stock = 0) | `ConflictException` ("Producto {nombre} sin stock disponible") — validación unificada en `stock < cantidad` |
| 5 | Producto no encontrado en BD | `ResourceNotFoundException` ("Producto no encontrado") |
| 6 | CompatibilidadService lanza excepción | La excepción se propaga (no se requiere mock especial) |
| 7 | Cálculo de total correcto (múltiples productos, cantidades variadas) | `total` = suma de `precio * cantidad` de cada detalle |

#### `obtenerPedido(Long id)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Pedido existe | `PedidosResponseDTO` con datos correctos |
| 2 | Pedido no existe | `ResourceNotFoundException` ("Pedido no encontrado") |

#### `confirmarPedido(Long id)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Pedido existe y está en estado `PRESUPUESTADO`, stock suficiente | Estado cambia a `CONFIRMADO`, stock de cada producto se reduce |
| 2 | Pedido ya está `CONFIRMADO` | `ConflictException` ("Este pedido ya fue confirmado") |
| 3 | Pedido no existe | `ResourceNotFoundException` |
| 4 | Stock insuficiente al confirmar | `ConflictException` ("Producto {nombre}sin stock disponible") — OJO: sin espacio antes de "sin", typo real en código |

#### `reCrearPedido(Long id, PedidosCreateDTO)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Pedido existe en estado `PRESUPUESTADO` | Pedido original pasa a `CANCELADO`, se crea un nuevo pedido con los nuevos detalles |
| 2 | Pedido existe en estado `CONFIRMADO` | `ConflictException` ("Este pedido no puede modifcarse debido a que ya fue confirmado") — typo "modifcarse" real en código |
| 3 | Pedido no existe | `ResourceNotFoundException` |
| 4 | Nuevo pedido inválido (errores de validación de `crear()`) | La excepción correspondiente se propaga |

> **Total escenarios:** ~17 tests (incluye nuevo escenario de stock insuficiente al confirmar)

---

### 5.4 ProductoServiceTest

**Archivo:** `backend/src/test/java/com/tomas/backend/service/productos/ProductoServiceTest.java`

**Mocks necesarios:** `ProductoRepository`, `CategoriaRepository`, `ProductoMapper`

#### `crearProducto(ProductoCreateDTO)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Categoría existe, creación exitosa | `ProductoResponseDTO` con datos correctos |
| 2 | Categoría no existe | `ResourceNotFoundException` |

#### `obtenerProducto(Long id)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Producto existe y está activo | `ProductoResponseDTO` con datos correctos |
| 2 | Producto existe pero está desactivado | `ConflictException` ("El producto se encuentra desactivado") |
| 3 | Producto no existe | `ResourceNotFoundException` |

#### `listarProductos()`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Hay productos | `List<ProductoResponseDTO>` con todos los productos |
| 2 | No hay productos | Lista vacía |

#### `actualizarProducto(ProductoUpdateDTO, Long id)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Producto existe, sin cambio de categoría | Producto actualizado correctamente |
| 2 | Producto existe, con cambio de categoría válida | Producto actualizado con nueva categoría |
| 3 | Producto no existe | `ResourceNotFoundException` |
| 4 | Cambio de categoría a una que no existe | `ResourceNotFoundException` |

#### `productosPorCategoria(Long idCategoria)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Categoría existe, con productos | Lista de productos filtrados |
| 2 | Categoría existe, sin productos | Lista vacía |
| 3 | Categoría no existe | `ResourceNotFoundException` |

#### `desactivarProducto(Long id)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Producto existe | `activo = false`, `repository.save()` invocado, DTO retornado |
| 2 | Producto no existe | `ResourceNotFoundException` |

#### `activarProducto(Long id)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Producto existe | `activo = true`, `repository.save()` invocado, DTO retornado |
| 2 | Producto no existe | `ResourceNotFoundException` |

#### `aumentarStock(Long id, Integer cantidad)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Producto activo, aumento válido (> 0) | Stock incrementado en la cantidad indicada |
| 2 | Producto existe pero está desactivado | `ConflictException` |
| 3 | Producto no existe | `ResourceNotFoundException` |
| 4 | Aumento ≤ 0 | `BadRequestException` ("debe ser mayor que 0") |

#### `disminuirStock(Long id, Integer cantidad)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Producto activo, disminución válida (≤ stock, > 0) | Stock disminuido correctamente |
| 2 | Producto desactivado | `ConflictException` |
| 3 | Producto no existe | `ResourceNotFoundException` |
| 4 | Disminución ≤ 0 | `BadRequestException` |
| 5 | Disminución > stock actual | `ConflictException` ("Stock insuficiente") |

#### `estaActivo(Long id)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Producto activo | `true` |
| 2 | Producto inactivo | `false` |
| 3 | Producto no existe | `ResourceNotFoundException` |

#### `actualizarPrecio(Long id, BigDecimal precio)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Producto existe | Precio actualizado, DTO retornado |
| 2 | Producto no existe | `ResourceNotFoundException` |

> **Total escenarios:** ~18 tests

---

### 5.5 CategoriaServiceTest

**Archivo:** `backend/src/test/java/com/tomas/backend/service/categorias/CategoriaServiceTest.java`

**Mocks necesarios:** `CategoriaRepository`, `CategoriaMapper`

> **Nota importante:** `CategoriaService` fue migrado a DTOs: `listarCategorias()`, `obtenerCategoria()`, `crearCategoria()`, `actualizarCategoria()`, `desactivarCategoria()` y `activarCategoria()` devuelven `CategoriaResponseDTO`. Ya no devuelve entidades `Categoria` crudas. Usa `CategoriaMapper` (con `toResponseDTO`, `toEntity` y `toUpdateEntity`). Los tests deben mockear `categoriaMapper`.

#### `listarCategorias()`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Hay categorías | `List<CategoriaResponseDTO>` con todas, incluyendo activas e inactivas |
| 2 | No hay categorías | Lista vacía |

#### `obtenerCategoria(Long id)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Categoría existe y está activa | `CategoriaResponseDTO` devuelta |
| 2 | Categoría existe pero está desactivada | `ConflictException` ("La categoria se encuentra desactivada") |
| 3 | Categoría no existe | `ResourceNotFoundException` ("No existe una categoria con el id: " + id) |

#### `crearCategoria(CategoriaCreateDTO)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Nombre no nulo, creación exitosa | `CategoriaResponseDTO` devuelta; `mapper.toEntity()` y `repository.save()` invocados |
| 2 | Nombre nulo | `BadRequestException` ("La categoria debe tener un nombre") |

#### `actualizarCategoria(CategoriaRequestDTO, Long id)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Categoría existe, activa, nombre válido | Nombre actualizado, `CategoriaResponseDTO` retornada; `mapper.toUpdateEntity()` y `repository.save()` invocados |
| 2 | Categoría no existe | `ResourceNotFoundException` |
| 3 | Categoría existe pero está desactivada | `ConflictException` |
| 4 | Nombre nulo en update | `BadRequestException` |

#### `desactivarCategoria(Long id)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Categoría existe | `activo = false`, `CategoriaResponseDTO` retornada |
| 2 | Categoría no existe | `ResourceNotFoundException` |

#### `activarCategoria(Long id)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Categoría existe | `activo = true`, `CategoriaResponseDTO` retornada |
| 2 | Categoría no existe | `ResourceNotFoundException` |

> **Total escenarios:** ~10 tests

---

### 5.6 AuthServiceTest

**Archivo:** `backend/src/test/java/com/tomas/backend/service/usuarios/AuthServiceTest.java`

**Mocks necesarios:** `UsuarioRepository`, `JwtService`, `AuthenticationManager`, `PasswordEncoder`

#### `login(LoginRequest)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Credenciales válidas, usuario existe | `AuthResponse` con token JWT y userId |
| 2 | Usuario no encontrado después de autenticar | `ResourceNotFoundException` |
| 3 | `AuthenticationManager.authenticate()` lanza `BadCredentialsException` | La excepción se propaga (el controller la maneja) |

#### `register(RegisterRequest)`
| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Registro exitoso (email no existe) | `RegisterResponse` con mensaje "Usuario registrado con exito" |
| 2 | Email ya registrado | `ConflictException` ("El email ya está registrado") |
| 3 | Verificar que password se encodea antes de guardar | `passwordEncoder.encode()` invocado con la password del request |

> **Total escenarios:** ~6 tests

---

## 6. Plan detallado de pruebas — PRIORIDAD MEDIA

### 6.1 JwtServiceTest

**Archivo:** `backend/src/test/java/com/tomas/backend/security/JwtServiceTest.java`

**Mocks necesarios:** Ninguno. Se usa `@InjectMocks` directamente.
**Setup:** Se debe asignar `secretKey` mediante `ReflectionTestUtils` o `@Value` mock.

```java
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // Asignar secretKey via reflection (no hay setter)
        ReflectionTestUtils.setField(jwtService, "secretKey", "my32charsecretkeyforhs256algorithm!!");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L); // 24h
    }
```

**Escenarios:**

| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | `generateToken(userDetails)` devuelve token no nulo | Token JWT válido en formato string |
| 2 | `extractUsername(token)` devuelve el username correcto | Coincide con `userDetails.getUsername()` |
| 3 | `isTokenValid(token, userDetails)` con token válido y mismo usuario | `true` |
| 4 | `isTokenValid(token, userDetails)` con token expirado | `false` |
| 5 | `isTokenValid(token, userDetails)` con username diferente | `false` |

> **Total escenarios:** ~5 tests

---

### 6.2 JwtAuthenticationFilterTest

**Archivo:** `backend/src/test/java/com/tomas/backend/security/JwtAuthenticationFilterTest.java`

**Mocks necesarios:** `JwtService`, `UserDetailsService`, `HttpServletRequest`, `HttpServletResponse`, `FilterChain`

**Enfoque:** Test unitario con Mockito, no se necesita levantar el contexto Spring.

| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Header `Authorization: Bearer <token>` válido | `SecurityContextHolder.getContext().getAuthentication()` no es null, username correcto |
| 2 | Sin header Authorization | `filterChain.doFilter()` invocado, no se intenta autenticar |
| 3 | Header malformado (no empieza con "Bearer ") | `filterChain.doFilter()` invocado, no se autentica |
| 4 | Token válido pero usuario no existe en BD | `UsernameNotFoundException` se propaga (no se setea authentication) |
| 5 | `isTokenValid()` retorna `false` | No se setea autenticación en el contexto |

> ⚠️ **IMPORTANTE:** En cada test, limpiar `SecurityContextHolder.clearContext()` en `@BeforeEach` o `@AfterEach` para evitar efectos colaterales entre tests.

> **Total escenarios:** ~5 tests

---

### 6.3 CustomUserDetailsServiceTest

**Archivo:** `backend/src/test/java/com/tomas/backend/security/CustomUserDetailsServiceTest.java`

**Mocks necesarios:** `UsuarioRepository`

| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | Usuario encontrado por email | `UserDetails` retornado, con username = email, password no nula, authorities = `[ROL]` |
| 2 | Usuario no encontrado | `UsernameNotFoundException` |

> **Total escenarios:** ~2 tests

---

## 7. Plan detallado de pruebas — PRIORIDAD BAJA

### 7.1 PedidosDetalleServiceTest

**Archivo:** `backend/src/test/java/com/tomas/backend/service/pedidosDetalle/PedidosDetalleServiceTest.java`

**Mocks necesarios:** `PedidoDetalleRepository`, `ProductoRepository`, `PedidoDetalleMapper`

| # | Escenario | Resultado esperado |
|---|-----------|-------------------|
| 1 | PedidoDetalle existe | `PedidoDetallesResponseDTO` retornado |
| 2 | PedidoDetalle no existe | `ResourceNotFoundException` |

> **Total escenarios:** ~2 tests

---

### 7.2 Controladores (integración)

**Archivos:** `backend/src/test/java/com/tomas/backend/controller/*ControllerTest.java`

**Enfoque:** Usar `@WebMvcTest(ControllerClass.class)` con `@MockBean` para el service correspondiente y `MockMvc` para disparar peticiones HTTP simuladas.

#### AuthControllerTest
| # | Endpoint | Escenario | Código esperado |
|---|----------|-----------|-----------------|
| 1 | `POST /auth/login` | Login exitoso | `200 OK` + `AuthResponse` JSON |
| 2 | `POST /auth/register` | Registro exitoso | `200 OK` + `RegisterResponse` |
| 3 | `POST /auth/register` | Email duplicado | `409 CONFLICT` |

#### PedidosControllerTest
| # | Endpoint | Escenario | Código esperado |
|---|----------|-----------|-----------------|
| 1 | `POST /pedidos/crear` | Pedido válido | `200 OK` |
| 2 | `POST /pedidos/crear` | Request inválido (@Valid falla) | `400 BAD_REQUEST` |
| 3 | `GET /pedidos/id/{id}` | Pedido existe | `200 OK` |
| 4 | `PUT /pedidos/confirmar/{id}` | Confirmación exitosa | `200 OK` |
| 5 | `PUT /pedidos/agregar-producto/{id}` | Re-crear pedido | `200 OK` |

#### ProductosControllerTest
| # | Endpoint | Escenario | Código esperado |
|---|----------|-----------|-----------------|
| 1 | `GET /productos` | Listar todos | `200 OK` |
| 2 | `GET /productos/id/{id}` | Producto activo existe | `200 OK` |
| 3 | `POST /productos/crear` | Creación exitosa | `200 OK` |
| 4 | `POST /productos/crear` | @Validation falla | `400 BAD_REQUEST` |
| 5 | `POST /productos/actualizar/{id}` | Actualización exitosa | `200 OK` |

#### UsuariosControllerTest
| # | Endpoint | Escenario | Código esperado |
|---|----------|-----------|-----------------|
| 1 | `GET /users` | Listar usuarios | `200 OK` |
| 2 | `GET /users/{id}` | Usuario existe | `200 OK` |
| 3 | `DELETE /users/Delete/{id}` | Usuario existe | `200 OK` (void) |
| 4 | `PUT /users/Update/{id}` | Actualización exitosa | `200 OK` |

#### CategoriasControllerTest
| # | Endpoint | Escenario | Código esperado |
|---|----------|-----------|-----------------|
| 1 | `GET /categorias` | Listar categorías | `200 OK` |
| 2 | `GET /categorias/id/{id}` | Categoría activa existe | `200 OK` |
| 3 | `POST /categorias/crear` | Creación exitosa | `200 OK` |

> **Total escenarios controladores:** ~17 tests combinados

---

## 8. Cobertura actual y brechas

### 8.1 Estado actual

> **Actualizado el 2026-08-08** tras completar FASE 1 y FASE 2. Total: **110 tests verdes** (BUILD SUCCESS).

| Archivo | Paquete | Tests | Fase |
|---------|---------|-------|------|
| `BackendApplicationTests.java` | raíz | 1 (contextLoads) | — |
| `UsuarioServiceTest.java` | `service.usuarios` | 16 | FASE 1 |
| `AuthServiceTest.java` | `service.usuarios` | 6 | FASE 1 |
| `ProductoServiceTest.java` | `service.productos` | 32 | FASE 1 |
| `PedidoServiceTest.java` | `service.pedidos` | 17 | FASE 1 |
| `CompatibilidadServiceTest.java` | `service.pedidos` | 12 | FASE 1 |
| `CategoriaServiceTest.java` | `service.categorias` | 13 | FASE 1 |
| `JwtServiceTest.java` | `security` | 6 | FASE 2 |
| `CustomUserDetailsServiceTest.java` | `security` | 2 | FASE 2 |
| `JwtAuthenticationFilterTest.java` | `security` | 5 | FASE 2 |
| **Total** | | **110** | |

### 8.2 Brechas identificadas

#### Brechas cerradas (FASE 1 + FASE 2 completas)
- ✅ `CompatibilidadService` — **12 tests**
- ✅ `PedidoService` — **17 tests**
- ✅ `ProductoService` — **32 tests**
- ✅ `CategoriaService` — **13 tests**
- ✅ `AuthService` — **6 tests**
- ✅ `JwtService` — **6 tests** (incluye token expirado y malformado tras fix de producción)
- ✅ `JwtAuthenticationFilter` — **5 tests**
- ✅ `CustomUserDetailsService` — **2 tests**

#### Brechas pendientes
- ❌ `PedidosDetalleService` — **0 tests**, 0% cobertura (prioridad baja)
- ❌ Todos los controladores — **0 tests de integración** (FASE 3 pendiente)

### 8.3 Objetivo de cobertura

| Fase | Cobertura de servicios | Cobertura total del código |
|------|----------------------|---------------------------|
| Fase 1 (ALTA) | > 80% servicios | > 50% |
| Fase 2 (MEDIA) | > 90% servicios + seguridad | > 65% |
| Fase 3 (BAJA) | — | > 75% (con controladores) |

> **Herramienta recomendada:** Usar `mvn test -Dcoverage` con JaCoCo para medir cobertura. Si no está configurado, agregar al `pom.xml`:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
    </executions>
</plugin>
```

### 8.4 Deuda técnica conocida y aceptada

| # | Deuda | Detalle | Estado |
|---|-------|---------|--------|
| 1 | **Race condition en `confirmarPedido` (doble descuento de stock bajo concurrencia)** | El guard `if (estado == CONFIRMADO)` protege contra llamadas secuenciales, pero **no contra dos requests paralelos** sobre el mismo pedido `PRESUPUESTADO` (TOCTOU: ambos leen `PRESUPUESTADO`, ambos descuentan, ambos commitean). No existe `@Version` ni `@Lock` en el código. **Decisión tomada (2026-08-08):** se acepta como deuda para el MVP. Si se va a producción con ventas reales, implementar primero `@Version` (optimistic) y evaluar `@Lock(PESSIMISTIC_WRITE)` en el repository. | ✅ Aceptada — documentada |
| 2 | ~~**`reCrearPedido` cancela el pedido original antes de validar el nuevo**~~ | ~~El pedido original pasa a `CANCELADO` en memoria sin `save` explícito antes de llamar a `crear()`. Si el pedido nuevo falla, la cancelación no se persiste (el test 17 de `PedidoServiceTest` documenta este comportamiento).~~ **RESUELTA — Opción A (2026-08-08):** se adopta la atomicidad transaccional. `reCrearPedido` es `@Transactional` y todas las `ApiException` extienden `RuntimeException`, por lo que si `crear()` falla, la transacción revierte y el original queda `PRESUPUESTADO` en BD. El estado en memoria puede ser `CANCELADO` pero no se persiste. Comportamiento documentado en `PedidoService.reCrearPedido` y en el test 17. | ✅ Resuelta — Opción A |
| 3 | ~~**Inconsistencia en mensajes de stock**~~ | ~~`crear()` usa `"Producto {nombre} sin stock disponible"` (con espacio) y `confirmarPedido()` usa `"Producto {nombre}sin stock disponible"` (sin espacio — typo).~~ **RESUELTA (2026-08-08):** mensajes unificados con espacio. Los tests fueron actualizados al nuevo contrato. | ✅ Resuelta — unificada |
| 4 | ~~**Token JWT expirado produce HTTP 500 en vez de 401/403**~~ | ~~En jjwt 0.11.5, `parseClaimsJws()` valida `exp` durante el parseo y lanza `ExpiredJwtException` antes de devolver claims. `JwtService.isTokenValid()` nunca podía devolver `false` para un token vencido y `JwtAuthenticationFilter` (línea 49) propagaba la excepción fuera de la cadena → 500 opaco. El frontend no podía distinguir "sesión expirada" de "servidor caído". Detectado por `deberiaRechazarUnTokenExpirado` en FASE 2.~~ **RESUELTA (2026-08-08):** `JwtService.extractUsername()` captura `JwtException`/`IllegalArgumentException` y devuelve `null` (el filtro ya tenía el guard `username != null`); `isTokenValid()` agrega guard anti-NPE y devuelve `false`. El test se actualizó al contrato nuevo (`assertFalse`) y se agregó `deberiaDevolverFalseCuandoElTokenEstaMalformado`. `isTokenExpired()`/`extractExpiration()` se conservan como defensa en profundidad. | ✅ Resuelta — fix en JwtService |

---

## 9. Instrucciones de ejecución

### 9.1 Ejecutar todos los tests

```bash
# Desde la carpeta backend/
.\mvnw.cmd test
```

### 9.2 Ejecutar un test específico

```bash
.\mvnw.cmd test -Dtest=UsuarioServiceTest
```

### 9.3 Ejecutar tests de una clase con patrón

```bash
.\mvnw.cmd test -Dtest=UsuarioServiceTest,CompatibilidadServiceTest
```

### 9.4 Ejecutar tests de un paquete

```bash
.\mvnw.cmd test -Dtest="com.tomas.backend.service.**"
```

### 9.5 Ejecutar tests con reporte de cobertura (si se agrega JaCoCo)

```bash
.\mvnw.cmd clean test
# El reporte HTML estará en: backend/target/site/jacoco/index.html
```

### 9.6 Compilar sin ejecutar tests

```bash
.\mvnw.cmd clean package -DskipTests
```

### 9.7 Consideraciones importantes

- **MySQL debe estar corriendo** si se ejecuta `BackendApplicationTests` o cualquier test de integración que use `@SpringBootTest` sin perfiles específicos.
- Para tests unitarios puros (con `@ExtendWith(MockitoExtension.class)`) **no se necesita BD**.
- Los tests con `@WebMvcTest` **no necesitan BD** porque solo mockean la capa de servicio.
- Verificar que `maven-surefire-plugin` tiene la flag `-Dnet.bytebuddy.experimental=true` para compatibilidad JDK 25 (ya está configurado).

---

## 10. Buenas prácticas para el equipo

### 10.1 Estructura y anotaciones

```java
@ExtendWith(MockitoExtension.class)  // ← SIEMPRE para tests unitarios
class MiServicioTest {

    @Mock
    private RepositorioDependiente repositorio;

    @Mock
    private MapperDependiente mapper;

    @InjectMocks
    private MiServicio servicio;
```

- **NO** usar `@SpringBootTest` para tests unitarios de servicios.
- **NO** usar `@MockBean` en tests unitarios (es para integración).
- Usar `@InjectMocks` para la clase bajo prueba. Spring Boot inyecta automáticamente los `@Mock` en el constructor.

### 10.2 Mockeando repositorios

```java
// Happy path — repository devuelve un Optional con entidad
when(repository.findById(1L)).thenReturn(Optional.of(entidad));

// Error path — Optional vacío
when(repository.findById(99L)).thenReturn(Optional.empty());

// Listas
when(repository.findAll()).thenReturn(List.of(e1, e2));
when(repository.findAll()).thenReturn(Collections.emptyList());

// Save
when(repository.save(any())).thenReturn(entidadGuardada);
```

### 10.3 Mockeando mappers

```java
when(mapper.toResponseDTO(any())).thenReturn(responseDTOEsperado);
```

Verificar que el mapper se invocó:

```java
verify(mapper).toResponseDTO(entidad);
```

### 10.4 Probando excepciones

```java
// Arrange
when(repository.findById(99L)).thenReturn(Optional.empty());

// Act & Assert
ResourceNotFoundException ex = assertThrows(
    ResourceNotFoundException.class,
    () -> servicio.obtenerUsuario(99L)
);

assertEquals("Usuario no encontrado", ex.getMessage());
```

### 10.5 Verificando interacciones

```java
// Verificar que un método fue llamado
verify(repository).save(entidad);

// Verificar que NO fue llamado
verify(repository, never()).deleteById(any());

// Verificar número exacto de llamadas
verify(repository, times(3)).findById(any());
```

### 10.6 Usando ArgumentMatchers

```java
// Cualquier valor
when(repository.findById(anyLong())).thenReturn(...);

// Capturar argumento para inspeccionarlo
ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
verify(repository).save(captor.capture());
Producto productoGuardado = captor.getValue();
assertEquals("Nuevo nombre", productoGuardado.getNombre());
```

### 10.7 Manejo del paquete `excetions` (typo)

> ⚠️ **ATENCIÓN:** El paquete de excepciones se escribe `excetions` (con `e`), no `exceptions`. No "corregir" esto. Las importaciones en los tests deben usar:

```java
import com.tomas.backend.excetions.custom.ResourceNotFoundException;
import com.tomas.backend.excetions.custom.ConflictException;
import com.tomas.backend.excetions.custom.BadRequestException;
import com.tomas.backend.excetions.custom.InvalidCredentialsException;
```

### 10.8 Tests de JWT — Configuración de secretKey

`JwtService` usa `@Value` para `secretKey` y `jwtExpiration`. Como no hay setters, usar `ReflectionTestUtils`:

```java
import org.springframework.test.util.ReflectionTestUtils;

@BeforeEach
void setUp() {
    ReflectionTestUtils.setField(jwtService, "secretKey", "my32charsecretkeyforhs256algorithm!!");
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);
}
```

### 10.9 Tests de filtros — Limpiar SecurityContext

```java
@BeforeEach
void setUp() {
    SecurityContextHolder.clearContext();
}

@AfterEach
void tearDown() {
    SecurityContextHolder.clearContext();
}
```

### 10.10 Pruebas con fechas y BigDecimal

```java
// BigDecimal: usar compareTo() en vez de equals() para evitar issues de escala
assertEquals(0, totalEsperado.compareTo(totalObtenido));

// LocalDateTime: no comparar directamente, verificar que no sea nulo
assertNotNull(pedido.getFecha());
```

### 10.11 Qué NO testear

- **Mappers** (UsuarioMapper, ProductoMapper, etc.): son conversiones simples de DTO a Entity y viceversa. Si se desea, un test de integración pequeño basta, pero no es prioritario.
- **Config** (SecurityConfig, CorsConfig, DataSeeder): se prueban implícitamente al levantar el contexto. DataSeeder es datos semilla, no lógica de negocio.
- **DTOs, Enums, Excepciones personalizadas**: son POJOs sin lógica. No requieren tests unitarios.
- **Métodos privados**: se prueban indirectamente a través de los métodos públicos que los invocan.

### 10.12 Checklist de revisión de tests

- [ ] ¿El test sigue el patrón AAA (Arrange-Act-Assert)?
- [ ] ¿El nombre del test describe el escenario y el resultado esperado?
- [ ] ¿Se probó el happy path?
- [ ] ¿Se probó al menos un escenario de error por cada excepción documentada?
- [ ] ¿Los mocks están configurados para devolver los valores necesarios?
- [ ] ¿Se están usando `verify()` para interacciones clave?
- [ ] ¿El test es independiente (no depende de estado de otros tests)?
- [ ] ¿Se limpió `SecurityContextHolder` en tests de seguridad?
- [ ] ¿Las importaciones usan el paquete `excetions` (con `e`)?
- [ ] ¿El test pasa con `.\mvnw.cmd test`?

---

## 11. Glosario

| Término | Significado |
|---------|-------------|
| **AAA** | Arrange-Act-Assert: patrón de organización de tests |
| **Happy Path** | Escenario donde todo funciona correctamente, sin errores |
| **Mock** | Objeto simulado que reemplaza una dependencia real |
| **Stub** | Configuración de un mock para devolver un valor específico |
| **`@InjectMocks`** | Crea una instancia real de la clase e inyecta los mocks en su constructor |
| **`@Mock`** | Crea un mock de la dependencia |
| **`@WebMvcTest`** | Test de integración que levanta solo la capa web (controllers) |
| **`@SpringBootTest`** | Test de integración que levanta todo el contexto Spring |
| **`ArgumentCaptor`** | Captura un argumento pasado a un mock para inspeccionarlo |
| **JaCoCo** | Herramienta de medición de cobertura de código |

---

## Anexo A: Plantilla de test unitario

```java
package com.tomas.backend.service.productos;

import com.tomas.backend.DTOs.productos.ProductoResponseDTO;
import com.tomas.backend.entity.Producto;
import com.tomas.backend.excetions.custom.ResourceNotFoundException;
import com.tomas.backend.mappers.ProductoMapper;
import com.tomas.backend.repository.CategoriaRepository;
import com.tomas.backend.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void deberiaObtenerProductoCuandoExisteYEstaActivo() {
        // Arrange
        Long id = 1L;
        Producto producto = new Producto();
        producto.setIdProducto(id);
        producto.setActivo(true);

        ProductoResponseDTO responseDTO = new ProductoResponseDTO();
        responseDTO.setId(id);

        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));
        when(productoMapper.toResponseDTO(producto)).thenReturn(responseDTO);

        // Act
        ProductoResponseDTO resultado = productoService.obtenerProducto(id);

        // Assert
        assertEquals(id, resultado.getId());
        verify(productoRepository).findById(id);
        verify(productoMapper).toResponseDTO(producto);
    }

    @Test
    void deberiaLanzarResourceNotFoundExceptionCuandoProductoNoExiste() {
        // Arrange
        Long id = 999L;
        when(productoRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> productoService.obtenerProducto(id));
        verify(productoRepository).findById(id);
    }
}
```

---

## Anexo B: Plantilla de test de integración (controlador)

```java
package com.tomas.backend.controller;

import com.tomas.backend.DTOs.auth.AuthResponse;
import com.tomas.backend.DTOs.auth.LoginRequest;
import com.tomas.backend.service.usuarios.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void deberiaRetornar200YTokenAlHacerLogin() throws Exception {
        // Arrange
        AuthResponse authResponse = new AuthResponse("token123", 1L);
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@test.com\", \"password\": \"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.userId").value(1L));
    }
}
```

---

*Documento generado el 30 de julio de 2026. Revisar y actualizar tras cada sprint de testing.*
