# Bus Ticket Reservation System

This repository contains the native Java REST API for managing bus seat reservations across multi-segment journeys AND concurrent API client to invoke the API (testting purposes).

## 🏗 Architecture Overview

The API implementation follows a **Layered Architecture**

### 1. Web Layer (`controller`)
*   **BusReservationServlet**: Acts as the transport adapter. It handles HTTP request parsing, JSON marshaling, and converts protocol-specific data into domain-friendly requests.

### 2. Service Layer (`service`)
*   **TicketService (Application Service)**: Orchestrates the business workflow (Pricing -> Seating -> Ticket Generation -> Logging).
*   **Seating/Pricing Services (Domain Services)**: Provide specialized logic for specific domain problems.

### 3. Domain Layer (`domain`)
*   **Bus (Entity)**: A domain model that encapsulates the seat map state and the core allocation algorithm.
*   **Location/DTOs**: Immutable records and enums representing the domain state.

### 4. Persistence Layer (`repository`)
*   **BusRepository**: Manages in-memory storage of Bus entities. Designed to be easily swapped for a database-backed repository in the future.

## 📖 API Specification

### 1. Check Availability
`GET /api/v1/tickets/availability`

**Query Parameters:**
* `origin` (string): Starting location (A, B, C, D)
* `destination` (string): Ending location (A, B, C, D)
* `passengers` (int): Number of seats required

**Response (200 OK):**
```json
{
  "origin": "A",
  "destination": "B",
  "passengers": 2,
  "price": 100.00,
  "available": true,
  "quoteId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### 2. Reserve Tickets
`POST /api/v1/tickets/reservations`

**Request Body:**
```json
{
  "origin": "A",
  "destination": "C",
  "passengers": 2,
  "paymentAmount": 200.00
}
```

**Response (200 OK):**
```json
{
  "ticketNumber": "TICK-1001",
  "bookedSeats": ["1A", "1B"],
  "origin": "A",
  "destination": "C",
  "departureTime": "10:00 AM",
  "arrivalTime": "02:00 PM",
  "totalPrice": 200.00
}
```

**Error Codes:**
* `402 Payment Required`: The price in the request does not match the server's calculation.
* `409 Conflict`: The requested seats were taken by another thread between the availability check and the reservation.
* `400 Bad Request`: Missing or malformed JSON data.

---

## 🔒 Concurrency & Thread Safety

To handle high-concurrency (multiple threads booking seats simultaneously), a **Pessimistic Locking** strategy has been implemented.

### Entity-Level Locking
`ReentrantReadWriteLock` has been implemented directly inside the **Bus Entity**.

*   **Why at the Entity Level?**
    *   **Granularity**: Locking at the entity level allows "Bus-A" and "Bus-B" to be updated in parallel. A global lock would serialize all bookings, creating a bottleneck.
    *   **Encapsulation**: The Entity is responsible for its own integrity. By placing the lock inside the `Bus` class, it is ensured that the internal `BitSet` can never be corrupted, regardless of who calls it.
*   **Read/Write Optimization**:
    *   **ReadLock**: Allows multiple concurrent threads to check availability.
    *   **WriteLock**: Ensures exclusive access during the seat allocation process to prevent overbooking (The "Lost Update" problem).

---

## 🚀 Key Technical Features

### Segment-Based Booking
Instead of a simple boolean for each seat, implementation uses a `BitSet` to represent journey segments (A-B, B-C, C-D). This allows a single seat to be sold to multiple passengers for different parts of the same journey (e.g., Seat 1A can be sold to Passenger X for A-B and Passenger Y for B-D).

### Concurrent invocation safety
Explained above

### In-memory persistence
API uses a concurrent hashmap to persist booking data and seat data. Hence true persistence is not achieved.
Data will reset on every re-deployment.

---

## 🛠 Tech Stack
*   **Core**: Java 23
*   **API**: Java Servlet API 4.0
*   **JSON**: Jackson Databind
*   **Build**: Maven (Multi-module)

---

## 🚀 How to Run

### Prerequisites
*   Java 17 or higher
*   Maven 3.8+
*   A Servlet Container (e.g., Apache Tomcat 9+)

### 1. Build the Project
Run the following command from the root directory to compile all modules and run tests:
```bash
mvn clean install
```

### 2. Start the Server
*   Locate the generated WAR file: `bus-reservation-server/target/bus-reservation-server.war`.
*   Deploy this WAR to your preferred Servlet Container (e.g., copy to Tomcat's `webapps/` folder).
*   The API will be available at: `http://localhost:8080/bus-reservation-server/api/v1/tickets/`

### 3. Run the Simulation Client
The client is packaged as a standalone "fat" JAR. Run it using:
```bash
java -jar bus-reservation-client/target/bus-reservation-client-1.0.0.jar
```

---

## 🛡 Security
A comprehensive security implementation plan (Authentication, Rate Limiting) is documented in [SECURITY.md](https://github.com/sachithKay/bus-reservation-app/blob/main/SECURITY.md).

## 📈 Improvement Roadmap
Future architectural evolutions (Optimistic Locking, Distributed state) are documented in [IMPROVEMENTS.md](https://github.com/sachithKay/bus-reservation-app/blob/main/IMPROVEMENTS.md).
