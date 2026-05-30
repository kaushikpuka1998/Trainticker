# 🚆 TrainTicker - IRCTC Like Railway Reservation System

TrainTicker is a scalable backend railway reservation system inspired by IRCTC, built using Java, Spring Boot, PostgreSQL, Redis, and JPA/Hibernate.

The system supports:

* Train Management
* Route Management
* Coach & Seat Generation
* Train Scheduling
* Ticket Booking
* Passenger Management
* RAC & Waiting List
* Dynamic Seat Allocation
* Partial Cancellation
* Automatic RAC/WL Promotion
* Redis-based PNR Caching
* Availability Search
* Fare Configuration

---

# ✨ Features

## 🚉 Train Management

Supports:

* Train creation
* Route mapping
* Coach attachment
* Train schedules
* Running day configuration

---

## 🛤 Route Management

Each train supports:

* Multi-day journeys
* Station ordering
* Arrival/departure timings
* Distance calculation
* State mapping

Example:

```text
HWH -> ASN -> GAYA -> NDLS
```

---

## 🏙 Station Reusability

Stations are normalized and reused across trains.

Example:

* HWH
* NDLS
* CSMT

exist only once in database.

---

## 📅 Train Scheduling

Supports:

* Daily trains
* Weekday trains
* Weekend trains
* Custom schedules

Example:

```text
1111111 -> Daily
1111100 -> Monday-Friday
0000011 -> Weekend
1010100 -> Monday, Wednesday, Friday
```

---

## 🚆 Coach Management

Supported coach types:

* 1A
* 2A
* 3A
* SL
* 2S
* Pantry

---

## 💺 Automatic Seat Generation

Seats are auto-generated based on coach type.

| Coach Type | Confirmed Capacity | RAC Capacity |
| ---------- | ------------------ | ------------ |
| 1A         | 24                 | 6            |
| 2A         | 54                 | 12           |
| 3A         | 72                 | 18           |
| SL         | 80                 | 20           |
| 2S         | 108                | 30           |

---

## 🎟 Booking System

Supports:

* Multi-passenger booking
* PNR generation
* Journey-based booking
* Seat allocation
* Passenger mapping
* Segment-based booking
* RAC allocation
* WAITLIST allocation

---

## 👤 Passenger Management

Each booking can contain multiple passengers.

Passenger details:

* Name
* Age
* Gender

Each passenger gets:

* Individual seat
* RAC/WL number
* Booking status

---

## 🪑 Seat Allocation

Supported statuses:

* CONFIRMED
* RAC
* WAITLIST
* CANCELLED

---

## 🚦 RAC (Reservation Against Cancellation)

When confirmed seats are full:

* passengers move to RAC
* RAC passengers receive RAC numbers
* cancellation automatically promotes RAC → CONFIRMED
* WAITLIST passengers automatically move to RAC

Example:

```text
RAC-1
RAC-2
```

TrainTicker supports automatic queue reordering after:

* cancellation
* RAC promotion
* WAITLIST promotion

---

## ⏳ Waiting List

When:

* confirmed seats full
* RAC full

Passengers move to waiting list.

Example:

```text
WL-1
WL-2
WL-3
```

Features:

* automatic WL numbering
* automatic WL → RAC promotion
* dynamic waiting list reordering

---

## ❌ Cancellation System

TrainTicker supports:

* Full Booking Cancellation
* Partial Passenger Cancellation
* Automatic RAC Promotion
* Automatic WAITLIST Promotion
* Queue Reordering

### Full Cancellation

```http
DELETE /api/v1/bookings/{pnr}
```

Cancels entire booking.

---

### Partial Passenger Cancellation

```http
DELETE /api/v1/bookings/{pnr}/passengers/{passengerId}
```

Cancels only selected passenger.

---

## 🔄 Promotion Flow

```text
CONFIRMED Cancelled
        ↓
RAC → CONFIRMED
        ↓
WAITLIST → RAC
        ↓
Reorder RAC/WL Queue
```

---

## 🧠 Segment-Based Reservation

TrainTicker supports journey segment allocation similar to real railway systems.

Example:

```text
HWH -> ASN -> GAYA -> NDLS
```

Passenger A:

```text
HWH -> GAYA
```

Passenger B:

```text
GAYA -> NDLS
```

Same seat can be reused without overlap.

---

## 💰 Fare Configuration

Supports:

* Fare per KM
* RAC capacity
* Waiting limit
* Tatkal quota
* Dynamic pricing support

---

# ⚡ Redis PNR Caching

TrainTicker uses Redis caching for high-speed PNR retrieval.

Flow:

```text
PNR Request
    ↓
Redis Cache
    ↓
Cache Hit → Return Response
    ↓
Cache Miss → Fetch DB → Store Cache
```

Features:

* Redis-based caching
* Automatic cache eviction
* Reduced database load
* Faster PNR fetch

Technologies:

* Spring Cache
* Redis
* CacheEvict
* Cacheable

---

# 🏗 Tech Stack

| Technology      | Usage                 |
| --------------- | --------------------- |
| Java 21         | Backend               |
| Spring Boot     | REST APIs             |
| Spring Data JPA | ORM                   |
| Hibernate       | Persistence           |
| PostgreSQL      | Database              |
| Redis           | Distributed Caching   |
| Lombok          | Boilerplate Reduction |
| Maven           | Build Tool            |

---

# 📂 Project Structure

```text
src/main/java/com/kgstrivers/trainticker
│
├── Controllers
├── Services
├── Repositories
├── Entities
├── DAO
├── DTO
├── Config
└── Exceptions
```

---

# 🧩 Entity Relationships

## Train

* One Train → Many RouteStations
* One Train → Many Coaches
* One Train → Many Schedules
* One Train → Many Bookings

## Coach

* One Coach → Many Seats

## Booking

* One Booking → Many Passengers
* One Booking → Many BookedSeats

## Passenger

* One Passenger → Many Bookings

## RouteStation

* Many RouteStations → One Station

---

# 🗃 Database Tables

Core Tables:

* trains
* stations
* route_stations
* train_schedules
* coaches
* seats
* bookings
* passengers
* booked_seats
* coach_type_config

---

# 🚀 APIs

## 🚆 Train APIs

### Create Train

```http
POST /api/v1/trains
```

### Get Train

```http
GET /api/v1/trains/{id}
```

---

## 🎟 Booking APIs

### Book Ticket

```http
POST /api/v1/bookings
```

### Get Booking By PNR

```http
GET /api/v1/bookings/{pnr}
```

### Cancel Booking

```http
DELETE /api/v1/bookings/{pnr}
```

### Cancel Passenger

```http
DELETE /api/v1/bookings/{pnr}/passengers/{passengerId}
```

---

## ⚙️ Coach Configuration APIs

### Create Coach Configurations

```http
POST /api/v1/coach-configs
```

### Get All Configurations

```http
GET /api/v1/coach-configs
```

### Get Single Configuration

```http
GET /api/v1/coach-configs/{coachType}
```

---

# 📥 Sample Booking Request

```json
{
  "trainNumber": "12301",
  "journeyDate": "2026-06-15",
  "sourceStationCode": "HWH",
  "destinationStationCode": "NDLS",
  "classType": "3A",
  "passengers": [
    {
      "name": "Kaushik Ghosh",
      "age": 26,
      "gender": "MALE"
    },
    {
      "name": "Rahul Sharma",
      "age": 30,
      "gender": "MALE"
    }
  ]
}
```

---

# 📤 Sample Responses

## CONFIRMED

```json
{
  "pnr": "8745632198",
  "bookingStatus": "CONFIRMED",
  "passengers": [
    {
      "passengerName": "Kaushik Ghosh",
      "coachNumber": "B1",
      "seatNumber": "21",
      "bookingStatus": "CONFIRMED"
    }
  ]
}
```

---

## RAC

```json
{
  "pnr": "8745632198",
  "bookingStatus": "RAC",
  "passengers": [
    {
      "passengerName": "Kaushik Ghosh",
      "coachNumber": null,
      "seatNumber": "RAC-4",
      "bookingStatus": "RAC"
    }
  ]
}
```

---

## WAITLIST

```json
{
  "pnr": "8745632198",
  "bookingStatus": "WAITLIST",
  "passengers": [
    {
      "passengerName": "Kaushik Ghosh",
      "coachNumber": null,
      "seatNumber": "WL-12",
      "bookingStatus": "WAITLIST"
    }
  ]
}
```

---

# ⚙️ How To Run

## Clone Repository

```bash
git clone <your-github-url>
```

---

## Configure PostgreSQL

Update:

```properties
application.properties
```

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/trainticker

spring.datasource.username=postgres

spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
```

---

## Configure Redis

```properties
spring.cache.type=redis

spring.data.redis.host=localhost

spring.data.redis.port=6379
```

---

## Run Redis

```bash
redis-server
```

---

## Run Application

```bash
mvn spring-boot:run
```

---

# 📌 Upcoming Features

* Tatkal Booking
* Dynamic Pricing
* Seat Preference
* Lower/Middle/Upper Berth
* Redis Seat Locking
* Kafka Booking Queue
* Distributed Locking
* JWT Authentication
* Email/SMS Notifications
* Payment Integration
* Refund Processing
* AI Based Demand Pricing

---

# 🧠 Design Concepts Used

* Database Normalization
* Entity Relationships
* JPA Cascade Operations
* Bidirectional Mapping
* Dynamic Seat Allocation
* Segment-Based Reservation
* RAC/WL Promotion Logic
* Schedule Management
* Redis Distributed Caching
* Cache Eviction Strategy
* Automatic Queue Promotion
* Partial Cancellation Workflow
* Passenger-level Reservation State
* Production-style API Design

---

# 📖 Learning Outcomes

This project demonstrates:

* Real-world railway reservation modeling
* Advanced JPA relationship handling
* Scalable backend architecture
* Production-grade booking workflow
* Complex seat allocation systems
* RAC & Waiting List handling
* Redis caching strategies
* Queue-based reservation systems
* Automatic RAC/WL promotion logic
* Partial booking cancellation handling
* Cache invalidation techniques

---

# 👨‍💻 Author

Kaushik Ghosh

Software Engineer | Backend Developer | Java & Spring Boot Enthusiast
