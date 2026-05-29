# 🚆 TrainTicker - IRCTC Like Railway Reservation System

TrainTicker is a scalable backend railway reservation system inspired by IRCTC, built using Java, Spring Boot, PostgreSQL, and JPA/Hibernate.

The system supports:

* Train Management
* Route Management
* Coach & Seat Generation
* Train Scheduling
* Ticket Booking
* Passenger Management
* RAC & Waiting List
* Dynamic Seat Allocation
* Availability Search
* Fare Configuration

---

# ✨ Features

# 🚉 Train Management

Supports:

* Train creation
* Route mapping
* Coach attachment
* Train schedules
* Running day configuration

---

# 🛤 Route Management

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

# 🏙 Station Reusability

Stations are normalized and reused across trains.

Example:

* HWH
* NDLS
* CSMT

exist only once in database.

---

# 📅 Train Scheduling

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

# 🚆 Coach Management

Supported coach types:

* 1A
* 2A
* 3A
* SL
* 2S
* Pantry

---

# 💺 Automatic Seat Generation

Seats are auto-generated based on coach type.

| Coach Type | Confirmed Capacity |
| ---------- | ------------------ |
| 1A         | 24                 |
| 2A         | 54                 |
| 3A         | 72                 |
| SL         | 80                 |
| 2S         | 108                |

---

# 🎟 Booking System

Supports:

* Multi-passenger booking
* PNR generation
* Journey-based booking
* Seat allocation
* Passenger mapping
* Segment-based booking

---

# 👤 Passenger Management

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

# 🪑 Seat Allocation

Supported statuses:

* CONFIRMED
* RAC
* WAITING
* CANCELLED

---

# 🚦 RAC (Reservation Against Cancellation)

When confirmed seats are full:

* passengers move to RAC
* RAC passengers receive RAC number
* cancellation promotes RAC → CONFIRMED

Example:

```text
RAC-1
RAC-2
```

---

# ⏳ Waiting List

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

---

# 🧠 Segment-Based Reservation

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

# 💰 Fare Configuration

Supports:

* Fare per KM
* RAC capacity
* Waiting limit
* Tatkal quota
* Dynamic pricing support

---

# 🏗 Tech Stack

| Technology      | Usage                 |
| --------------- | --------------------- |
| Java 21         | Backend               |
| Spring Boot     | REST APIs             |
| Spring Data JPA | ORM                   |
| Hibernate       | Persistence           |
| PostgreSQL      | Database              |
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

# Train

* One Train → Many RouteStations
* One Train → Many Coaches
* One Train → Many Schedules
* One Train → Many Bookings

# Coach

* One Coach → Many Seats

# Booking

* One Booking → Many Passengers
* One Booking → Many BookedSeats

# Passenger

* One Passenger → One Allocated Seat

# RouteStation

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

# 🚆 Train APIs

## Create Train

```http
POST /api/v1/trains
```

## Get Train

```http
GET /api/v1/trains/{id}
```

---

# 🎟 Booking APIs

## Book Ticket

```http
POST /api/v1/bookings
```

## Get Booking By PNR

```http
GET /api/v1/bookings/{pnr}
```

---

# ⚙️ Coach Configuration APIs

## Create Coach Configurations

```http
POST /api/v1/coach-configs
```

## Get All Configurations

```http
GET /api/v1/coach-configs
```

## Get Single Configuration

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

# 📤 Sample Response

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

## WAITING

```json
{
  "pnr": "8745632198",

  "bookingStatus": "WAITING",

  "passengers": [
    {
      "passengerName": "Kaushik Ghosh",
      "coachNumber": null,
      "seatNumber": "WL-12",
      "bookingStatus": "WAITING"
    }
  ]
}
```

---

# ⚙️ How To Run

# Clone Repository

```bash
git clone <your-github-url>
```

---

# Configure PostgreSQL

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

# Run Application

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
* Cancellation & Refund
* JWT Authentication
* Email/SMS Notifications
* Payment Integration
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

---

# 👨‍💻 Author

Kaushik Ghosh

Software Engineer | Backend Developer | Java & Spring Boot Enthusiast
