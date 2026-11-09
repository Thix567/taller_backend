# Taller Mecanico - Backend

API REST para la gestion de un taller mecanico automotriz, construida con **Spring Boot 3.4.3 + JWT**. El frontend Angular compilado puede servirse desde el mismo servicio.

> Repositorio independiente del frontend. El frontend Angular vive en el repositorio `taller-mecanico-frontend`.

## Modo desarrollo (MySQL)

Requisitos:

- JDK 21 o superior (`JAVA_HOME` configurado).
- Maven 3.9+.
- MySQL Server 8.0.
- PowerShell 5.1+.

```bat
REM Arranca MySQL local, crea la base y compila/ejecuta el backend
ejecutar.cmd -Build
```

Detener MySQL:

```bat
detener-mysql.cmd
```

La primera ejecucion inicializa una instancia MySQL local en `.mysql-data` (puerto `3308`) y crea la base `bd_mecanica` con el usuario `taller_app`.

### Variables de entorno

Copie `.env.example` como `.env` y ajuste lo necesario:

| Variable | Default | Descripcion |
| --- | --- | --- |
| `DB_PORT` | `3308` | Puerto MySQL |
| `DB_USER` | `taller_app` | Usuario MySQL |
| `DB_PASSWORD` | `taller123` | Clave MySQL |
| `DB_ROOT_PASSWORD` | vacio | Clave root (si aplica) |
| `CORS_ORIGINS` | `http://localhost:4200` | Origen permitido para Angular en desarrollo |
| `JWT_SECRET` | valor por defecto | Clave HS256 (cambiar en produccion) |
| `APP_ADMIN_USER` | `admin` | Usuario inicial |
| `APP_ADMIN_PASSWORD` | `admin123` | Clave inicial |
| `MAVEN_PATH` | vacio | Ruta a Maven si no esta en el PATH |

`scripts/ejecutar.ps1` carga automaticamente el archivo `.env` si existe.

## Stack

| Componente | Version |
| --- | --- |
| Java | 21 (compilado con `--release 21`) |
| Spring Boot | 3.4.3 |
| Maven | 3.9+ |
| MySQL | 8.0 (desarrollo) |
| Spring Security + OAuth2 Resource Server | JWT HS256 |
| springdoc-openapi | 2.7.0 |
| Lombok | 1.18.48 |

## Estructura de paquetes

```
com.taller.mecanica
├── config        # seguridad, OpenAPI, CORS, datos iniciales
├── controller    # endpoints REST y servido del frontend compilado
├── dto           # request/response, mapper y ErrorResponse
├── exception     # excepciones de negocio y @RestControllerAdvice
├── model         # entidades JPA y enums
├── repository    # interfaces JpaRepository
├── security      # JwtService, UserDetailsService, handlers 401/403
└── service       # logica de negocio y almacenamiento de archivos
```

## Credenciales y URLs

| Recurso | Valor |
| --- | --- |
| Aplicacion | `http://localhost:8080` |
| API | `http://localhost:8080/api` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| Usuario | `admin` |
| Clave | `admin123` |
| Base de datos | `bd_mecanica` |
| Usuario MySQL app | `taller_app` / `taller123` |

El usuario inicial se crea automaticamente solo si la tabla `usuarios` esta vacia.

## Autenticacion

1. `POST /api/auth/login` con `{ "username": "admin", "password": "admin123" }`.
2. La respuesta incluye `token` (JWT, vigencia 480 minutos).
3. Enviar `Authorization: Bearer <token>` en el resto de endpoints.
4. `GET /api/auth/me` devuelve el usuario autenticado.

Rutas publicas: `/api/auth/login`, `/api/health`, `/v3/api-docs/**`, `/swagger-ui/**`, `/uploads/**` y los archivos estaticos del frontend. Todo `/api/**` exige JWT.

## Endpoints principales

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| POST | `/api/auth/login` | Iniciar sesion |
| GET | `/api/auth/me` | Usuario autenticado |
| GET | `/api/health` | Estado del servicio |
| CRUD | `/api/clientes` | Clientes (`?q=` busca por nombre/DNI) |
| GET | `/api/clientes/{id}/vehiculos` | Vehiculos del cliente |
| CRUD | `/api/vehiculos` | Vehiculos (`?clienteId=`, `/placa/{placa}`) |
| CRUD | `/api/mecanicos` | Mecanicos |
| CRUD | `/api/paquetes` | Paquetes con items de plantilla |
| CRUD | `/api/insumos` | Inventario y `/alertas-stock` |
| CRUD | `/api/citas` | Citas, cambio de estado y `/convertir` |
| CRUD | `/api/informes` | Ordenes de trabajo |
| PATCH | `/api/informes/{id}/estado` | Transiciones de estado |
| PATCH | `/api/informes/{id}/escaneos` | Escaneo inicial/final |
| PATCH | `/api/informes/{id}/checklist/{detalleId}` | Actualizar punto del checklist |
| GET | `/api/informes/{id}/reporte` | Reporte agregado (PDF-ready) |
| POST | `/api/informes/{id}/ficha-tecnica` | Subir PDF de ficha tecnica |
| CRUD | `/api/informes/{id}/consumos` | Consumos de insumos (descuenta stock) |
| CRUD | `/api/informes/{id}/adicionales` | Repuestos / mano de obra adicional |
| PATCH | `/api/adicionales/{id}/aprobacion?aprobado=true` | Aprobacion del cliente |
| CRUD | `/api/informes/{id}/pagos` | Adelantos y liquidaciones |
| CRUD | `/api/informes/{id}/evidencias` | Fotos por etapa |
| GET | `/api/reportes/dashboard` | Indicadores del taller |
| GET | `/api/reportes/historial/{placa}` | Historial del vehiculo |
| GET | `/api/reportes/recordatorios` | Recordatorios de mantenimiento |
| GET | `/api/reportes/mecanicos/productividad` | Productividad por mecanico |

## Reglas de negocio implementadas

- Cada orden genera un **checklist de 19 puntos** desde la plantilla del paquete.
- Los items condicionales no admiten `PENDIENTE`: exigen `SOLO_VERIFICADO`, `RELLENADO`, `CAMBIADO` o `NO_REQUERIDO`.
- No se puede pasar a `LISTO_PARA_ENTREGA` ni `ENTREGADO` sin **escaneo final** y sin completar los items de **CORTESIA**.
- Transiciones de orden: `RECEPCIONADO -> EN_PROCESO -> LISTO_PARA_ENTREGA -> ENTREGADO`; `CANCELADO` repone stock; `ENTREGADO` y `CANCELADO` son finales.
- Transiciones de cita: `PENDIENTE <-> CONFIRMADA`, `-> EN_TALLER`, `-> CANCELADA`; una cita cancelada puede volver a `PENDIENTE`.
- Al convertir una cita se valida que vehiculo y paquete coincidan y que no tenga ya una orden.
- Capacidad maxima simultanea de citas configurable (por defecto 3).
- Consumos descuentan stock y se revierten al cancelar la orden.
- Pagos no pueden superar el saldo pendiente.
- Adicionales se cobran solo si estan aprobados y marcados como cobrables.

## Frontend compilado

El backend puede servir el build de Angular desde `src/main/resources/static`. Si cambia el frontend, regenere esos archivos:

```bat
REM En el repositorio taller-mecanico-frontend
npm.cmd run build

REM En este repositorio
scripts\copiar-frontend.ps1
```

Luego recompile el backend con `ejecutar.cmd -Build`.

## Pruebas manuales

Con el backend levantado en modo MySQL:

```bat
smoke-test.cmd
```

El script valida health, 401 sin token, login, `/auth/me`, catalogos, dashboard y un CRUD temporal de cliente.

Tambien puede importar `scripts/api.http` en VS Code (REST Client) o IntelliJ.

## Datos iniciales

En modo desarrollo (`DataInitializer`) se crea:

- Configuracion del taller.
- Paquete `MANTENIMIENTO MENOR GASOLINA` con 19 items.
- 11 insumos iniciales.
- Usuario `admin`.

## Notas academicas

- El proyecto prioriza claridad arquitectonica sobre seguridad avanzada: un solo rol `ADMIN`, sin refresh tokens ni recuperacion de clave.
- Para produccion: cambiar `JWT_SECRET` y la clave de `admin`, usar HTTPS, desactivar `ddl-auto=update` y gestionar migraciones (Flyway/Liquibase).
