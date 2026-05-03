# Future Architectural Improvements

## 1. Input Sanitization and corrective error responses
*  **Sanitization**: Add a mechanism to fail fast when an incoming request has incorrect parameter values.
*  **Error responses**: Error responses have to be improved to return a generic, helpful JSON message to the user.

## 2. Observability & Monitoring
*   **Debugging**: Add a correlation-id implementation to persist throughout the request lifecycle.
This should be logged as well.
*   **Structured Logging**: Replace `java.util.logging` with SLF4J + Logback/Log4j2 for JSON-formatted logs suitable for analytics.
*   **Metrics**: Integrate **Micrometer** to track metrics like `reservation_success_rate`, `allocation_latency`, and `current_bus_occupancy`.

## 3. API Hardening
*   **Rate Limiting**: Implement a Token Bucket algorithm (as outlined in `Security.md`) to protect the system from burst traffic and DDoS attempts.

## 4. Persistence Tier
*   **SQL Integration**: Migrate the `BusRepository` to use Hibernate/JPA with a PostgreSQL backend. This ensures data persistence across server restarts and provides transactional integrity.
