# Healthcheck

Healthcheck is a Spring Boot application for registering HTTP services, checking their availability on a schedule, and reviewing health-check history. It stores service definitions and check results in PostgreSQL and exposes a REST API protected by HTTP Basic authentication.

## How it works

1. Register a service with its name, URL, check interval (seconds), and critical flag.
2. A scheduled poller finds services whose last check is due. The default scheduler polling interval is one second; each service's configured interval controls how often it is checked.
3. The application sends an HTTP `GET` to each due service. A 2xx response is recorded as `UP`; a non-2xx response or an HTTP error is recorded as `DOWN`.
4. Check results, including status and response time, are saved. The history API reports uptime percentage, average response time, and up to 50 recent checks for a requested time range.

## Requirements

- Java 25
- PostgreSQL, with a database named `healthcheck` (or a different database configured through `DB_URL`)
- The included Maven Wrapper (`./mvnw`); a separate Maven installation is not required

## Configuration and run

The default PostgreSQL connection is `jdbc:postgresql://localhost:5432/healthcheck`. Create that database in your PostgreSQL instance, or set the connection values for your environment.

The application optionally imports a root-level `.env` file. Start with the example:

```bash
cp .env.example .env
```

Edit `.env` to match your PostgreSQL connection and choose application credentials. The example values are for local development only; use strong credentials outside local development.

```dotenv
DB_URL=jdbc:postgresql://localhost:5432/healthcheck
DB_USERNAME=postgres
DB_PASSWORD=postgres

APP_USERNAME=monitor
APP_PASSWORD=monitor

MONITORING_DEPENDENCY_URL=https://httpbin.org/status/200
```

`DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` configure PostgreSQL. `APP_USERNAME` and `APP_PASSWORD` configure the HTTP Basic account. `MONITORING_DEPENDENCY_URL` sets the external dependency checked by the Actuator health endpoint. Defaults are also defined in `src/main/resources/application.yaml`.

Run the application from the project root:

```bash
./mvnw spring-boot:run
```

The server listens on port `8090` by default. To build a runnable JAR instead:

```bash
./mvnw package
java -jar target/healthcheck-0.0.1-SNAPSHOT.jar
```

## API

Service registry and history endpoints require HTTP Basic authentication using the configured application credentials. The examples below use the local values from `.env.example`; replace them if you changed the credentials.

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/api/services` | List registered services |
| `GET` | `/api/services/{id}` | Get a service by ID |
| `POST` | `/api/services` | Register a service |
| `PUT` | `/api/services` | Update a service; include its ID in the body |
| `DELETE` | `/api/services/{id}` | Delete a service |
| `GET` | `/api/services/{id}/history?from={instant}&to={instant}` | Get check history and aggregates for an ISO-8601 time range |

Create a service (the `checkInterval` is in seconds and must be at least 5):

```bash
curl -u monitor:monitor \
  -H 'Content-Type: application/json' \
  -d '{"name":"payments","url":"https://example.com/health","checkInterval":30,"critical":true}' \
  http://localhost:8090/api/services
```

Read the service list and check history (use UTC ISO-8601 instants):

```bash
curl -u monitor:monitor http://localhost:8090/api/services
curl -u monitor:monitor --get \
  --data-urlencode 'from=2026-10-30T00:00:00Z' \
  --data-urlencode 'to=2026-10-30T23:59:59Z' \
  http://localhost:8090/api/services/1/history
```

The history response includes `uptimePercentage`, `responseTimeAvg`, and a `checks` list with each check's timestamp, `UP`/`DOWN` status, and response time in milliseconds. The API validates service names and URLs; invalid requests return an error response.

### Health, metrics, and API documentation

- `GET http://localhost:8090/actuator/health` is available without authentication. It reports application health, including the database and configured external HTTP dependency.
- `GET http://localhost:8090/actuator/metrics` requires HTTP Basic authentication.
- Swagger UI is available at `http://localhost:8090/swagger-ui/index.html`; the OpenAPI document is at `http://localhost:8090/v3/api-docs`. These documentation routes are available without authentication.

## Project structure

```text
src/
├── main/
│   ├── java/llc/devlab/app/
│   │   ├── config/       # Security, HTTP client, properties, and OpenAPI configuration
│   │   ├── controller/   # REST endpoints for service registry and health history
│   │   ├── enums/        # Health status values
│   │   ├── exception/    # API exceptions and global error handling
│   │   ├── health/       # Database and external-dependency health indicators
│   │   ├── mapper/       # DTO/entity mapping
│   │   ├── model/        # API DTOs and JPA entities
│   │   ├── repository/   # Persistence and due-check queries
│   │   ├── service/      # Registry, scheduled checks, and history logic
│   │   └── HealthcheckApplication.java
│   └── resources/
│       └── application.yaml
└── test/
    ├── java/             # Application, health endpoint, and registry integration tests
    └── resources/
        └── application.yaml  # H2 test database and test configuration
```

## Tests

Run the test suite with:

```bash
./mvnw test
```

Tests use the in-memory H2 database configured under `src/test/resources` and local test fixtures for the external health dependency; a local PostgreSQL instance is not required for the test suite.
