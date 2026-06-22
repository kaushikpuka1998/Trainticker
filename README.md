# 🚆 TrainTicker — IRCTC-style Railway Reservation System

A scalable backend railway reservation system inspired by IRCTC, built with **Java 21, Spring Boot, PostgreSQL, Redis, and JPA/Hibernate**. It models the genuinely hard parts of railway booking — dynamic seat allocation, RAC and waiting-list queues, segment-based reservation, partial cancellation with automatic promotion, and Redis-backed PNR caching — as a clean, layered service.

> Group ID: `com.kgstrivers` · Package: `com.kgstrivers.trainticker`

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=spring&logoColor=white)](#)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat-square&logo=postgresql&logoColor=white)](#)
[![Redis](https://img.shields.io/badge/Redis-DD0031?style=flat-square&logo=redis&logoColor=white)](#)

---

## Why this project exists

A single "book a ticket" request in a real railway system touches schedule lookup, per-coach seat availability, berth assignment, PNR generation, and persistence under contention — and when seats run out, it has to fall back to RAC, then waiting list, then re-order those queues correctly on every cancellation. TrainTicker implements that full flow end to end. The goal is correctness and clear design under realistic constraints, not a UI.

## Tech stack

| Technology | Usage |
|---|---|
| Java 21 | Backend |
| Spring Boot | REST APIs |
| Spring Data JPA / Hibernate | ORM & persistence |
| PostgreSQL | Primary database |
| Redis | Distributed PNR caching |
| Lombok | Boilerplate reduction |
| Maven | Build tool |

## Architecture

```
Client
  │  REST
  ▼
Controller ──► Service ──► Repository ──► PostgreSQL
                  │
                  ▼
              Redis cache  (DTO-shaped, not entities)
                  │
                  ▼
       @RestControllerAdvice  (global exception handling)
```

Standard three-layer separation. The two pieces worth calling out — the DTO-shaped cache and the global exception advice — are detailed under [Design decisions](#design-decisions), since that's where the interesting bugs lived.

---

## ✨ Features

### 🚉 Train & route management
- Train creation, coach attachment, and schedule configuration
- Multi-day journeys with ordered stations, arrival/departure timings, distance, and state mapping
- Example route: `HWH → ASN → GAYA → NDLS`

### 🏙 Station reusability
Stations are normalized and reused across trains — `HWH`, `NDLS`, `CSMT` exist exactly once in the database and are referenced by many `route_stations`.

### 📅 Train scheduling
Running days are encoded as a 7-bit mask:

| Mask | Meaning |
|---|---|
| `1111111` | Daily |
| `1111100` | Monday–Friday |
| `0000011` | Weekend |
| `1010100` | Mon, Wed, Fri |

### 🚆 Coaches & automatic seat generation
Supported coach types: `1A`, `2A`, `3A`, `SL`, `2S`, `Pantry`. Seats are auto-generated from coach type.

| Coach type | Confirmed capacity | RAC capacity |
|---|---|---|
| 1A | 24 | 6 |
| 2A | 54 | 12 |
| 3A | 72 | 18 |
| SL | 80 | 20 |
| 2S | 108 | 30 |

### 🎟 Booking system
Multi-passenger booking, PNR generation, journey-based seat allocation, passenger mapping, and segment-based booking — with automatic fallback to RAC and waiting list.

### 🪑 Seat allocation states
`CONFIRMED` · `RAC` · `WAITLIST` · `CANCELLED`

### 🚦 RAC & waiting list
When confirmed seats are full, passengers move to **RAC** (e.g. `RAC-1`, `RAC-2`); when RAC is full, they move to the **waiting list** (`WL-1`, `WL-2`). The system handles automatic numbering and dynamic queue reordering.

### 🔄 Promotion flow
```
CONFIRMED cancelled
        ↓
RAC → CONFIRMED
        ↓
WAITLIST → RAC
        ↓
Reorder RAC / WL queue
```

### 🧠 Segment-based reservation
The same seat is reused across non-overlapping journey segments, mirroring real railway behaviour:

```
Route:  HWH → ASN → GAYA → NDLS
Pax A:  HWH → GAYA
Pax B:  GAYA → NDLS      (same seat, no overlap)
```

### ❌ Cancellation system
- **Full cancellation** — `DELETE /api/v1/bookings/{pnr}`
- **Partial passenger cancellation** — `DELETE /api/v1/bookings/{pnr}/passengers/{passengerId}`

Both trigger automatic RAC promotion, waiting-list promotion, and queue reordering.

### 💰 Fare configuration
Fare per KM, RAC capacity, waiting limit, Tatkal quota, and dynamic-pricing support.

### ⚡ Redis PNR caching
```
PNR request → Redis cache
   ├─ hit  → return response
   └─ miss → fetch DB → store in cache
```
Implemented with Spring Cache (`@Cacheable` / `@CacheEvict`) over Redis, with automatic eviction to keep PNR state consistent after cancellations and promotions.

---

## Design decisions

### 1. DTO-shaped Redis caching (not entities)
Caching JPA entities directly causes serialization failures the moment Hibernate's lazy-loaded associations are touched outside an open session. The cache therefore stores **DTOs** (`TrainDTO`, `RouteStationDTO`, `CoachDTO`, `TrainScheduleDTO`), each built via a static `from(...)` factory that flattens exactly the needed fields — so what lands in Redis is a stable, fully-materialized shape with no lazy proxies.

### 2. Typed exceptions + a single advice layer
Errors are intent-revealing rather than generic runtime failures:
- `ResourceNotFoundException` — train / schedule / PNR not found
- `InvalidBookingException` — malformed or illegal booking request
- `NoSeatsAvailableException` — confirmed, RAC, and WL inventory all exhausted

A `@RestControllerAdvice` maps each to the correct HTTP status and a consistent error body, keeping controllers free of try/catch noise. The project defines its own error-response type to avoid colliding with Spring's built-in `ErrorResponse`.

### 3. Upsert for `CoachTypeConfig` to kill an N+1
Seeding/updating coach-type configuration naively triggers a lookup per row. Config is loaded once into a map keyed by type and reconciled in memory, turning an N+1 read pattern into a single batched pass.

### 4. Automatic queue promotion as a single transaction
On cancellation, the RAC→CONFIRMED, WL→RAC, and reorder steps run together so the queue can never be observed in a half-promoted state.

---

## 🧩 Entity relationships

```
Train ──┬─< RouteStation >── Station
        ├─< Coach ──< Seat
        ├─< TrainSchedule
        └─< Booking ──┬─< Passenger
                      └─< BookedSeat
```

- One `Train` → many `RouteStation`, `Coach`, `TrainSchedule`, `Booking`
- One `Coach` → many `Seat`
- One `Booking` → many `Passenger` and many `BookedSeat`
- Many `RouteStation` → one `Station`

## 🗃 Database tables
`trains` · `stations` · `route_stations` · `train_schedules` · `coaches` · `seats` · `bookings` · `passengers` · `booked_seats` · `coach_type_config`

## 📂 Project structure
```
src/main/java/com/kgstrivers/trainticker
├── controllers
├── services
├── repositories
├── entities
├── dao
├── dto
├── config
└── exceptions
```

---

## 🚀 API reference

### Train
| Method | Path | Purpose |
|---|---|---|
| `POST` | `/api/v1/trains` | Create a train |
| `GET` | `/api/v1/trains/{id}` | Fetch a train |

### Booking
| Method | Path | Purpose |
|---|---|---|
| `POST` | `/api/v1/bookings` | Book a ticket, allocate seats, return PNR |
| `GET` | `/api/v1/bookings/{pnr}` | Get booking by PNR (cache-backed) |
| `DELETE` | `/api/v1/bookings/{pnr}` | Cancel an entire booking |
| `DELETE` | `/api/v1/bookings/{pnr}/passengers/{passengerId}` | Cancel a single passenger |

### Coach configuration
| Method | Path | Purpose |
|---|---|---|
| `POST` | `/api/v1/coach-configs` | Create coach configurations |
| `GET` | `/api/v1/coach-configs` | List all configurations |
| `GET` | `/api/v1/coach-configs/{coachType}` | Get a single configuration |

### 📥 Sample booking request
```json
{
  "trainNumber": "12301",
  "journeyDate": "2026-06-15",
  "sourceStationCode": "HWH",
  "destinationStationCode": "NDLS",
  "classType": "3A",
  "passengers": [
    { "name": "Kaushik Ghosh", "age": 26, "gender": "MALE" },
    { "name": "Rahul Sharma",  "age": 30, "gender": "MALE" }
  ]
}
```

### 📤 Sample responses

**Confirmed**
```json
{
  "pnr": "8745632198",
  "bookingStatus": "CONFIRMED",
  "passengers": [
    { "passengerName": "Kaushik Ghosh", "coachNumber": "B1", "seatNumber": "21", "bookingStatus": "CONFIRMED" }
  ]
}
```

**RAC**
```json
{
  "pnr": "8745632198",
  "bookingStatus": "RAC",
  "passengers": [
    { "passengerName": "Kaushik Ghosh", "coachNumber": null, "seatNumber": "RAC-4", "bookingStatus": "RAC" }
  ]
}
```

**Waitlist**
```json
{
  "pnr": "8745632198",
  "bookingStatus": "WAITLIST",
  "passengers": [
    { "passengerName": "Kaushik Ghosh", "coachNumber": null, "seatNumber": "WL-12", "bookingStatus": "WAITLIST" }
  ]
}
```

---

## ⚙️ How to run

### Prerequisites
- JDK 21+ · Maven 3.9+ · PostgreSQL · Redis

### 1. Clone
```bash
git clone https://github.com/kaushikpuka1998/TrainTicker.git
cd TrainTicker
```

### 2. Configure PostgreSQL — `application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/trainticker
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Configure Redis
```properties
spring.cache.type=redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### 4. Run Redis & the app
```bash
redis-server
mvn spring-boot:run
```
Service starts on `http://localhost:8080`.

---

## 📌 Roadmap
- [ ] Redis seat locking + distributed locking (prevent double-booking under load)
- [ ] Kafka booking queue
- [ ] Tatkal booking & AI-based demand pricing
- [ ] Berth preference (lower / middle / upper)
- [ ] JWT authentication
- [ ] Email / SMS notifications
- [ ] Payment integration & refund processing
- [ ] Integration tests across the full booking flow

## 🧠 Concepts demonstrated
Database normalization · JPA cascades & bidirectional mapping · dynamic seat allocation · segment-based reservation · RAC/WL promotion logic · Redis caching & eviction strategy · automatic queue promotion · partial-cancellation workflow · passenger-level reservation state · production-style API design.

## 👨‍💻 Author
**Kaushik Ghosh** — Software Engineer · Backend Developer · Java & Spring Boot
[LinkedIn](https://www.linkedin.com/in/kgstrivers) · [GitHub](https://github.com/kaushikpuka1998)
