# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

LuckyPhone backend: a Spring Boot 3.2.5 / Java 21 REST API for a phone & accessories store. It covers internal management (products, categories, suppliers, inventory movements, sales, users/roles) plus a public read-only product catalog for a storefront page. The frontend that consumes this API lives in a separate project (`front_lukyphone`, not part of this repo) — CORS is currently locked to `http://localhost:4200`.

## Commands

- Build: `./mvnw.cmd clean compile`
- Run locally: `./mvnw.cmd spring-boot:run` — starts on `http://localhost:8080`; `spring-boot-devtools` is on the classpath, so recompiling (`./mvnw.cmd compile`) while the app is running triggers a hot restart.
- Run tests: `./mvnw.cmd test`
- Run a single test: `./mvnw.cmd test -Dtest=ClassName#methodName`
- Package: `./mvnw.cmd package`

Only one placeholder test exists (`SistematecApplicationTests#contextLoads`) — there is no meaningful automated test suite yet. Changes have been verified so far by running the app against the real dev MySQL database and exercising endpoints with `curl`.

## Database

MySQL, configured in `src/main/resources/application.properties`. Note the non-default port (`4000`, not `3306`) and database name `sistema_ventas`. Credentials live only in that file — don't duplicate them elsewhere.

`spring.jpa.hibernate.ddl-auto=update` is set, so schema changes are driven by the `@Entity` classes: add a field to an entity and restart the app, Hibernate adds the column itself (this is how `CategoriaProducto.fechaEliminacion` was added — no manual migration was written).

Known dev/seed accounts (local DB only): `admin` / `admin123` (role `ADMIN`), `user` / `user123` (role `USER`).

## Architecture

Layered REST API (not classic MVC-with-views): `controller` → `service` → `repo` (Spring Data JPA) → `entity`, plus `dto` for request/response shapes that don't map 1:1 to an entity, `security` for JWT/Spring Security, and `exception` for centralized error handling.

### Auth & security (`security/`)

- `JwtUtil` issues/validates HS-signed JWTs (`jwt.secret` / `jwt.expiration` in `application.properties`), with the username as the subject and the role as a `rol` claim.
- `JwtAuthFilter` (`OncePerRequestFilter`) reads `Authorization: Bearer <token>`, validates it, and populates `SecurityContextHolder` — fully stateless, no server sessions.
- `SecurityConfig` defines route-level authorization. Two roles: `ADMIN` (owner) and `USER` (employee). `USER` can do day-to-day operations (`/api/ventas/**`, `/api/inventario/**`, read-only `/products`); catalog/config management (`categorias`, `proveedores`, `usuarios`, `roles`, `metodos-pago`) is `ADMIN`-only. `/auth/login` and `/api/public/**` are open to everyone.
- `AuthController.login()` is the only place passwords are checked (`PasswordEncoder.matches`); it rejects inactive (`estado=false`) or soft-deleted users.
- Sales are always attributed to the authenticated JWT user: `VentaController` extracts `Authentication.getName()` and `VentaService` resolves the `Usuario` by that username — never trust a client-supplied user id for who made a sale.

### Soft delete

`Producto`, `Proveedor`, `Usuario`, and `CategoriaProducto` use a `fechaEliminacion` timestamp instead of physical deletes. Every repo for these four entities exposes `...AndFechaEliminacionIsNull` finder variants, and every `listar()` / `obtenerPorId()` in the corresponding service goes through those — never call the plain inherited `findAll()` / `findById()` for these entities, or soft-deleted rows leak back into results. `eliminar()` in these services sets `fechaEliminacion` + `estado=false` and saves; it does not call `repo.deleteById()`.

`Venta`, `DetalleVenta`, `MovimientoInventario`, `Rol`, and `MetodoPago` have no soft-delete column and use plain JPA CRUD.

### Error handling (`exception/`)

`GlobalExceptionHandler` (`@RestControllerAdvice`) is the single place HTTP status codes get decided from exceptions — controllers/services should not build their own error `ResponseEntity`. Throw `RecursoNoEncontradoException` (→ 404) or `StockInsuficienteException` (→ 409) from service code. `MethodArgumentNotValidException` (from `@Valid`) → 400 with a field-error map. `DataIntegrityViolationException` (FK conflicts, e.g. deleting a `CategoriaProducto` still referenced by a `Producto`) → 409. Anything else → 500 with no stack trace leaked to the client.

### Pagination

All "table" listing endpoints return Spring Data's `Page<T>` (built into `spring-boot-starter-data-jpa`, no extra dependency) instead of `List<T>`, via a `Pageable` controller parameter with `@PageableDefault(size = 20, sort = "...")`. Repo methods just add a trailing `Pageable` parameter and change the return type to `Page<T>` — Spring Data resolves it automatically from the derived query, no custom query needed. Global defaults (`spring.data.web.pageable.default-page-size=20`, `max-page-size=100`) live in `application.properties`. Small fixed catalogs (`Rol`, `MetodoPago`) and the single-sale detail list (`/api/ventas/{id}/detalles`) intentionally stay plain, unpaginated lists.

### Inventory/sales business logic

`VentaService.crear()` is the one place stock gets decremented: for each line item it checks `producto.getStock()` against the requested quantity (throwing `StockInsuficienteException` if insufficient), writes a `MovimientoInventario` row with `tipo=SALIDA`, and updates `Producto.stock` — all inside one `@Transactional` method. `MovimientoInventarioService.registrar()` is the manual counterpart for stock intake from suppliers (`ENTRADA`), corrections (`AJUSTE` — sets stock directly to the given value rather than adding/subtracting), and manual `SALIDA`. Any new flow that changes `Producto.stock` should also write a `MovimientoInventario` row — that table is the audit trail.

### JSON shaping

`Usuario.password` is annotated `@JsonProperty(access = WRITE_ONLY)` — accepted on create/update but never serialized in any response, including when a `Usuario` is nested inside another entity (e.g. `Venta.usuario`). `UsuarioResponseDTO` additionally exists for `UsuarioController` so the API shapes the role as a plain string instead of the nested `Rol` entity. Entities use only unidirectional `@ManyToOne` relations (no `@OneToMany` back-references) specifically to avoid Jackson infinite-recursion loops — don't add a back-reference without also adding `@JsonIgnore` / `@JsonManagedReference`+`@JsonBackReference`.

## API reference (for `front_lukyphone`)

Base URL: `http://localhost:8080`. CORS currently only allows `http://localhost:4200` — update `SecurityConfig.corsConfigurationSource()` if the frontend runs on a different origin.

### Auth

- `POST /auth/login` — public. Body `{"usuario","password"}` → `200 {"token","rol","nombre"}`, or `401` on bad credentials. Send the token as `Authorization: Bearer <token>` on every other request.

### Public storefront (no auth required)

- `GET /api/public/productos?page=&size=&sort=` → `Page<Producto>`
- `GET /api/public/productos/{id}` → `Producto` or `404`
- `GET /api/public/categorias?page=&size=&sort=` → `Page<CategoriaProducto>`

### Products — `/products` (GET: USER or ADMIN, writes: ADMIN only)

- `GET /products?page=&size=&sort=`
- `GET /products/{id}`
- `GET /products/buscar?nombre=|idCategoria=|idProveedor=|precioMin=&precioMax=|codigoBarras=|stockMenorQue=&page=&size=&sort=` — first matching filter param wins; no filter = same as `listar`
- `POST /products` / `PUT /products/{id}` — body validated: `nombre` required, `precio >= 0`, `stock >= 0`
- `DELETE /products/{id}` — soft delete

### Categories, suppliers, users, roles, payment methods — ADMIN only

- `/api/categorias` — CRUD, paginated `GET`, soft delete
- `/api/proveedores` — CRUD, paginated `GET`, soft delete
- `/api/usuarios` — CRUD, paginated `GET` (returns `UsuarioResponseDTO`, no password field), soft delete
- `/api/roles` — `GET` only, plain array, fixed `ADMIN`/`USER`
- `/api/metodos-pago` — CRUD, plain array (not paginated)

### Sales & inventory — USER or ADMIN

- `GET /api/ventas?page=&size=&sort=` (default sorted by `fecha` desc)
- `GET /api/ventas/{id}/detalles` — plain list
- `POST /api/ventas` — body `{"tipoComprobante","metodoPago":{"idMetodoPago"},"detalles":[{"producto":{"idProducto"},"cantidad"}]}`; the selling user is taken from the JWT — don't send it
- `GET /api/inventario?page=&size=&sort=`, `GET /api/inventario/producto/{idProducto}`, `GET /api/inventario/tipo/{tipo}` (default sorted by `fechaCreacion` desc)
- `POST /api/inventario` — body `{"producto":{"idProducto"},"tipo":"ENTRADA"|"SALIDA"|"AJUSTE","cantidad","observacion"}`

### Errors

All non-2xx responses are `{"status","message","timestamp","errors"?}` — `errors` is a field→message map, present only on 400 validation failures.

### Pagination response shape

Every paginated endpoint returns Spring Data's default `Page` JSON: `{"content":[...],"totalElements","totalPages","number","size","first","last",...}`. Read `content` for the rows and the rest for pager UI (page count, current page, etc.).
